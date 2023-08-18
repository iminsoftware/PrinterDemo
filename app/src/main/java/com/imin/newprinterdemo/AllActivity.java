package com.imin.newprinterdemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;

import com.imin.newprinterdemo.databinding.ActivityAllBinding;
import com.imin.newprinterdemo.utils.BytesUtils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

public class AllActivity extends BaseActivity {
    private static final String TAG = "printerTest_AllActivity";
    ActivityAllBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        //综合小票打印
        binding.tvPrintTicket0.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.customData(), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });
        //百度小票
        binding.tvPrintTicket1.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.getBaiduTestBytes(), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        binding.tvPrintTicket2.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.getErlmoData(), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        binding.tvPrintTicket3.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.getKoubeiData(), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        binding.tvPrintTicket4.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.getMeituanBill(), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        binding.tvPrintTicket5.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.printBitmap(BytesUtils.initBlackBlock(576)), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        binding.tvPrintTicket6.setOnClickListener(v -> {

            PrinterHelper.getInstance().sendRAWData(BytesUtils.printBitmap(BytesUtils.initBlackBlock(160,576)), new INeoPrinterCallback() {
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

                }
            });
            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

        //self check
        binding.tvPrintTicket7.setOnClickListener(v -> {
            PrinterHelper.getInstance().sendRAWData(BytesUtils.PrintSelfcheck(), new INeoPrinterCallback() {
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

                }
            });

            PrinterHelper.getInstance().printAndFeedPaper(70);
        });

    }
}