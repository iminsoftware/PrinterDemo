package com.imin.newprinterdemo;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.LayoutInflater;

import com.imin.newprinterdemo.databinding.ActivityMainBinding;
import com.imin.newprinterdemo.fragment.PrintFragment;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        selectPosition = 0;
        checkStorageManagerPermission();
        selectItem();
        fragmentList = new ArrayList<>();
        fragmentList.add(new PrintFragment());

        binding.vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
                binding.vp.setCurrentItem(position);
                selectItem();
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        binding.vp.setAdapter(adapter);
        binding.vp.setOffscreenPageLimit(3);
        binding.vp.setCurrentItem(0);

        binding.printerTest.setOnClickListener(v -> {
            selectPosition = 0;
            selectItem();
            binding.vp.setCurrentItem(selectPosition);
        });

        PrinterHelper.getInstance().initPrinter(getPackageName(), new INeoPrinterCallback() {
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

    }

    int selectPosition = 0;

    private void selectItem() {
        switch (selectPosition) {
            case 0:
                binding.ivPrinter.setImageResource(R.mipmap.ic_printer);
                binding.tvPrinter.setTextColor(getResources().getColor(R.color.color_3333333));
                break;
        }
    }

    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectItem();
    }
}