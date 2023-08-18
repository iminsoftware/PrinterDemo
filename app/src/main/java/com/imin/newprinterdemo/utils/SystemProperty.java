package com.imin.newprinterdemo.utils;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * create by Mark on 2023/7/11 Time：20:38
 * tip:
 */
public class SystemProperty {

    /**
     * 系统版本号
     */
    public static final String SYSTEM_VERSION = "ro.build.version.incremental";

    /**
     * android 序列号
     */
    public static final String SERIAL_NUMBER = "ro.serialno";
    public static final String PRODUCT_MODEL = "ro.product.model";
    public static final String RO_BUILD_ID = "ro.build.id";


    public static String getSystemProperties(String property) {
        String value = "";
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method getter = clazz.getDeclaredMethod("get", String.class);
            value = (String) getter.invoke(null, property);
        } catch (Exception e) {
            Log.d("TAG", "Unable to read system properties");
        }
        return value;
    }
}
