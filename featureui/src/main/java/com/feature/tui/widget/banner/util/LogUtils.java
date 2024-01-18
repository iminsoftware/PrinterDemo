package com.feature.tui.widget.banner.util;

import android.util.Log;

public class LogUtils {
    public static final String TAG = "banner_log";

    private static final boolean DEBUG = true;

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void v( String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }
}
