package com.imin.newprinterdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.imin.newprinterdemo.databinding.ActivityPicBinding;
import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PicActivity extends BaseActivity {
    private static final String TAG = "printerTest_PicActivity";
    private ActivityPicBinding binding;

    private MyHandler myHandler;

    private List<Bitmap> bitmapList = new ArrayList<>();

    private SelectDialog selectDialog;
    private int alignment = 0, cutType = 0, print=0;
    private int printTotalNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPicBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        myHandler = new MyHandler(this);
        initView();
    }

    private void initView() {
        binding.textContent1.setOnClickListener(v -> {
            if (selectDialog != null) {
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
                    if (s != null) {
                        alignment = i;
                        binding.textContent1.setText(s.trim());
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

        binding.textContent4.setOnClickListener(v -> {
            if (selectDialog != null) {
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
                    if (s != null) {
                        cutType = i;
                        binding.textContent4.setText(s.trim());
                    }
                }

                @Override
                public void cancel() {

                }

                @Override
                public void sure(String s) {

                }
            });
            selectDialog.setRvStringListData(Utils.getCutList());
            selectDialog.show();
        });

        binding.etPicNum1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String picNum = binding.etPicNum1.getText().toString().trim();
                if (picNum.length() == 0) {
                    binding.tvPrintExample.setEnabled(true);
                    binding.tvPrintExample.setAlpha(1f);
                    Toast.makeText(PicActivity.this, getText(R.string.edit_input_empty_tip), Toast.LENGTH_SHORT).show();
                    return;
                }
                int num = Integer.parseInt(picNum);

                if (num <= 0) {
                    binding.etPicNum1.setText("1");
                }
            }
        });

        binding.tvPrintExample.setOnClickListener(v -> {
            binding.tvPrintExample.setEnabled(false);
            binding.tvPrintExample.setAlpha(0.5f);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icona);

            String str = binding.etPicNum1.getText().toString().trim();
            int num = 0;
            if (str.length() > 0) {
                num = Integer.parseInt(str);
            }

            String interTime = binding.etPicNum3.getText().toString().trim();
            int time = 0;
            if (interTime.length() > 0) {
                time = Integer.parseInt(interTime);
            } else {
                time = 1;
            }

            if (num > 0) {
                printTotalNum = num;
                printBitmapVoid(time, bitmap);
            }

        });
    }


    private void printBitmapVoid(int intervalTime, Bitmap bitmap) {
        if (myHandler != null) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " printBitmap printTotalNum= " + printTotalNum + ", intervalTime = " + intervalTime);

                    if (BaseApplication.isAPITest == 1) {
                        PrinterHelper.getInstance().printBitmap(bitmap, new INeoPrinterCallback() {
                            @Override
                            public void onRunResult(boolean isSuccess) throws RemoteException {
                                Log.d(TAG, " printBitmap    onRunResult ====>    " + isSuccess);
                            }

                            @Override
                            public void onReturnString(String result) throws RemoteException {
                                Log.d(TAG, "  onReturnString ====>    " + result);
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

                    PrinterHelper.getInstance().printBitmapWithAlign(bitmap, alignment, new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {
                            Log.d(TAG, " printBitmapWithAlign    onRunResult ====>    " + isSuccess);
                        }

                        @Override
                        public void onReturnString(String result) throws RemoteException {
                            Log.d(TAG, "  onReturnString ====>    " + result);
                        }

                        @Override
                        public void onRaiseException(int code, String msg) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int code, String msg) throws RemoteException {

                        }
                    });
                    PrinterHelper.getInstance().printAndFeedPaper(70);
                    if (cutType == 1) {
                        PrinterHelper.getInstance().fullCut();
                    }
                    if (cutType == 2) {
                        PrinterHelper.getInstance().partialCut();
                    }
                    printTotalNum--;
                    if (printTotalNum > 0) {
                        myHandler.postDelayed(this, intervalTime*1000);
                    } else {
                        printTotalNum = 0;
                        binding.tvPrintExample.setEnabled(true);
                        binding.tvPrintExample.setAlpha(1f);
                    }
                }

            });
        }
    }

    class MyHandler extends Handler {
        WeakReference<PicActivity> reference;

        public MyHandler(PicActivity mActivity) {
            reference = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    }
}