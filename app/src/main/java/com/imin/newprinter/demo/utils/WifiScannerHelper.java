package com.imin.newprinter.demo.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("MissingPermission")
public class WifiScannerHelper {
    private static final String TAG = "PrintDemo_WifiScannerHelper";
    private static final int DEFAULT_SIGNAL_LEVELS = 5;
    private static final int REQUEST_CODE_LOCATION = 1001;

    private final Context context;
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private OnScanResultsListener scanResultsListener;
    private volatile boolean isScanning = false;

    public WifiScannerHelper(Context context) {
        this.context = context.getApplicationContext();
        initWifiManager();
    }

    // ================== 初始化与基础配置 ==================
    private void initWifiManager() {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    // ================== 权限管理 ==================
    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestLocationPermission() {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION
            );
        } else {
            Log.e(TAG, "Context is not an Activity, cannot request permission");
        }
    }

    public void handlePermissionResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                notifyPermissionDenied();
            }
        }
    }

    // ================== WiFi 扫描控制 ==================
    public void startScan() {
        if (!checkLocationPermission()) {
            notifyPermissionRequired();
            return;
        }

        if (wifiManager == null) {
            Log.e(TAG, "WifiManager is null");
            notifyScanFailed();
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            notifyWifiDisabled();
            return;
        }

        registerReceiver();
        isScanning = true;
        boolean scanStarted = wifiManager.startScan();
        if (!scanStarted) {
            isScanning = false;
            unregisterReceiver();
            notifyScanFailed();
        }
    }

    public void stopScan() {
        isScanning = false;
        unregisterReceiver();
    }

    // ================== 广播接收器管理 ==================
    private void registerReceiver() {
        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            context.registerReceiver(wifiScanReceiver, intentFilter);
        }
    }

    private void unregisterReceiver() {
        if (wifiScanReceiver != null) {
            try {
                context.unregisterReceiver(wifiScanReceiver);
                wifiScanReceiver = null;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Receiver not registered: " + e.getMessage());
            }
        }
    }

    // ================== 扫描结果处理 ==================
    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                final boolean isFresh = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false
                );
                final List<ScanResult> results = wifiManager.getScanResults();
                // 使用子线程处理扫描结果
                new Thread(() -> {
                    List<ScanResult> filteredResults = filterValidNetworks(results);
                    // 切换到主线程回调结果
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (isScanning && scanResultsListener != null) {
                            scanResultsListener.onResultsReceived(filteredResults, isFresh);
                        }
                    });
                }).start();
            }else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){

                handleWifiStateChange(intent,context);
            }
        }
    }

    private void handleWifiStateChange(Intent intent, Context context) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
               // Toast.makeText(context, "WiFi is enabled", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Receiver not wifi连接: ");
                notifyWifiConnectStatus(true);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                //wifi断开
                Log.e(TAG, "Receiver not wifi断开: ");
                notifyWifiConnectStatus(false);
                //Toast.makeText(context, "WiFi is disabled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

//    private List<ScanResult> filterValidNetworks(List<ScanResult> rawResults) {
//        List<ScanResult> validResults = new ArrayList<>();
//        for (ScanResult result : rawResults) {
//            if (isValidNetwork(result)) {
//                validResults.add(result);
//            }
//        }
//        return validResults;
//    }

    // 在原有代码中添加以下方法

    private List<ScanResult> filterDuplicatesByBand(List<ScanResult> results) {
        Map<String, ScanResult> bestResults = new HashMap<>();

        for (ScanResult result : results) {
            // 生成组合键：SSID + 频段分类
            String band = getFrequencyBand(result.frequency);
            String compositeKey = result.SSID + "_" + band;
//            Log.d(TAG,
//                    "WiFi结果  名称：" + result.SSID +
//                            " 频段：" + getFrequencyBand(result.frequency) +
//                            " 强度：" + result.level + "dBm"
//            );
            // 保留信号最强的结果
            ScanResult existing = bestResults.get(compositeKey);
            if (existing == null || existing.level < result.level) {
                bestResults.put(compositeKey, result);
            }
        }

        return new ArrayList<>(bestResults.values());
    }

    public String getFrequencyBand(int frequency) {
        if (frequency >= 2400 && frequency <= 2500) {
            return "2.4GHz";
        } else if (frequency >= 4900 && frequency <= 5900) {
            return "5GHz";
        } else if (frequency >= 5925 && frequency <= 7125) { // WiFi 6E
            return "6GHz";
        } else {
            return String.valueOf(frequency);
        }
    }

    // 修改现有的过滤方法
    private List<ScanResult> filterValidNetworks(List<ScanResult> rawResults) {
        List<ScanResult> validResults = new ArrayList<>();
        for (ScanResult result : rawResults) {
            if (isValidNetwork(result)) {
                validResults.add(result);
            }
        }
        // 添加频段过滤
        return filterDuplicatesByBand(validResults);
    }


    private boolean isValidNetwork(ScanResult result) {
        return result.SSID != null
                && !result.SSID.isEmpty()
                && !"<unknown ssid>".equals(result.SSID);
    }

    private boolean isOpenNetwork(ScanResult result) {
        final String caps = result.capabilities;
        // 匹配常见加密类型标识
        return !caps.matches(".*WEP|PSK|EAP|SAE.*");
    }


    // ================== 工具方法 ==================
    public static int convertSignalToLevel(int rssi) {
        return WifiManager.calculateSignalLevel(rssi, DEFAULT_SIGNAL_LEVELS);
    }

    public static int convertSignalToPercentage(int rssi) {
        int percentage = (int) ((rssi + 100.0) * 2.0);
        return Math.max(0, Math.min(percentage, 100));
    }

    // ================== 资源释放 ==================
    public void release() {
        stopScan();
        scanResultsListener = null;
    }

    // ================== 回调通知 ==================
    public interface OnScanResultsListener {
        void onResultsReceived(List<ScanResult> results, boolean isFreshScan);
        void onPermissionRequired();
        void onPermissionDenied();
        void onWifiDisabled();
        void onScanFailed();
        void onWifiConnectStatus(boolean b);
    }

    public void setScanResultsListener(OnScanResultsListener listener) {
        this.scanResultsListener = listener;
    }

    private void notifyScanResults(List<ScanResult> results, boolean isFresh) {
        if (scanResultsListener != null) {
            scanResultsListener.onResultsReceived(results, isFresh);
        }
    }

    public void notifyPermissionRequired() {
        if (scanResultsListener != null) {
            scanResultsListener.onPermissionRequired();
        }
    }

    private void notifyPermissionDenied() {
        if (scanResultsListener != null) {
            scanResultsListener.onPermissionDenied();
        }
    }

    private void notifyWifiDisabled() {
        if (scanResultsListener != null) {
            scanResultsListener.onWifiDisabled();
        }
    }

    private void notifyScanFailed() {
        if (scanResultsListener != null) {
            scanResultsListener.onScanFailed();
        }
    }

    private void notifyWifiConnectStatus(boolean b) {
        if (scanResultsListener != null) {
            scanResultsListener.onWifiConnectStatus(b);
        }
    }
}