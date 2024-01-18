package com.feature.tui.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class XUiDisplayHelper {

    /**
     * 通过资源获取像素
     *
     * @param id
     * @return
     */
    public static int getDimensionPixelToId(Context context, int id) {
        return Integer.valueOf(context.getResources().getDimensionPixelOffset(id));
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics;
    }

    /**
     * 把以 dp 为单位的值，转化为以 px 为单位的值
     *
     * @param dp 以 dp 为单位的值
     * @return px value
     */
    public static int dp2px(Context context, int dp) {
        return (int) ((double) (getDensity(context) * (float) dp) + 0.5);
    }

    /**
     * 把以 dp 为单位的值，转化为以 px 为单位的值
     *
     * @param dp 以 dp 为单位的值
     * @return px value
     */
    public static int dp2px(Context context, float dp) {
        return (int) ((double) (getDensity(context) * dp) + 0.5);
    }

    /**
     * 把以 px 为单位的值，转化为以 dp 为单位的值
     *
     * @param px 以 px 为单位的值
     * @return dp值
     */
    public static int px2dp(Context context, int px) {
        return (int) ((double) ((float) px / getDensity(context)) + 0.5);
    }

    /**
     * 单位转换: sp -> px
     *
     * @param sp
     * @return
     */
    public static int sp2px(Context context, int sp) {
        return (int) ((double) (getFontDensity(context) * (float) sp) + 0.5);
    }

    /**
     * 单位转换:px -> sp
     *
     * @param px
     * @return
     */
    public static int px2sp(Context context, int px) {
        return (int) ((double) ((float) px / getFontDensity(context)) + 0.5);
    }

    public static float getDensity(Context context) {
        Resources resources = context.getResources();
        return resources.getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        Resources resources = context.getResources();
        return resources.getDisplayMetrics().scaledDensity;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics((Context) context).widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取屏幕的真实宽高
     *
     * @param context
     * @return
     */
    public static int[] getRealScreenSize(Context context) {
        int[] size = new int[2];
        int widthPixels = 0;
        int heightPixels = 0;
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        try {
            Point realSize = new Point();
            d.getRealSize(realSize);
            Display.class.getMethod("getRealSize", Point.class).invoke(d, new Object[]{realSize});
            widthPixels = realSize.x;
            heightPixels = realSize.y;
        } catch (Exception e) {
            e.printStackTrace();
        }
        size[0] = widthPixels;
        size[1] = heightPixels;
        return size;
    }

    /**
     * 通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
     *
     * @param context
     * @return
     */
    public static boolean isNavMenuExist(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(4);
        return !hasMenuKey && !hasBackKey;
    }

    /**
     * 判断是否有状态栏
     *
     * @param context
     * @return
     */
    public static boolean hasStatusBar(Context context) {
        if (context instanceof Activity) {
            Window window = ((Activity) context).getWindow();
            WindowManager.LayoutParams attrs = window.getAttributes();
            return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        return true;
    }

    /**
     * 获取ActionBar高度
     *
     * @param context
     * @return
     */
    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            Resources resources = context.getResources();
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            String string = field.get(obj).toString();
            int x = Integer.parseInt(string);
            if (x > 0) {
                return context.getResources().getDimensionPixelSize(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取虚拟菜单的高度,若无则返回0
     *
     * @param context
     * @return
     */
    public static int getNavMenuHeight(Context context) {
        if (!isNavMenuExist(context)) {
            return 0;
        }
        int resourceNavHeight = getResourceNavHeight(context);
        return resourceNavHeight >= 0 ? resourceNavHeight : getRealScreenSize(context)[1] - getScreenHeight(context);
    }

    public static int getResourceNavHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : -1;
    }

    public static boolean hasCamera(Context context) {
        PackageManager pckMgr = context.getPackageManager();
        boolean flag = pckMgr.hasSystemFeature("android.hardware.camera.front");
        boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
        boolean flag2 = false;
        flag2 = flag || flag1;
        return flag2;
    }

    /**
     * 是否有网络功能
     *
     * @param context
     * @return
     */
    @SuppressLint(value = {"MissingPermission"})
    public static boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * 判断是否存在pckName包
     *
     * @param pckName
     * @return
     */
    public static boolean isPackageExist(Context context, String pckName) {
        try {
            PackageInfo pckInfo = context.getPackageManager().getPackageInfo(pckName, 0);
            if (pckInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException nameNotFoundException) {
        }
        return false;
    }

    /**
     * 判断 SD Card 是否 ready
     *
     * @return
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED == Environment
                .getExternalStorageState();
    }

    /**
     * 获取当前国家的语言
     *
     * @param context
     * @return
     */
    public static String getCurCountryLan(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        return sysLocale.getLanguage() + "-" + sysLocale.getCountry();
    }

    /**
     * 判断是否为中文环境
     *
     * @param context
     * @return
     */
    public static boolean isZhCN(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        String lang = sysLocale.getCountry();
        return lang.equalsIgnoreCase("CN");
    }

    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 取消全屏
     *
     * @param activity
     */
    public static void cancelFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    /**
     * 判断是否全屏
     *
     * @param activity
     * @return
     */
    public static boolean isFullScreen(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        return (params.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /**
     * 判断设备是否存在NavigationBar
     *
     * @return true 存在, false 不存在
     */
    public static boolean deviceHasNavigationBar() {
        boolean haveNav = false;
        try {
            Method hasNavBarMethod;
            Method getWmServiceMethod;
            Class<?> windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal");
            Method method = getWmServiceMethod = windowManagerGlobalClass.getDeclaredMethod("getWindowManagerService");
            method.setAccessible(true);
            Object iWindowManager = getWmServiceMethod.invoke(null);
            Class<?> iWindowManagerClass = iWindowManager.getClass();
            Method method2 = hasNavBarMethod = iWindowManagerClass.getDeclaredMethod("hasNavigationBar");
            method2.setAccessible(true);
            Object object = hasNavBarMethod.invoke(iWindowManager);
            haveNav = (Boolean) object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return haveNav;
    }

    public static boolean huaweiIsNotchSetToShowInSetting(Context context) {
        // 0: 默认
        // 1: 隐藏显示区域
        int result = Settings.Secure.getInt(context.getContentResolver(), "display_notch_status", 0);
        return result == 0;
    }

}
