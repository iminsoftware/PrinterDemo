package com.imin.newprinter.demo.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.feature.tui.util.XUiDisplayHelper;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.AllTestAdapter;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentAllBinding;
import com.imin.newprinter.demo.databinding.FragmentAllTestBinding;
import com.imin.newprinter.demo.utils.BitmapUtils;
import com.imin.newprinter.demo.utils.BytesUtils;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.IWirelessPrintResult;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.ConnectType;
import com.imin.printer.enums.WirelessConfig;
import com.imin.printer.wireless.WirelessPrintStyle;

import java.util.ArrayList;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class AllFragment extends BaseFragment {

    private static final String TAG = "AllTestFragment";
    private String[] contents;
    private com.imin.newprinter.demo.databinding.FragmentAllBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllBinding.inflate(inflater);
        initView();
        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: "+isVisibleToUser+"    "+isResumed());
        if (isVisibleToUser && isResumed()) {
            // 当 Fragment 对用户可见时执行操作（兼容旧版本）
            updatePrinterStatus(PrinterHelper.getInstance().getPrinterStatus());
            PrinterHelper.getInstance().getWirelessPrinterInfo(WirelessPrintStyle.getWirelessPrintStyle()
                    .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE), new IWirelessPrintResult.Stub() {
                @Override
                public void onResult(int i, String s) throws RemoteException {

                }

                @Override
                public void onReturnString(String s) throws RemoteException {
                    Log.d(TAG, "WIRELESS_CONNECT_TYPE: "+s);

                    if (!Utils.isEmpty(s) && !s.equals("0")){
                        PrinterHelper.getInstance().setWirelessPrinterConfig(WirelessPrintStyle.getWirelessPrintStyle()
                                .setWirelessStyle(WirelessConfig.WIRELESS_CONNECT_TYPE)
                                .setConfig(ConnectType.USB.getTypeName()), new IWirelessPrintResult.Stub() {
                            @Override
                            public void onResult(int i, String s) throws RemoteException {

                            }

                            @Override
                            public void onReturnString(String s) throws RemoteException {

                            }
                        });
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);

        binding.recyclerView.setLayoutManager(gridLayoutManager);

        ArrayList<FunctionTestBean> list = new ArrayList<>();
        contents = getResources().getStringArray(R.array.all_test_item_content);
        for (int i = 0; i < contents.length; i++) {
            Log.d(TAG, "initAdapter: " + contents[i]);
            boolean flag = false;
            if (i == contents.length -1) {
                int screenWidth = XUiDisplayHelper.getScreenWidth(getContext());
                int screenHeight = XUiDisplayHelper.getScreenHeight(getContext());

                if (!Utils.isCN()) {
                    flag = screenWidth < screenHeight;
                }
            }
            list.add(new FunctionTestBean(flag ? contents[i]  + "\n" : contents[i], 0));
        }
        AllTestAdapter allTestAdapter = new AllTestAdapter(R.layout.item_all_test, list);
        binding.recyclerView.setAdapter(allTestAdapter);

        allTestAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FunctionTestBean item = allTestAdapter.getItem(position);
                Log.d(TAG, "onItemChildClick: " + item.getTitle());
                switch (position) {
                    case 0:
                        PrinterHelper.getInstance().printerSelfChecking(new INeoPrinterCallback() {
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

                                Log.d(TAG, "onPrintResult: " + code +  ", msg = " + msg);
                            }
                        });
                        break;
                    case 1:
                        PrinterHelper.getInstance().sendRAWData(BytesUtils.getErlmoData(), null);
                        PrinterHelper.getInstance().partialCut();
                        break;
                    case 2:
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
                        PrinterHelper.getInstance().partialCut();
                        break;
                    case 3:
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
                        break;

                    case 4:
                        PrinterHelper.getInstance().sendRAWData(BytesUtils.printBitmap(BytesUtils.initBlackBlock(
                                PrinterHelper.getInstance().getPrinterPaperType() ==
                                        BitmapUtils.PRINTER_TYPE_58 ? BitmapUtils.WIDTH_58_PIXEL : BitmapUtils.WIDTH_80_PIXEL)), null);
                        break;

                    case 5:
                        PrinterHelper.getInstance().sendRAWData(BytesUtils.printBitmap(BytesUtils.initBlackBlock(160,
                                PrinterHelper.getInstance().getPrinterPaperType() ==
                                        BitmapUtils.PRINTER_TYPE_58 ? BitmapUtils.WIDTH_58_PIXEL : BitmapUtils.WIDTH_80_PIXEL)), null);
                        break;
                }
                PrinterHelper.getInstance().printAndFeedPaper(70);
            }
        });

//        allTestAdapter.setOnItemClickListener(new OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//
//
//            }
//        });
    }

    public void updatePrinterStatus(int status) {
        boolean isNormal = (status >= Utils.PRINTER_NORMAL);
        String printerStatusTip = Utils.getPrinterStatusTip(getContext(), status);
        Log.d(TAG, "updateStatus: " + status + ", isNormal= " + isNormal);

        if (binding != null && binding.usbStatusTv != null){
            if (isNormal){
                binding.usbStatusTv.setText(String.format(getString(R.string.status_wifi),"USB"
                        ,getString(R.string.connected)));
            }else {
                binding.usbStatusTv.setText(String.format(getString(R.string.status_wifi),"USB"
                        ,getString(R.string.un_connected)));
            }
        }

    }

}
