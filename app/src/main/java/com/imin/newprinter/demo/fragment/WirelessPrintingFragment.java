package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinter.demo.MainActivity;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding;
import com.imin.newprinter.demo.utils.BytesUtils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.IWirelessPrintResult;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.ConnectType;
import com.imin.printer.enums.WirelessConfig;
import com.imin.printer.wireless.WirelessPrintStyle;

import java.util.ArrayList;

public class WirelessPrintingFragment extends BaseFragment{
    private static final String TAG = "PrintDemo_WirelessPrintingFragment";

    private static final String CONNECT_TYPE = "connectType";
    private static final String CONNECT_CONTENT = "connectContent";
    private com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding binding;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: "+isVisibleToUser+"    "+isResumed()+"    ");
        if (isVisibleToUser && isResumed()) {
            // 当 Fragment 对用户可见时执行操作（兼容旧版本）
            binding.connectStatusTv.setText(String.format(getString(R.string.status_wifi), MainActivity.connectType,getString(R.string.connected)));
            binding.connectContentTv.setText(MainActivity.connectAddress);



        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWirelessPrintingBinding.inflate(inflater);
        initView();
        return binding.getRoot();
    }


    public void updateStatus(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.connectStatusTv.setText(String.format(getString(R.string.status_wifi), MainActivity.connectType,getString(R.string.connected)));
                binding.connectContentTv.setText(MainActivity.connectAddress);
            }
        });

    }
    private void initView() {

        binding.connectNetworkTv.setOnClickListener(view -> {
            Log.d(TAG, "connectNetworkTv: "+"    "+MainActivity.connectType);
            switchFragment(MainActivity.connectType.contains("WIFI")?1:2);
        });
        binding.printTest1.setOnClickListener(view -> {

            PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                    .setConfig(MainActivity.connectType.contains("WIFI")? ConnectType.WIFI.getTypeName():ConnectType.BT.getTypeName()), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {

                }

                @Override
                public void onReturnString(String s) throws RemoteException {

                }
            });

            PrinterHelper.getInstance().printerSelfChecking(new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {

                }

                @Override
                public void onReturnString(String result) throws RemoteException {

                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {

                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {

                    Log.d(TAG, "onPrintResult: " + code +  ", msg = " + msg);
                }
            });
        });
    }

    private SwitchFragmentListener fragmentListener;

    public void setCallback(SwitchFragmentListener listener) {

        fragmentListener = listener;
        Log.d(TAG, "setCallback: " + (fragmentListener != null));
    }



    public void switchFragment(int num) {
        Log.d(TAG, "switchPager :num=  " + num + (fragmentListener != null));

        if (fragmentListener != null) {
            fragmentListener.switchFragment(num);
        }
    }
}
