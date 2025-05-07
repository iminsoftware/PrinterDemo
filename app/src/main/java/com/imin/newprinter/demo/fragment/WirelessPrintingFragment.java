package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.util.ToastUtil;
import com.imin.newprinter.demo.MainActivity;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding;
import com.imin.newprinter.demo.utils.WifiKeyName;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

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

    public void disConnect(){
        binding.connectStatusTv.setText(String.format(getString(R.string.status_wifi), MainActivity.connectType,getString(R.string.disconnected)));
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

            PrinterHelper.getInstance().setPrinterAction(WifiKeyName.WIRELESS_CONNECT_TYPE
                    , MainActivity.connectType.contains("WIFI") ? "WIFI" : "BT"
                    , new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean b) throws RemoteException {

                        }

                        @Override
                        public void onReturnString(String s) throws RemoteException {

                        }

                        @Override
                        public void onRaiseException(int i, String s) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int i, String s) throws RemoteException {

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 0){
                                ToastUtil.showShort(getContext(),R.string.print_fail);
                            }else {

                            }

                        }
                    });

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
