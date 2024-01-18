package com.imin.newprinter.demo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.modle.DialogItemDescription;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.adapter.PrinterParameterAdapter;
import com.imin.newprinter.demo.databinding.FragmentPrinterParameterBinding;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class PrinterParameterFragment extends BaseListFragment<FragmentPrinterParameterBinding, FragmentCommonViewModel, PrinterParameterAdapter> {

    private static final String TAG = "PrinterParameterFragment";
    private String[] contentArray;

    private List<DialogItemDescription> list;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_printer_parameter;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
    }

    @Override
    public void initData() {
        contentArray = getParameterArray();
        super.initData();

    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    protected PrinterParameterAdapter initAdapter() {

        if (contentArray == null) {
            contentArray = getParameterArray();
        }
        if (list == null) {
            list = new ArrayList<DialogItemDescription>();
        }
        for (int i = 0; i < contentArray.length; i++) {
            list.add(new DialogItemDescription(contentArray[i]));
        }

        return new PrinterParameterAdapter(R.layout.item_parameter, list);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();


        PrinterHelper.getInstance().getPrinterSerialNumber(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {

                Log.d(TAG, "getPrinterSerialNumber: " + result);
                updateParameterList(0, result);

                getActivity().runOnUiThread(()-> {
                    getRvAdapter().notifyItemChanged(0);
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterModelName(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterModelName: " + result);
                updateParameterList(1, result);
                getActivity().runOnUiThread(()-> {
                    getRvAdapter().notifyItemChanged(1);
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterThermalHead(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterThermalHead: " + result);
                updateParameterList(2, result);
                getActivity().runOnUiThread(()-> {
                    getRvAdapter().notifyItemChanged(2);
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        PrinterHelper.getInstance().getPrinterFirmwareVersion(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterFirmwareVersion: " + result);
                updateParameterList(3, result);
                getActivity().runOnUiThread(()-> {
                    getRvAdapter().notifyItemChanged(3);
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });

        String serviceVersion = PrinterHelper.getInstance().getServiceVersion();
        updateParameterList(4, serviceVersion);
        getRvAdapter().notifyItemChanged(4);
        String paperType = PrinterHelper.getInstance().getPrinterPaperType() + "";
        updateParameterList(5, paperType);
        getRvAdapter().notifyItemChanged(5);
        Log.d(TAG, "initViewObservable version: " + serviceVersion + ", type= " + paperType);

        PrinterHelper.getInstance().getPrinterPaperDistance(new INeoPrinterCallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                Log.d(TAG, "getPrinterPaperDistance: " + result);
                updateParameterList(6, result);
                SystemClock.sleep(500);
                getActivity().runOnUiThread(()->{

                    getRvAdapter().notifyItemChanged(6);
                });
            }

            @Override
            public void onRaiseException(int code, String msg) throws RemoteException {

            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {

            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        getRvAdapter().setNewInstance(null);
        list.clear();
    }

    private String[] getParameterArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.printer_parameter_list);
        return array;
    }


    private void updateParameterList(int checkIndex, String value) {

        for (int i = 0; i < list.size(); i++) {
            DialogItemDescription item = list.get(i);
            if (checkIndex == i && !TextUtils.isEmpty(value)) {
                item.setDescription(value);
            }
        }
    }

    /**
     * String serviceVersion = PrinterHelper.getInstance().getServiceVersion(); 服务版本号
     * getPrinterModelName  打印机型号
     * getPrinterThermalHead 打印头型号
     * getPrinterFirmwareVersion 固件版本号
     * PrinterHelper.getInstance().getPrinterPaperType()
     * PrinterHelper.getInstance().getPrinterPaperDistance();
     */

}
