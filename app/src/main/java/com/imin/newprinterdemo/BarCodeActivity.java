package com.imin.newprinterdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;

import com.imin.newprinterdemo.databinding.ActivityBarCodeBinding;
import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.EditDialog;
import com.imin.newprinterdemo.dialog.SeekSizeDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.BitmapUtil;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

public class BarCodeActivity extends BaseActivity {
    private static final String TAG = "printerTest_BarCodeActivity";
    private ActivityBarCodeBinding binding;
    private EditDialog editDialog;
    private SelectDialog selectDialog;
    private SeekSizeDialog seekSizeDialog;
    private int symbology = 4,textposition = 0,alignment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarCodeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.barTitle20.setText(Utils.getBarSymbologyListTip().get(symbology));
        binding.barContent1.setText(Utils.getBarContentList().get(symbology));
        binding.barContent1.setOnClickListener(v -> {
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
                        binding.barContent1.setText(s.trim());
                    }
                }

            });
            editDialog.setEdText(binding.barContent1.getText().toString().trim());
            editDialog.show();
        });

        binding.barContent2.setOnClickListener(v -> {
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
                        symbology = i;
                        binding.barContent2.setText(s.trim());
                        binding.barTitle20.setText(Utils.getBarSymbologyListTip().get(symbology));
                        binding.barContent1.setText(Utils.getBarContentList().get(symbology));

                    }
                }
                @Override
                public void cancel() {

                }
                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getBarSymbologyList());
            selectDialog.show();

        });

        binding.barContent3.setOnClickListener(v -> {
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
                    Log.d(TAG, "selectItem: posStr= " + s + ", i= " + i);
                    if (s != null){
                        textposition = i;
                        binding.barContent3.setText(s);
                    }
                }
                @Override
                public void cancel() {
                }
                @Override
                public void sure(String s) {
                }
            });
            selectDialog.setRvStringListData(Utils.getBarcodeTextList());
            selectDialog.show();
        });


        binding.barContent4.setOnClickListener(v -> {
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
                        binding.barContent4.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(1);
            seekSizeDialog.setString(binding.barContent4.getText().toString().trim());
            seekSizeDialog.setMax(255);
            seekSizeDialog.show();
        });

        binding.barContent5.setOnClickListener(v -> {
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
                        binding.barContent5.setText(s.trim());
                    }
                }
            });
            seekSizeDialog.setMin(2);
            seekSizeDialog.setString(binding.barContent5.getText().toString().trim());
            seekSizeDialog.setMax(6);
            seekSizeDialog.show();

        });

        binding.barContent6.setOnClickListener(v -> {
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
                        binding.barContent6.setText(s.trim());
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

        binding.tvPrint.setOnClickListener(v -> {
            PrinterHelper.getInstance().setBarCodeWidth(Integer.parseInt(binding.barContent5.getText().toString().trim()));
            PrinterHelper.getInstance().setBarCodeHeight(Integer.parseInt(binding.barContent4.getText().toString().trim()));
            PrinterHelper.getInstance().setBarCodeContentPrintPos(textposition);
            if (BaseApplication.isAPITest == 1){
                Log.d(TAG," printBarCode  开始 ====>    ");
                PrinterHelper.getInstance().printBarCode(binding.barContent1.getText().toString().trim(), symbology, new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean isSuccess) throws RemoteException {
                        Log.d(TAG," printBarCode  onRunResult ====>    "+isSuccess);
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
                });
                PrinterHelper.getInstance().printAndFeedPaper(70);
                PrinterHelper.getInstance().printBarCodeWithAlign(binding.barContent1.getText().toString().trim(), symbology,alignment, new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean isSuccess) throws RemoteException {
                        Log.d(TAG," printBarCode  onRunResult ====>    "+isSuccess);
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
                });

                PrinterHelper.getInstance().printAndFeedPaper(70);
            }

            Log.d(TAG," printBarCodeWithFull  开始 ====>    ");
            PrinterHelper.getInstance().printBarCodeWithFull(binding.barContent1.getText().toString().trim(),
                    symbology,
                    Integer.parseInt(binding.barContent5.getText().toString().trim()),
                    Integer.parseInt(binding.barContent4.getText().toString().trim()),
                    textposition,alignment,new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {
                            Log.d(TAG," printBarCodeWithFull  onRunResult ====>    "+isSuccess);
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
                    });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

    }


}