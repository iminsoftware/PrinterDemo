package com.imin.newprinter.demo.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imin.newprinter.demo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("MissingInflatedId")
public class SelectDialog extends BaseDialog {

    private static final String TAG = "SelectDialog";
    private TextView setTitleDialog;
    private TextView flClose;
    private RecyclerView rvDialog;
    private BaseQuickAdapter<SelectDialogBean, BaseViewHolder> adapter;
    private BaseQuickAdapter<String, BaseViewHolder> adapterStr;
    private List<SelectDialogBean> list = new ArrayList<>();
    protected ArrayList<CharSequence> items;
    private List<String> stringList;
    private String mTitle = "";

    private int mIndex = -1;

    public SelectDialog(@NonNull Context context) {
        super(context, R.layout.set_dialog);
        Log.d(TAG, "SelectDialog: ");
//        initView();
    }

//    public SelectDialog(Context context, @NonNull QrBarCodeInfo tips) {
//        super(context, R.layout.set_dialog);
////        initView();
//
//    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public void updateContent(int index, String tips) {
        mIndex = index;
//        updateTips(tips);
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
        initView();
    }

    private void initView() {
        Log.d(TAG, "initView: ");
        setTitleDialog = findViewById(R.id.tv_title);
        flClose = findViewById(R.id.tvCancel);
        rvDialog = findViewById(R.id.rv);

        setTitleDialog.setText(mTitle);



        adapterStr = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_select_dialog_item) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, String functionBean) {
                Log.d(TAG, "convert: functionBean= " + functionBean);
                TextView rvTitle = viewHolder.getView(R.id.rvSelectText);
                if (functionBean != null) {
                    rvTitle.setText(functionBean);
                }
            }
        };

//        adapterStr.setNewData(new ArrayList<>());
        adapterStr.setNewData(stringList);
        rvDialog.setAdapter(adapterStr);


        adapterStr.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (stringList != null) {
                    String bean = stringList.get(position);
                    if (bean != null) {
                        if (clickListener != null) {
                            clickListener.selectItem(bean, position);
                        }
                    }

                }
                dismiss();
            }
        });


    }

    private ClickListener clickListener;

    public SelectDialog setRvData(List<SelectDialogBean> list1) {
        this.list = list1;
        return this;
    }

    public SelectDialog setRvStringListData(List<String> list) {
        Log.d(TAG, "setRvStringListData: " + Arrays.toString(list.toArray()));
        this.stringList = list;

        return this;
    }


    public SelectDialog setTitle(String title) {
        mTitle = title;
        if (setTitleDialog != null) {

            setTitleDialog.setText(mTitle);
        }
        return this;
    }


}
