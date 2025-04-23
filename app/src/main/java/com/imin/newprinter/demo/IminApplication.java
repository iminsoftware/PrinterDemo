package com.imin.newprinter.demo;

import static me.goldze.mvvmhabit.utils.Utils.getContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.util.XUiDisplayHelper;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.InitPrinterCallback;
import com.imin.printer.PrinterHelper;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.Utils;


public class IminApplication extends Application {


    public static Context mContext;
    private static final String TAG = "IminApplication";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        mContext = base;

    }
    public FunctionTestHandler functionTestHandler;
    private static Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        functionTestHandler = new FunctionTestHandler(Looper.myLooper());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                Log.d(TAG, "onActivityCreated: ");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                currentActivity = activity;
                Log.d(TAG, "onActivityStarted: ");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                Log.d(TAG, "onActivityResumed: ");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

                Log.d(TAG, "onActivityPaused: ");
                if (activity == currentActivity) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

                Log.d(TAG, "onActivityStopped: ");
                if (activity == currentActivity) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                Log.d(TAG, "onActivitySaveInstanceState: ");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

                Log.d(TAG, "onActivityDestroyed: ");
                if (activity == currentActivity) {
                    currentActivity = null;
                }
            }
        });
        PrinterHelper.getInstance().initPrinterService(this, new InitPrinterCallback() {
            @Override
            public void onConnected() {
                Log.d(TAG, "registorUIChangeLiveDataCallBack:===== "+PrinterHelper.getInstance().getPrinterSupplierName());
                functionTestHandler.sendEmptyMessageDelayed(PRINTER_UPDATE_STATUS, PRINTER_UPDATE_DELAY);
                int screenWidth = XUiDisplayHelper.getScreenWidth(getContext());
                int screenHeight = XUiDisplayHelper.getScreenHeight(getContext());
                if (com.imin.newprinter.demo.utils.Utils.compareStringToInt(PrinterHelper.getInstance().getServiceVersion(),"2.0.11_2412221202") >=0){//判断是否 > 20241223
                    PrinterHelper.getInstance().labelGetPrinterMode(new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {

                        }

                        @Override
                        public void onReturnString(String result) throws RemoteException {
                            if (!com.imin.newprinter.demo.utils.Utils.isEmpty(result)) {
                                if (result.equals("Receipt")){

                                }else {

                                }
                            }
                            com.imin.newprinter.demo.utils.Utils.printModel = result;

                            if (screenWidth > screenHeight) {
                                functionTestHandler.sendEmptyMessageDelayed(PRINTER_GET_PARAMETER, PRINTER_UPDATE_DELAY + PRINTER_UPDATE_DELAY*2);
                                functionTestHandler.sendEmptyMessageDelayed(PRINTER_UPDATE_PARAMETER, PRINTER_UPDATE_DELAY * 3);
                            }

                            if (currentActivity != null){
                                MainActivity activity = (MainActivity) currentActivity;
                                activity.getPrinterParameter();
                            }
                        }

                        @Override
                        public void onRaiseException(int code, String msg) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int code, String msg) throws RemoteException {

                        }
                    });

                }else {
                    if (screenWidth > screenHeight) {
                        functionTestHandler.sendEmptyMessageDelayed(PRINTER_GET_PARAMETER, PRINTER_UPDATE_DELAY + PRINTER_UPDATE_DELAY*2);
                        functionTestHandler.sendEmptyMessageDelayed(PRINTER_UPDATE_PARAMETER, PRINTER_UPDATE_DELAY * 3);
                    }

                    if (currentActivity != null){
                        MainActivity activity = (MainActivity) currentActivity;
//                        activity.initViewData();
                    }

                }


            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "registorUIChangeLiveDataCallBack:===== "+PrinterHelper.getInstance().getPrinterSupplierName());
            }
        });

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

    private static final int PRINTER_UPDATE_STATUS = 200;
    private static final int PRINTER_GET_PARAMETER = 201;
    private static final int PRINTER_UPDATE_PARAMETER = 202;
    private static final int PRINTER_UPDATE_DELAY = 1000;
    public class FunctionTestHandler extends Handler {
        public FunctionTestHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);
            try {
                switch (msg.what) {
                    case PRINTER_UPDATE_STATUS:
                        if (currentActivity != null){
                            MainActivity activity = (MainActivity) currentActivity;
                            activity.updateStatus(PrinterHelper.getInstance().getPrinterStatus());
                        }
                        break;

                    case PRINTER_GET_PARAMETER:
                        if (currentActivity != null){
                            MainActivity activity = (MainActivity) currentActivity;
                            activity.getPrinterParameter();
                        }

                        break;
                    case PRINTER_UPDATE_PARAMETER:

                        break;
                }
            }catch (Exception e){
                Log.d(TAG, "handleMessage:e " + e.getMessage());
            }

        }
    }
}
