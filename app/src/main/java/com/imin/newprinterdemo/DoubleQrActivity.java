package com.imin.newprinterdemo;


import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;

import com.imin.newprinterdemo.databinding.ActivityDoubleQrBinding;
import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.EditDialog;
import com.imin.newprinterdemo.dialog.SeekSizeDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.IPrinterCallback;
import com.imin.printer.PrinterHelper;

public class DoubleQrActivity extends BaseActivity {
    private static final String TAG = "printerTest_DoubleQrActivity";
    private ActivityDoubleQrBinding binding;
    private EditDialog editDialog;
    private SelectDialog selectDialog;
    private SeekSizeDialog seekSizeDialog;
    private int level = 0,level1 = 0,size = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoubleQrBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.qrContent.setOnClickListener(v -> {
            if (selectDialog != null){
                selectDialog.dismiss();
                selectDialog = null;
            }
            selectDialog = new SelectDialog(v.getContext());
            selectDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }
                @Override
                public void selectItem(String s, int i) {
                    if (s != null){
                        size = i;
                        binding.qrContent.setText(s.trim());
                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getDoubleQRSizeList());
            selectDialog.show();
        });

        binding.qrContent1.setOnClickListener(v -> {
            if (editDialog != null){
                editDialog.dismiss();
                editDialog = null;
            }
            editDialog = new EditDialog(this);
            editDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }
                @Override
                public void selectItem(String s, int i) {
                }

                @Override
                public void cancel() {
                }
                @Override
                public void sure(String s) {
                    if (s != null && s.length()>0){
                        binding.qrContent1.setText(s.trim());
                    }
                }

            });
            editDialog.setEdText(binding.qrContent1.getText().toString().trim());
            editDialog.show();
        });


        binding.qrContent11.setOnClickListener(v -> {
            if (editDialog != null){
                editDialog.dismiss();
                editDialog = null;
            }
            editDialog = new EditDialog(this);
            editDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }
                @Override
                public void selectItem(String s, int i) {
                }

                @Override
                public void cancel() {
                }
                @Override
                public void sure(String s) {
                    if (s != null && s.length()>0){
                        binding.qrContent11.setText(s.trim());
                    }
                }

            });
            editDialog.setEdText(binding.qrContent11.getText().toString().trim());
            editDialog.show();
        });


        binding.qrContent22.setOnClickListener(v -> {
            if (seekSizeDialog != null){
                seekSizeDialog.dismiss();
                seekSizeDialog = null;
            }
            seekSizeDialog = new SeekSizeDialog(v.getContext());
            seekSizeDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void selectItem(String s, int i) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void sure(String s) {
                    if (s != null){
                        binding.qrContent22.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.qrContent22.getText().toString().trim());
            seekSizeDialog.setMax(255);
            seekSizeDialog.show();

        });

        binding.qrContent2.setOnClickListener(v -> {
            if (seekSizeDialog != null){
                seekSizeDialog.dismiss();
                seekSizeDialog = null;
            }
            seekSizeDialog = new SeekSizeDialog(v.getContext());
            seekSizeDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void selectItem(String s, int i) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void sure(String s) {
                    if (s != null){
                        binding.qrContent2.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.qrContent2.getText().toString().trim());
            seekSizeDialog.setMax(255);
            seekSizeDialog.show();

        });

        binding.qrContent3.setOnClickListener(v -> {
            if (selectDialog != null){
                selectDialog.dismiss();
                selectDialog = null;
            }
            selectDialog = new SelectDialog(v.getContext());
            selectDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }
                @Override
                public void selectItem(String s, int i) {
                    if (s != null){
                        level = i;
                        binding.qrContent3.setText(s.trim());
                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getQRLevList());
            selectDialog.show();
        });

        binding.qrContent33.setOnClickListener(v -> {
            if (selectDialog != null){
                selectDialog.dismiss();
                selectDialog = null;
            }
            selectDialog = new SelectDialog(v.getContext());
            selectDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }
                @Override
                public void selectItem(String s, int i) {
                    if (s != null){
                        level1 = i;
                        binding.qrContent33.setText(s.trim());
                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getQRLevList());
            selectDialog.show();
        });


        binding.qrContent4.setOnClickListener(v -> {
            if (seekSizeDialog != null){
                seekSizeDialog.dismiss();
                seekSizeDialog = null;
            }
            seekSizeDialog = new SeekSizeDialog(v.getContext());
            seekSizeDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void selectItem(String s, int i) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void sure(String s) {
                    if (s != null){
                        binding.qrContent4.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.qrContent4.getText().toString().trim());
            seekSizeDialog.setMax(40);
            seekSizeDialog.show();
        });

        binding.qrContent44.setOnClickListener(v -> {
            if (seekSizeDialog != null){
                seekSizeDialog.dismiss();
                seekSizeDialog = null;
            }
            seekSizeDialog = new SeekSizeDialog(v.getContext());
            seekSizeDialog.setClickListener(new BaseDialog.ClickListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void selectItem(String s, int i) {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void sure(String s) {
                    if (s != null){
                        binding.qrContent44.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.qrContent44.getText().toString().trim());
            seekSizeDialog.setMax(40);
            seekSizeDialog.show();
        });

        binding.tvPrint.setOnClickListener(v -> {
            PrinterHelper.getInstance().setDoubleQRSize(Integer.parseInt(binding.qrContent.getText().toString().trim()));
            PrinterHelper.getInstance().setDoubleQR1MarginLeft(Integer.parseInt(binding.qrContent2.getText().toString().trim()));
            PrinterHelper.getInstance().setDoubleQR2MarginLeft(Integer.parseInt(binding.qrContent22.getText().toString().trim()));
            PrinterHelper.getInstance().setDoubleQR1Level(level);
            PrinterHelper.getInstance().setDoubleQR2Level(level1);
            PrinterHelper.getInstance().setDoubleQR1Version(Integer.parseInt(binding.qrContent4.getText().toString().trim()));
            PrinterHelper.getInstance().setDoubleQR2Version(Integer.parseInt(binding.qrContent44.getText().toString().trim()));

            PrinterHelper.getInstance().printDoubleQR(binding.qrContent1.getText().toString().trim(),
                    binding.qrContent11.getText().toString().trim(), new IPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {
                            Log.d(TAG,"  onRunResult ====>    "+isSuccess);
                        }

                        @Override
                        public void onReturnString(String result) throws RemoteException {
                            Log.d(TAG,"  onReturnString ====>    "+result);
                        }

                        @Override
                        public void onRaiseException(int code, String msg) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int code, String msg) throws RemoteException {

                        }

                        @Override
                        public IBinder asBinder() {
                            return null;
                        }
                    });

            PrinterHelper.getInstance().printAndFeedPaper(70);
        });
    }
}