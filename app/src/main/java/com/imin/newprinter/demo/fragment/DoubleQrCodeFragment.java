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

import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.builder.InputDialogBuilder;
import com.feature.tui.dialog.builder.SeekbarDialogBuilder;
import com.feature.tui.dialog.builder.SingleChoiceDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.widget.buttonview.ButtonView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CommonTestAdapter;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentDoubleQrCodeTestBinding;
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
public class DoubleQrCodeFragment extends BaseListFragment<FragmentDoubleQrCodeTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "DoubleQrCodeFragment";
    private String[] contents;
    private String[] values;

    private List<DialogItemDescription> qrSizeList;
    private List<DialogItemDescription> qr1ErrorList;
    private List<DialogItemDescription> qr2ErrorList;
    private List<DialogItemDescription> qrAlignmentList;
    private String qrContent = "12345678";
    private int qr1LeftDis = 10;
    private int mLeftQr1Error = 0;

    private String qr2Content = "12345678";
    private int qr2LeftDis = 150;
    private int mLeftQr2Error = 0;

    private int mQrSize = 5;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_double_qr_code_test;
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
        contents = getResources().getStringArray(R.array.double_qr_code_list);
        values = new String[]{qrContent, String.valueOf(qr1LeftDis) , getQrErrorArray()[mLeftQr1Error],
                qr2Content, String.valueOf(qr2LeftDis), getQrErrorArray()[mLeftQr2Error],
                String.valueOf(mQrSize)};
        super.initData();
        qrSizeList = getQrSizeList(mQrSize -1);
        qr1ErrorList = getQr1ErrorList(mLeftQr1Error);
        qr2ErrorList = getQr2ErrorList(mLeftQr2Error);

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

        printQR.setOnClickListener(v -> {
            Log.d(TAG, "printQR: " + qrContent
                    + ", mQrSize= " + mQrSize
                    + ", mQrError= " + mLeftQr1Error
                    + ", mAlignment= " + mLeftQr2Error

            );
            PrinterHelper.getInstance().setDoubleQRSize(mQrSize);
            PrinterHelper.getInstance().setDoubleQR1MarginLeft(qr1LeftDis);
            PrinterHelper.getInstance().setDoubleQR2MarginLeft(qr2LeftDis);
            PrinterHelper.getInstance().setDoubleQR1Level(mLeftQr1Error);
            PrinterHelper.getInstance().setDoubleQR2Level(mLeftQr2Error);
            PrinterHelper.getInstance().setDoubleQR1Version(10);
            PrinterHelper.getInstance().setDoubleQR2Version(10);

            if (qrContent.isEmpty()) {
                Log.e(TAG, "qrContent is empty ");
                return;
            }
            PrinterHelper.getInstance().printDoubleQR(qrContent,
                    qr2Content, new IPrinterCallback() {
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
            PrinterHelper.getInstance().partialCut();

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

                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(255)
                            .setSeeBarProcess(qr1LeftDis)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                qr1LeftDis = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", qr1LeftDis= " + qr1LeftDis);
                                tvValue.setText(qr1LeftDis + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();


                } else if ((position == 2)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qr1ErrorList, (dialog, i) -> {
                                        qr1ErrorList = getQr1ErrorList(i);
                                        mLeftQr1Error = i;
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
                                            qr2Content = inputDialogBuilder.getInputText();
                                            tvValue.setText(qr2Content);
                                            Log.d(TAG, "invoke: " + i + ", = " + qr2Content);
                                            dialog.dismiss();
                                        }
                                    }, XUiDialogAction.ACTION_PROP_POSITIVE));

                    inputDialogBuilder.setInputText(qr2Content);
                    inputDialogBuilder.create()
                            .show();
                }  else if ((position == 4)) {

                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(255)
                            .setSeeBarProcess(qr2LeftDis)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                qr2LeftDis = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", qr1LeftDis= " + qr2LeftDis);
                                tvValue.setText(qr2LeftDis + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();


                } else if ((position == 5)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qr2ErrorList, (dialog, i) -> {
                                        qr2ErrorList = getQr1ErrorList(i);
                                        mLeftQr2Error = i;
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

                } else if (position == 6) {
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

    private List<DialogItemDescription> getQr1ErrorList(int checkIndex) {
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

    private List<DialogItemDescription> getQr2ErrorList(int checkIndex) {
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
