package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
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

import com.feature.tui.dialog.builder.AgreementDialogBuilder;
import com.feature.tui.dialog.builder.SingleChoiceDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.util.XUiDisplayHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CommonTestAdapter;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentPictureTestBinding;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class SettingFragment extends BaseListFragment<FragmentPictureTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "SettingFragment";
    private String[] contentList;

    private List<DialogItemDescription> mConnectModeList;
    private int mConnectMode = 0;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_setting;
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
        int screenWidth = XUiDisplayHelper.getScreenWidth(getContext());
        int screenHeight = XUiDisplayHelper.getScreenHeight(getContext());


        if (screenWidth > screenHeight) {
            contentList = getResources().getStringArray(R.array.settings_list_land);
        } else {
            contentList = getResources().getStringArray(R.array.settings_list);
        }
        super.initData();

        mConnectModeList = getConnectModeList(0);

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
            if (i == 0) {

                list.add(new FunctionTestBean(contentList[i], "AIDL"));
            } else {
                list.add(new FunctionTestBean(contentList[i], 0));
            }
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

                if (item.getTitle().equals(contentList[0])) {
                    showChoiceDialog(item, tvValue);
                } else if (item.getTitle().equals(contentList[1])) {

                    if (contentList.length == 2) {
                        showAgreementDialog(item);
                    } else {
                        if (leftCallback != null) {

                            leftCallback.nextPage(8);
                        }
                    }

                } else if (item.getTitle().equals(contentList[2])) {

                    showAgreementDialog(item);
                }

            }
        });


    }

    private void showAgreementDialog(FunctionTestBean item) {
        AgreementDialogBuilder builder = new AgreementDialogBuilder(getActivity());
        builder.setTitle(item.getTitle())
                .setMessage(getString(R.string.about_printer_demo))
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .addAction(getString(R.string.close), (dialog, i) -> {
                    Log.d(TAG, "onItemClick: i= " + i);
                    dialog.dismiss();
                });

        builder.create().show();
    }

    private void showChoiceDialog(FunctionTestBean item, TextView tvValue) {
        SingleChoiceDialogBuilder singleBuilder = new SingleChoiceDialogBuilder(getContext(), false);
        XUiDialog mSelectDialog = singleBuilder.setTitle(item.getTitle())
                .setItems(mConnectModeList, (dialog, i) -> {
                            mConnectModeList = getConnectModeList(i);
                            mConnectMode = i;
                            String[] array = getConnectModeArray();
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
    }

    private String[] getConnectModeArray() {
        String[] array = getActivity().getResources().getStringArray(R.array.connecting_mode);
        return array;
    }

    private List<DialogItemDescription> getConnectModeList(int checkIndex) {
        String[] qrSizeArray = getConnectModeArray();
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
