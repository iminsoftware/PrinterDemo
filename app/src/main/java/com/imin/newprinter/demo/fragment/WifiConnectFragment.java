package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding;

/**
 * @Author: hy
 * @date: 2025/4/18
 * @description:
 */
public class WifiConnectFragment extends BaseFragment {

    private com.imin.newprinter.demo.databinding.FragmentWifiConnectBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWifiConnectBinding.inflate(inflater);
        return binding.getRoot();
    }

}
