package com.imin.newprinter.demo.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.imin.newprinter.demo.databinding.FragmentTextTestBinding;
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
public class TextFragment extends BaseListFragment<FragmentTextTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "TextPicFragment";

    private String[] contentList;
    private String[] values;


    private String content = "iMin committed to use advanced technologies to help our business partners digitize their business.We are dedicated in becoming a leading provider of smart business equipment in ASEAN countries,assisting our partners to connect, create and utilize data effectively.\n";
    private int fontSize = 24;
    private int lineSpace = 1;
    private int font = 0;
    private int mAlignment = 0;
    private int textSpace = 0;

    private List<DialogItemDescription> fontList;
    private List<DialogItemDescription> barcodePositionList;

    private List<DialogItemDescription> qrAlignmentList;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_text_test;
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
        contentList = getResources().getStringArray(R.array.text_list);
        values = new String[]{content, String.valueOf(fontSize),
                String.valueOf(lineSpace), getFontArray()[font],
                getAlignmentArray()[mAlignment], String.valueOf(textSpace)};

        super.initData();
        fontList = getFontList(font);

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
    protected CommonTestAdapter initAdapter() {
        ArrayList<FunctionTestBean> list = new ArrayList<>();

        for (int i = 0; i < contentList.length; i++) {
            list.add(new FunctionTestBean(contentList[i], values[i]));
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
                            .setHintInputText(getString(R.string.set_pls_content))
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
                                            content = inputDialogBuilder.getInputText();
                                            tvValue.setText(content);
                                            Log.d(TAG, "invoke: " + i + ", = " + content);
                                            dialog.dismiss();
                                        }
                                    }, XUiDialogAction.ACTION_PROP_POSITIVE));

                    inputDialogBuilder.setInputText(content);
                    inputDialogBuilder.create()
                            .show();
                } else if ((position == 1)) {

                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMinProcess(12)
                            .setSeeBarMaxProcess(36)
                            .setSeeBarProcess(fontSize)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                fontSize = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + fontSize);
                                tvValue.setText(fontSize + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                } else if ((position == 2)) {

                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(6)
                            .setSeeBarProcess(lineSpace)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                lineSpace = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + lineSpace);
                                tvValue.setText(lineSpace + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                } else if (position == 3) {

                    SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);

                    XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                            .setItems(fontList, (dialog, i) -> {
                                fontList = getFontList(i);
                                        font = i;
                                        String[] array = getFontArray();
                                        if (array != null) {
                                            tvValue.setText(array[i]);
                                        }
                                        dialog.dismiss();
                                    }
                            ).addAction(getString(R.string.action_cancel), ((dialog, i) -> {

                                dialog.dismiss();
                            }))
                            .create();
                    mSelectDialog.show();

                } else if (position == 4) {
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

                } else if (position == 5) {
                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(255)
                            .setSeeBarProcess(textSpace)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                textSpace = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + textSpace);
                                tvValue.setText(textSpace + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();

                }

            }
        });

        ButtonView print = binding.getRoot().findViewById(R.id.print);
        CheckBox ckUnderLine = binding.getRoot().findViewById(R.id.ck_under_line);
        CheckBox ckStrikeThrough = binding.getRoot().findViewById(R.id.ck_strike_through);
        CheckBox ckAntiWhite = binding.getRoot().findViewById(R.id.ck_anti_white);

//        ckUnderLine.setOnCheckedChangeListener((v, c)->{
//            Log.d(TAG, "ckUnderLine: " + c);
//        });
//
//        ckStrikeThrough.setOnCheckedChangeListener((v, c)->{
//            Log.d(TAG, "ckStrikeThrough: " + c);
//
//        });
//
//        ckAntiWhite.setOnCheckedChangeListener((v, c)->{
//            Log.d(TAG, "ckAntiWhite: " + c);
//
//        });

        print.setOnClickListener(v -> {
            Log.d(TAG, "print: " + content + ", fontSize= " + fontSize
                    + ", lineSpace= " + lineSpace
                    + ", font= " + font
                    + ", mAlignment= " + mAlignment
                    + ", textSpace= " + textSpace
                    + ", ckUnderLine= " + ckUnderLine.isChecked()
                    + ", ckStrikeThrough= " + ckStrikeThrough.isChecked()
                    + ", ckAntiWhite= " + ckAntiWhite.isChecked()
            );
            PrinterHelper.getInstance().setTextBitmapStrikeThru(ckStrikeThrough.isChecked());
            PrinterHelper.getInstance().setTextBitmapUnderline(ckUnderLine.isChecked());
            PrinterHelper.getInstance().setTextBitmapAntiWhite(ckAntiWhite.isChecked());

            PrinterHelper.getInstance().setTextBitmapSize(fontSize);

            PrinterHelper.getInstance().setTextBitmapLineSpacing(lineSpace);

            PrinterHelper.getInstance().setTextBitmapStyle(font);

            PrinterHelper.getInstance().setTextBitmapLetterSpacing((float) (textSpace/80));

            PrinterHelper.getInstance().printTextBitmapWithAli(content, mAlignment, new INeoPrinterCallback() {

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

    private String[] getFontArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.text_font);
        return array;
    }

    private List<DialogItemDescription> getFontList(int checkIndex) {
        String[] array = getFontArray();
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

