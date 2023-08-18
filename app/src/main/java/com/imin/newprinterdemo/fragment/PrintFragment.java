package com.imin.newprinterdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.imin.newprinterdemo.PrintFunctionActivity;
import com.imin.newprinterdemo.R;
import com.imin.newprinterdemo.blue.BluetoothActivity;
import com.imin.newprinterdemo.databinding.FragmentPrintBinding;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.lang.ref.WeakReference;

public class PrintFragment extends Fragment {
    private static final String TAG = "printerTest_PrintFragment";
    private static final int UPDATE_VIEW = 5;
    FragmentPrintBinding binding;
    private MyHandler myHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPrintBinding.inflate(LayoutInflater.from(this.getContext()));
        binding.printTest.setOnClickListener(v -> {
            v.getContext().startActivity(new Intent(v.getContext(), PrintFunctionActivity.class));
        });
        binding.blueTest.setOnClickListener(v -> {
            startActivity(new Intent(this.getContext(), BluetoothActivity.class));
        });
        myHandler = new MyHandler(getContext());

        myHandler.sendEmptyMessageDelayed(UPDATE_VIEW,3000);
        return binding.getRoot();
    }

    private void initView() {
        Log.d(TAG,"  printer status ====>    "+PrinterHelper.getInstance().getPrinterStatus());

        PrinterHelper.getInstance().getPrinterSerialNumber(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
                Log.d(TAG,"  onRunResult ====>    "+isSuccess);
            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG,"  onReturnString ====>    "+result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.textSerialNumber.setText(getString(R.string.printer_serial_number,result));
                    }
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {
                Log.d(TAG,"  onRaiseException ====>    "+msg);
            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterModelName(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG,"  result ====>    "+result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.textModelName.setText(getString(R.string.printer_model_name,result));
                    }
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterThermalHead(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG,"  result ====>    "+result);
                //builder.append(getString(R.string.printer_thermal_head,result));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.textThermalHead.setText(getString(R.string.printer_thermal_head,result));
                    }
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });
        PrinterHelper.getInstance().getPrinterFirmwareVersion(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG,"  result ====>    "+result);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.textFirmwareVersion.setText(getString(R.string.printer_firmware_version,result));
                    }
                });

            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        String serviceVersion = PrinterHelper.getInstance().getServiceVersion();
        binding.textTitle2.setText(getString(R.string.printer_service_version,serviceVersion));
        binding.titleLy.setPrinterStatus(PrinterHelper.getInstance().getPrinterStatus());


    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    public  class MyHandler extends Handler {
        private final WeakReference<Context> mAct;

        public MyHandler(Context mainActivity) {
            mAct = new WeakReference<Context>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    initView();
                    break;
            }
        }
    }
}
