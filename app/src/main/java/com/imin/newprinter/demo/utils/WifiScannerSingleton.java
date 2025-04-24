package com.imin.newprinter.demo.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class WifiScannerSingleton {
    private static final String TAG = "PrintDemo_WifiScannerSingleton";
    private static volatile WifiScannerSingleton instance;
    private final Context context;
    private WifiScannerHelper wifiScannerHelper;
    private WifiListListener wifiListListener;
    private boolean isScanning = false;

    private WifiScannerSingleton(Context context) {
        this.context = context.getApplicationContext();
        initWifiScanner();
    }

    public static WifiScannerSingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiScannerSingleton.class) {
                if (instance == null) {
                    instance = new WifiScannerSingleton(context);
                }
            }
        }
        return instance;
    }

    private void initWifiScanner() {
        wifiScannerHelper = new WifiScannerHelper(context);
        wifiScannerHelper.setScanResultsListener(new WifiScannerHelper.OnScanResultsListener() {
            @Override
            public void onResultsReceived(List<ScanResult> results, boolean isFreshScan) {
                Log.e(TAG, "onResultsReceived results: " + (results == null?null:results.size())+"   ,isFreshScan==>"+isFreshScan+"  , "+(wifiListListener != null));

                if (wifiListListener != null && isScanning) {
                    ArrayList<String> ssidList = new ArrayList<>();
                    for (ScanResult result : results) {
                        String securityType;
                        if (result.capabilities.contains("WPA2")) {
                            securityType = "WPA2-PSK";
                        } else if (result.capabilities.contains("WEP")) {
                            securityType = "WEP";
                        } else {
                            securityType = "OPEN";
                        }


                        ssidList.add(result.SSID + " (" + securityType+"_"+ wifiScannerHelper.getFrequencyBand(result.frequency)+ ")");
                    }
                    wifiListListener.onWifiListUpdated(ssidList);
                }
                isScanning = false;
            }

            @Override
            public void onPermissionRequired() {
                if (wifiListListener != null) {
                    wifiListListener.onPermissionRequired();
                }
            }

            @Override
            public void onPermissionDenied() {
                if (wifiListListener != null) {
                    wifiListListener.onPermissionDenied();
                }
            }

            @Override
            public void onWifiDisabled() {
                if (wifiListListener != null) {
                    wifiListListener.onWifiDisabled();
                }
            }

            @Override
            public void onScanFailed() {
                if (wifiListListener != null) {
                    wifiListListener.onScanFailed();
                }
                isScanning = false;
            }
        });
    }

    public void startWifiScan(WifiListListener listener) {
        if (isScanning) return;

        this.wifiListListener = listener;
        Log.d(TAG, "startWifiScan: "+checkLocationPermission());
        if (checkLocationPermission()) {
            isScanning = true;
            wifiScannerHelper.startScan();
        } else {
            wifiScannerHelper.notifyPermissionRequired();
        }
    }

    public void stopWifiScan() {
        isScanning = false;
        this.wifiListListener = null;
        if (wifiScannerHelper != null) {
            wifiScannerHelper.stopScan();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void release() {
        stopWifiScan();
        instance = null;
    }

    public interface WifiListListener {
        void onWifiListUpdated(ArrayList<String> wifiList);
        void onPermissionRequired();
        void onPermissionDenied();
        void onWifiDisabled();
        void onScanFailed();
    }
}