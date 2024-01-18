package com.imin.newprinter.demo.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.builder.InputDialogBuilder;
import com.feature.tui.dialog.builder.SingleChoiceDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.widget.buttonview.ButtonView;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CommonTestAdapter;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentQrCodeTestBinding;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.IPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class QrCodeFragment extends BaseListFragment<FragmentQrCodeTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "QrCodeFragment";
    private String[] contents;
    private String[] values;


    private List<DialogItemDescription> qrSizeList;
    private List<DialogItemDescription> qrErrorList;
    private List<DialogItemDescription> qrAlignmentList;
    private String qrContent = "12345678";

    private int mQrSize = 5;
    private int mAlignment = 0;
    private int mQrError = 0;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_qr_code_test;
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
        contents = getResources().getStringArray(R.array.qr_code_list);
        values = new String[]{qrContent, String.valueOf(mQrSize), getQrErrorArray()[mQrError], getAlignmentArray()[mAlignment]};
        super.initData();
        qrSizeList = getQrSizeList(mQrSize -1);
        qrErrorList = getQrErrorList(mQrError);
        qrAlignmentList = getAlignmentList(mAlignment);

    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        ButtonView printQR = binding.getRoot().findViewById(R.id.print);
        ButtonView printDoubleQR = binding.getRoot().findViewById(R.id.double_qr_print);

        printQR.setOnClickListener(v -> {
            Log.d(TAG, "printQR: " + qrContent
                    + ", mQrSize= " + mQrSize
                    + ", mQrError= " + mQrError
                    + ", mAlignment= " + mAlignment

            );
            PrinterHelper.getInstance().printQRCodeWithFull(qrContent, mQrSize, mQrError, mAlignment, new IPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {
                            Log.d(TAG, "  onRunResult ====>    " + isSuccess);
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

                        @Override
                        public IBinder asBinder() {
                            return null;
                        }
                    });
            PrinterHelper.getInstance().printAndFeedPaper(70);
            PrinterHelper.getInstance().partialCut();
        });

        printDoubleQR.setOnClickListener(v -> {
            Log.d(TAG, "printDoubleQR: " + (leftCallback != null));
            if (leftCallback != null) {
                leftCallback.nextPage(9);
            }
        });

        getRvAdapter().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FunctionTestBean item = getRvAdapter().getItem(position);
                Log.d(TAG, "onItemChildClick: " + item.getTitle());
                TextView tvValue = view.findViewById(R.id.tv_item_value);

                if (position == 0) {
                    InputDialogBuilder inputDialogBuilder = new InputDialogBuilder(getActivity());
                    inputDialogBuilder
                            .setTitle(item.getTitle())
                            .setHintInputText(getString(R.string.set_qr_pls_content))
                            .addAction(new XUiDialogAction(getString(R.string.action_cancel),
                                    new Functions.Fun1() {
                                        @Override
                                        public void invoke(Dialog dialog, int i) {
                                            Log.d(TAG, "invoke: " + i);
                                            dialog.dismiss();
                                        }
                                    }))
                            .setEditTextCount(500)
                            .setCanceledOnTouchOutside(false)
                            .isShowLengthOverTips(true)
                            .setCancelable(false)
                            .isShowDelete(true)
                            .addAction(new XUiDialogAction(getString(R.string.action_confirm),
                                    new Functions.Fun1() {
                                        @Override
                                        public void invoke(Dialog dialog, int i) {
                                            qrContent = inputDialogBuilder.getInputText();
                                            tvValue.setText(qrContent);
                                            Log.d(TAG, "invoke: " + i + ", = " + qrContent);
                                            dialog.dismiss();
                                        }
                                    }, XUiDialogAction.ACTION_PROP_POSITIVE));

                    inputDialogBuilder.setInputText(qrContent);
                    inputDialogBuilder.create()
                            .show();
                } else if ((position == 1)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qrSizeList, (dialog, i) -> {
                                        qrSizeList = getQrSizeList(i);
                                        mQrSize = i + 1;
                                        String[] sizeArray = getQrSizeArray();
                                        if (sizeArray != null) {
                                            tvValue.setText(sizeArray[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();

                } else if ((position == 2)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qrErrorList, (dialog, i) -> {
                                        qrErrorList = getQrErrorList(i);
                                        mQrError = i;
                                        String[] errorArray = getQrErrorArray();
                                        if (errorArray != null) {
                                            tvValue.setText(errorArray[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();

                } else if ((position == 3)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qrAlignmentList, (dialog, i) -> {
                                        qrAlignmentList = getAlignmentList(i);
                                        mAlignment = i;
                                        String[] alignmentArray = getAlignmentArray();
                                        if (alignmentArray != null) {
                                            tvValue.setText(alignmentArray[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();
                }

            }
        });

    }

    @Override
    protected CommonTestAdapter initAdapter() {
        ArrayList<FunctionTestBean> list = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            list.add(new FunctionTestBean(contents[i], values[i]));
        }

        return new CommonTestAdapter(R.layout.item_common, list);
    }

    private String[] getQrSizeArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.qrcode_size);
        return array;
    }

    private String[] getQrErrorArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.qrcode_error);
        return array;
    }

    private String[] getAlignmentArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.alignment);
        return array;
    }

    private List<DialogItemDescription> getQrSizeList(int checkIndex) {
        String[] qrSizeArray = getQrSizeArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < qrSizeArray.length; i++) {
            DialogItemDescription item = new DialogItemDescription(qrSizeArray[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }

    private List<DialogItemDescription> getQrErrorList(int checkIndex) {
        String[] qrSizeArray = getQrErrorArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < qrSizeArray.length; i++) {
            DialogItemDescription item = new DialogItemDescription(qrSizeArray[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }

    private List<DialogItemDescription> getAlignmentList(int checkIndex) {
        String[] qrSizeArray = getAlignmentArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < qrSizeArray.length; i++) {
            DialogItemDescription item = new DialogItemDescription(qrSizeArray[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }
}
