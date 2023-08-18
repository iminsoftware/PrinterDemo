package com.imin.newprinterdemo;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.imin.newprinterdemo.blue.BluetoothUtil;
import com.imin.printer.PrinterHelper;


public class BaseApplication extends MultiDexApplication {


    public static int isBufferPrint = 0;
    public static Context mContext;
    public static int isAPITest = 0;//0 AIDL , 1 API

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        PrinterHelper.getInstance().initPrinterService(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PrinterHelper.getInstance().deInitPrinterService(this);
        BluetoothUtil.closeBlueSocket();
    }


}
