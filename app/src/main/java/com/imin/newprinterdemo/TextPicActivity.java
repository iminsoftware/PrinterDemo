package com.imin.newprinterdemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;

import com.imin.newprinterdemo.databinding.ActivityTextPicBinding;
import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.EditDialog;
import com.imin.newprinterdemo.dialog.SeekSizeDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

public class TextPicActivity extends BaseActivity {
    private static final String TAG = "printerTest_TextPicActivity";
    private ActivityTextPicBinding binding;

    private EditDialog editDialog;
    private SelectDialog selectDialog;
    private SeekSizeDialog seekSizeDialog;
    private List<String> codepageList = new ArrayList<>();
    private int codepage = 0, alignment = 0, cutType = 0, textType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextPicBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        codepageList = PrinterHelper.getInstance().getFontCodepage();
        binding.textPicContent1.setOnClickListener(v -> {
            if (editDialog != null) {
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
                    if (s != null && s.length() > 0) {
                        binding.textPicContent1.setText(s.trim());
                    }
                }
            });
            editDialog.setEdText(binding.textPicContent1.getText().toString().trim());
            editDialog.show();
        });
        binding.textPicContent2.setOnClickListener(v -> {
            if (codepageList == null || codepageList.size() == 0) {
                return;
            }
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
                        binding.textPicContent2.setText(s.trim());
                        if (s.contains(" ")) {
                            String page1 = s.substring(1, s.indexOf(" "));
                            codepage = Integer.parseInt(page1);
                            PrinterHelper.getInstance().setFontCodepage(codepage);
                        }
                    }
                }

                @Override
                public void cancel() {
                }

                @Override
                public void sure(String s) {
                }
            });
            selectDialog.setRvStringListData(codepageList);
            selectDialog.show();
        });
        binding.textPicContent3.setOnClickListener(v -> {
            if (seekSizeDialog != null) {
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
                    if (s != null) {
                        binding.textPicContent3.setText(s.trim());
                        PrinterHelper.getInstance().setTextBitmapSize(Integer.parseInt(binding.textPicContent3.getText().toString().trim()));
                    }
                }
            });
            seekSizeDialog.setMin(12);
            seekSizeDialog.setString(binding.textPicContent3.getText().toString().trim());
            seekSizeDialog.setMax(36);
            seekSizeDialog.show();
        });

        binding.textPicContent4.setOnClickListener(v -> {
            if (seekSizeDialog != null) {
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
                    if (s != null) {
                        binding.textPicContent4.setText(s.trim());
                        PrinterHelper.getInstance().setTextBitmapLineSpacing(Float.parseFloat(binding.textPicContent4.getText().toString().trim()));
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.textPicContent4.getText().toString().trim());
            seekSizeDialog.setMax(255);
            seekSizeDialog.show();
        });
        binding.textPicContent5.setOnClickListener(v -> {
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
                        textType = i;
                        binding.textPicContent5.setText(s.trim());
                        PrinterHelper.getInstance().setTextBitmapStyle(textType);
                    }
                }

                @Override
                public void cancel() {
                }

                @Override
                public void sure(String s) {
                }
            });
            selectDialog.setRvStringListData(Utils.getTextTypeList());
            selectDialog.show();
        });
        binding.textPicContent6.setOnClickListener(v -> {
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
                        binding.textPicContent6.setText(s.trim());
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
        binding.textPicContent7.setOnClickListener(v -> {
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
                        binding.textPicContent7.setText(s.trim());
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

        binding.textPicContent8.setOnClickListener(v -> {
            if (seekSizeDialog != null) {
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
                    if (s != null) {
                        binding.textPicContent8.setText(s.trim());
                        PrinterHelper.getInstance().setTextBitmapLetterSpacing(Float.parseFloat(binding.textPicContent8.getText().toString().trim())/80);
                    }
                }
            });
            seekSizeDialog.setMin(0);
            seekSizeDialog.setString(binding.textPicContent8.getText().toString().trim());
            seekSizeDialog.setMax(255);
            seekSizeDialog.show();
        });

        binding.tvPrint.setOnClickListener(v -> {

            PrinterHelper.getInstance().setTextBitmapStrikeThru(binding.textPicCheck4.isChecked() ? true : false);
            PrinterHelper.getInstance().setTextBitmapUnderline(binding.textCheck3.isChecked() ? true : false);
            PrinterHelper.getInstance().setTextBitmapAntiWhite(binding.textPicCheck5.isChecked() ? true : false);
            if (BaseApplication.isAPITest == 1) {
                PrinterHelper.getInstance().printTextBitmap(binding.textPicContent1.getText().toString().trim(), new INeoPrinterCallback() {
                    @Override
                    public void onRunResult(boolean isSuccess) throws RemoteException {
                        Log.d(TAG, " printTextBitmap    onRunResult ====>    " + isSuccess);
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


            PrinterHelper.getInstance().printTextBitmapWithAli(binding.textPicContent1.getText().toString().trim(), alignment, new INeoPrinterCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                    Log.d(TAG, " printTextBitmapWithAli    onRunResult ====>    " + isSuccess);
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

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrinterHelper.getInstance().setTextBitmapLetterSpacing(0.1f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrinterHelper.getInstance().setTextBitmapLetterSpacing(0.1f);
    }
}