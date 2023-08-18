package com.imin.newprinterdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.imin.newprinterdemo.databinding.ActivityQrBinding;
import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.EditDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.IPrinterCallback;
import com.imin.printer.PrinterHelper;

public class QrActivity extends BaseActivity {
    private static final String TAG = "printerTest_QrActivity";
    private ActivityQrBinding binding;
    private EditDialog editDialog;
    private SelectDialog selectDialog;

    private int level = 0,alignment = 0;

    @Override @NonNull
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
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


        binding.qrContent2.setOnClickListener(v -> {
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

                        binding.qrContent2.setText(s.trim());
                        PrinterHelper.getInstance().setQrCodeSize(Integer.parseInt(binding.qrContent2.getText().toString().trim()));
                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getQRSizeList());
            selectDialog.show();

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
                        PrinterHelper.getInstance().setQrCodeErrorCorrectionLev(level);
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
                        alignment = i;
                        binding.qrContent4.setText(s.trim());
                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getAlignmentList());
            selectDialog.show();
        });

        binding.tvDoubleQr.setOnClickListener(v -> {
            startActivity(new Intent(QrActivity.this,DoubleQrActivity.class));
        });
        binding.tvPrint.setOnClickListener(v -> {

            if (BaseApplication.isAPITest == 1){
                PrinterHelper.getInstance().printQrCode(binding.qrContent1.getText().toString().trim(),null);
                PrinterHelper.getInstance().printAndFeedPaper(70);
                PrinterHelper.getInstance().printQrCodeWithAlign(binding.qrContent1.getText().toString().trim(),alignment,null);
                PrinterHelper.getInstance().printAndFeedPaper(70);
            }

            PrinterHelper.getInstance().printQRCodeWithFull(binding.qrContent1.getText().toString().trim(),
                    Integer.parseInt(binding.qrContent2.getText().toString().trim()),
                    level,
                    alignment, new IPrinterCallback() {
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