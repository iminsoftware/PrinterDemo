package com.imin.newprinter.demo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.printer.PrinterHelper;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;


public class IminApplication extends Application {


    public static Context mContext;
    private static final String TAG = "IminApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        mContext = base;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                Log.d(TAG, "onActivityCreated: ");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

                Log.d(TAG, "onActivityStarted: ");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

                Log.d(TAG, "onActivityResumed: ");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

                Log.d(TAG, "onActivityPaused: ");
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

                Log.d(TAG, "onActivityStopped: ");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                Log.d(TAG, "onActivitySaveInstanceState: ");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

                Log.d(TAG, "onActivityDestroyed: ");
            }
        });
        PrinterHelper.getInstance().initPrinterService(this);

        BaseApplication.setApplication(this);

        KLog.init(true);
//配置全局异常崩溃操作
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)
                .showErrorDetails(true)
                .showRestartButton(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                .errorDrawable(R.drawable.printer_icon)
                .restartActivity(MainActivity.class) //重新启动后的activity
                .apply();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PrinterHelper.getInstance().deInitPrinterService(this);
        Log.d(TAG, "onTerminate: ");
    }

}
