package com.imin.newprinter.demo.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hy
 * @date: 2025/4/17
 * @description:
 */
@SuppressLint("MissingPermission")
public class WifiScannerHelper {
    private static final String TAG = "WifiScannerHelper";
    private static final int DEFAULT_SIGNAL_LEVELS = 5;
    private static final int REQUEST_CODE_LOCATION = 1001;

    private final Context context;
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private OnScanResultsListener scanResultsListener;

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
        boolean scanStarted = wifiManager.startScan();
        if (!scanStarted) {
            notifyScanFailed();
        }
    }

    // ================== 广播接收器管理 ==================
    private void registerReceiver() {
        if (wifiScanReceiver == null) {
            wifiScanReceiver = new WifiScanReceiver();
            IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
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
                boolean isFresh = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false
                );
                processScanResults(isFresh);
            }
        }
    }

    private void processScanResults(boolean isFresh) {
        List<ScanResult> results = wifiManager.getScanResults();
        List<ScanResult> filteredResults = filterValidNetworks(results);
        notifyScanResults(filteredResults, isFresh);
    }

    private List<ScanResult> filterValidNetworks(List<ScanResult> rawResults) {
        List<ScanResult> validResults = new ArrayList<>();
        for (ScanResult result : rawResults) {
            if (isValidNetwork(result)) {
                validResults.add(result);
            }
        }
        return validResults;
    }

    private boolean isValidNetwork(ScanResult result) {
        return result.SSID != null
                && !result.SSID.isEmpty()
                && !"<unknown ssid>".equals(result.SSID);
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
        unregisterReceiver();
        scanResultsListener = null;
    }

    // ================== 回调通知 ==================
    public interface OnScanResultsListener {
        void onResultsReceived(List<ScanResult> results, boolean isFreshScan);
        void onPermissionRequired();
        void onPermissionDenied();
        void onWifiDisabled();
        void onScanFailed();
    }

    public void setScanResultsListener(OnScanResultsListener listener) {
        this.scanResultsListener = listener;
    }

    private void notifyScanResults(List<ScanResult> results, boolean isFresh) {
        if (scanResultsListener != null) {
            scanResultsListener.onResultsReceived(results, isFresh);
        }
    }

    private void notifyPermissionRequired() {
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
}
