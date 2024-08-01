package com.imin.newprinter.demo.fragment;

import android.app.Dialog;
import android.os.Bundle;
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
import com.imin.newprinter.demo.databinding.FragmentBarcodeTestBinding;
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
public class BarcodeFragment extends BaseListFragment<FragmentBarcodeTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "BarcodeFragment";
    private String[] contentList;
    private String[] barcodeTypeTips;
    private String[] barcodeContent;
    private String[] barcodeValue;

    private String content = "123456789012";
    private int mBarCodeType = 0;
    private int mTextPosition = 0;
    private int mHeightProgress = 80;
    private int mWidthProgress = 2;
    private int mAlignment = 0;

    private List<DialogItemDescription> barcodeTypeList;
    private List<DialogItemDescription> barcodePositionList;

    private List<DialogItemDescription> qrAlignmentList;
    private ArrayList<FunctionTestBean> list;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_barcode_test;
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
        contentList = getResources().getStringArray(R.array.bar_code_list);
        barcodeTypeTips = getResources().getStringArray(R.array.barcode_type_tips);
        barcodeContent = getResources().getStringArray(R.array.barcode_content);
        barcodeValue = getResources().getStringArray(R.array.barcode_def_value);

        super.initData();
        barcodeTypeList = getBarcodeTypeList(0);
        barcodePositionList = getBarcodePositionList(0);

        qrAlignmentList = getAlignmentList(0);

    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    protected CommonTestAdapter initAdapter() {
        list = new ArrayList<>();

        for (int i = 0; i < contentList.length; i++) {
            FunctionTestBean bean = new FunctionTestBean(contentList[i], barcodeValue[i]);
            list.add(bean);
        }

        return new CommonTestAdapter(R.layout.item_common, list);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
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
                            .setBrTips(barcodeTypeTips[mBarCodeType], com.feature.tui.R.color.xui_config_color_error, null)
                            .setHintInputText(getString(R.string.set_pls_content))
                            .addAction(new XUiDialogAction(getString(R.string.action_cancel),
                                    new Functions.Fun1() {
                                        @Override
                                        public void invoke(Dialog dialog, int i) {
                                            Log.d(TAG, "invoke: " + i);
                                            dialog.dismiss();
                                        }
                                    }))
                            .setEditTextCount(30)
                            .setCanceledOnTouchOutside(false)
//                            .isShowLengthOverTips(true)
                            .setCancelable(false)
                            .isShowDelete(true)
//                            .setErrorText(barcodeTypeTips[mBarCodeType])
                            .addAction(new XUiDialogAction(getString(R.string.action_confirm),
                                    new Functions.Fun1() {
                                        @Override
                                        public void invoke(Dialog dialog, int i) {
                                            content = inputDialogBuilder.getInputText();
                                            tvValue.setText(content);
                                            FunctionTestBean bean = list.get(0);
                                            bean.setValue(content);
                                            Log.d(TAG, "invoke: " + i + ", = " + content);
                                            dialog.dismiss();
                                        }
                                    }, XUiDialogAction.ACTION_PROP_POSITIVE));

                    inputDialogBuilder.setInputText(content);
                    inputDialogBuilder.create()
                            .show();
                } else if ((position == 1)) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(barcodeTypeList, (dialog, i) -> {
                                Log.d(TAG, "onItemClick: " + i);
                                barcodeTypeList = getBarcodeTypeList(i);
                                mBarCodeType = i;
                                        String[] array = getBarcodeTypeArray();
                                        if (array != null) {
                                            String str = array[i];
                                            tvValue.setText(str);

                                            content = barcodeContent[i];

                                            FunctionTestBean bean = list.get(0);
                                            bean.setValue(content);

                                            FunctionTestBean bean1 = list.get(1);
                                            bean1.setValue(str);

                                            getRvAdapter().notifyItemChanged(0);
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
                            .setItems(barcodePositionList, (dialog, i) -> {
                                barcodePositionList = getBarcodePositionList(i);
                                        mTextPosition = i;
                                        String[] array = getBarcodePositionArray();
                                        if (array != null) {
                                            tvValue.setText(array[i]);
                                            FunctionTestBean bean = list.get(2);
                                            bean.setValue(array[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();

                } else if (position == 3) {

                    Log.d(TAG, "onItemClick: mHeightProgress = " + mHeightProgress);
                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(255)
                            .setSeeBarProcess(mHeightProgress)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                mHeightProgress = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + mHeightProgress);
                                tvValue.setText(mHeightProgress + "");
                                FunctionTestBean bean = list.get(3);
                                bean.setValue(mHeightProgress + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                } else if (position == 4) {

                    Log.d(TAG, "onItemClick: " + mWidthProgress);
                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(6)
                            .setSeeBarMinProcess(1)
                            .setSeeBarProcess(mWidthProgress)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                mWidthProgress = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + mWidthProgress);
                                tvValue.setText(mWidthProgress + "");
                                FunctionTestBean bean = list.get(4);
                                bean.setValue(mWidthProgress + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                } else if (position == 5) {
                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(qrAlignmentList, (dialog, i) -> {
                                        qrAlignmentList = getAlignmentList(i);
                                        mAlignment = i;
                                        String[] alignmentArray = getAlignmentArray();
                                        if (alignmentArray != null) {
                                            tvValue.setText(alignmentArray[i]);
                                            FunctionTestBean bean = list.get(5);
                                            bean.setValue(alignmentArray[i] );
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

        ButtonView print = binding.getRoot().findViewById(R.id.print);
        print.setOnClickListener(v -> {
            Log.d(TAG, "print: " + content + ", mBarCodeType= " + mBarCodeType
                + ", mWidthProgress= " + mWidthProgress
                + ", mHeightProgress= " + mHeightProgress
                + ", mTextPosition= " + mTextPosition
                + ", mAlignment= " + mAlignment
            );
            PrinterHelper.getInstance().printBarCodeWithFull(content, mBarCodeType, mWidthProgress, mHeightProgress
                , mTextPosition, mAlignment, new INeoPrinterCallback() {

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
            PrinterHelper.getInstance().partialCut();
        });

    }

    private String[] getBarcodeTypeArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.barcode_type);
        return array;
    }

    private List<DialogItemDescription> getBarcodeTypeList(int checkIndex) {
        String[] array = getBarcodeTypeArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            DialogItemDescription item = new DialogItemDescription(array[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }

    private String[] getBarcodePositionArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.barcode_position);
        return array;
    }

    private List<DialogItemDescription> getBarcodePositionList(int checkIndex) {
        String[] array = getBarcodePositionArray();
        List<DialogItemDescription> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            DialogItemDescription item = new DialogItemDescription(array[i]);
            if (checkIndex == i) {
                item.setChecked(true);
            }
            list.add(item);
        }

        return list;
    }



    private String[] getAlignmentArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.alignment);
        return array;
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
