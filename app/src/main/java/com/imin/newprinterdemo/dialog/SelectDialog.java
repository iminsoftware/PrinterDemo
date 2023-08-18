package com.imin.newprinterdemo.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imin.newprinterdemo.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingInflatedId")
public class SelectDialog extends BaseDialog {

    private TextView setTitleDialog;
    private FrameLayout flClose;
    private RecyclerView rvDialog;
    private BaseQuickAdapter<SelectDialogBean, BaseViewHolder> adapter;
    private BaseQuickAdapter<String, BaseViewHolder> adapterStr;
    private List<SelectDialogBean> list = new ArrayList<>();

    public SelectDialog(@NonNull Context context) {
        super(context);
    }

    public SelectDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.select_dialog);
    }

    public SelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private int isStringList = 0;

    public SelectDialog setIsStringList(int isStringList) {
        this.isStringList = isStringList;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_dialog);
        setTitleDialog = findViewById(R.id.setTitleDialog);
        flClose = findViewById(R.id.flClose);
        rvDialog = findViewById(R.id.rvDialog);
        flClose.setOnClickListener(v -> {
            if (clickListener != null){
                clickListener.dismiss();
            }
            dismiss();
        });
        setTitleDialog.setText(title);
        rvDialog.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new BaseQuickAdapter<SelectDialogBean, BaseViewHolder>(R.layout.layout_select_dialog_item) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, SelectDialogBean functionBean) {
                TextView rvTitle = viewHolder.getView(R.id.rvSelectText);
                if (functionBean != null){
                    rvTitle.setText(functionBean.getTitle());
                }
            }
        };
        adapterStr = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_select_dialog_item) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, String functionBean) {
                TextView rvTitle = viewHolder.getView(R.id.rvSelectText);
                if (functionBean != null){
                    rvTitle.setText(functionBean);
                }
            }
        };
        adapter.setNewData(new ArrayList<>());
        adapterStr.setNewData(new ArrayList<>());

        adapter.setNewData(list);
        adapterStr.setNewData(stringList);
        rvDialog.setAdapter(isStringList == 0?adapterStr:adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (list != null){
                    SelectDialogBean selectDialogBean = list.get(position);
                    if (selectDialogBean != null){
                        if (clickListener != null){
                            clickListener.selectItem(selectDialogBean.getTitle(),selectDialogBean.getId());
                        }
                    }

                }
               dismiss();
            }
        });
        adapterStr.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (stringList != null){
                    String s = stringList.get(position);
                    if (s != null){
                        if (clickListener != null){
                            clickListener.selectItem(s,position);
                        }
                    }

                }
                dismiss();
            }
        });
    }
    public SelectDialog setRvData(List<SelectDialogBean> list1){
        this.list = list1;
        return this;
    }

    List<String> stringList = new ArrayList<>();
    public SelectDialog setRvStringListData(List<String> list1){
        this.stringList = list1;
        return this;
    }





}
