package com.imin.newprinter.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

/**
 * @Author: hy
 * @date: 2025/4/24
 * @description:
 */
public class NetworkUtils {

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) return false;

            NetworkCapabilities capabilities =
                    cm.getNetworkCapabilities(activeNetwork);

            return capabilities != null
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    && networkInfo.isConnected();
        }
    }

    // 获取当前 WiFi 的 SSID（需要定位权限）
    @SuppressLint("MissingPermission")
    public static String getCurrentSsid(Context context) {
        WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null) return "";

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        return ssid.replace("\"", ""); // 移除引号
    }
}
