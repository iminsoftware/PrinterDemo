package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding;
import com.imin.newprinter.demo.utils.BytesUtils;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;

public class WirelessPrintingFragment extends BaseFragment{
    private static final String TAG = "PrintDemo_WirelessPrintingFragment";

    private static final String CONNECT_TYPE = "connectType";
    private static final String CONNECT_CONTENT = "connectContent";
    private com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding binding;

    public static WirelessPrintingFragment newInstance(String connectType,String connectContent) {
        WirelessPrintingFragment fragment = new WirelessPrintingFragment();
        Bundle args = new Bundle();
        args.putString(CONNECT_TYPE, connectType);
        args.putString(CONNECT_CONTENT, connectContent);
        fragment.setArguments(args);
        return fragment;
    }

    String connectTypeStr,connectContentStr;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            connectTypeStr = getArguments().getString(CONNECT_TYPE);
            connectContentStr = getArguments().getString(CONNECT_CONTENT);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWirelessPrintingBinding.inflate(inflater);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        binding.connectStatusTv.setText(String.format(getString(R.string.status_wifi),connectTypeStr,getString(R.string.normal)));
        binding.connectContentTv.setText(connectContentStr);
        binding.connectNetworkTv.setOnClickListener(view -> {
            switchFragment(connectTypeStr.contains("WIFI")?1:2);
        });
        binding.printTest1.setOnClickListener(view -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.getErlmoData(), null);
            PrinterHelper.getInstance().partialCut();
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
