package com.imin.newprinter.demo.fragment;

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
import com.feature.tui.util.XUiDisplayHelper;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.AllTestAdapter;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentAllTestBinding;
import com.imin.newprinter.demo.utils.BitmapUtils;
import com.imin.newprinter.demo.utils.BytesUtils;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class AllTestFragment extends BaseListFragment<FragmentAllTestBinding, FragmentCommonViewModel, AllTestAdapter> {

    private static final String TAG = "AllTestFragment";
    private String[] contents;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_all_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
    }

    @Override
    public void initData() {
        contents = getResources().getStringArray(R.array.all_test_item_content);
        super.initData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        getRvAdapter().addChildClickViewIds(R.id.tv_item_all_test_print);
        getRvAdapter().setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FunctionTestBean item = getRvAdapter().getItem(position);
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
    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        return null;
    }

    @Override
    protected AllTestAdapter initAdapter() {
        ArrayList<FunctionTestBean> list = new ArrayList<>();

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

        return new AllTestAdapter(R.layout.item_all_test, list);
    }
}
