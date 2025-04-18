package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinter.demo.databinding.FragmentWirelessPrintingBinding;

public class WirelessPrintingFragment extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentWirelessPrintingBinding binding = FragmentWirelessPrintingBinding.inflate(inflater);
        return binding.getRoot();
    }
}
