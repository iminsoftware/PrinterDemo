package com.feature.tui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter.OnItemClickListener;
import com.feature.tui.dialog.Functions;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.layout.MaxRecyclerView;

import java.util.List;

public class ListPopup<T> extends NormalPopup {
    protected List listData;
    private Functions.Fun4 onClickListener;
    protected OnItemClickListener itemClickListener;
    protected DiffUtil.ItemCallback itemCallback;
    protected int maxHeight;
    protected int itemHeight;

    public ListPopup(@NonNull Context context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initPop(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.xui_popup_max_height));
    }

    public ListPopup(@NonNull Context context, int width, int maxHeight) {
        super(context, width, -2);
        initPop(maxHeight);
    }

    public ListPopup(@NonNull Context context, int width) {
        super(context, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        initPop(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.xui_popup_max_height));
    }

    protected void initPop(int maxHeight) {
        this.maxHeight = maxHeight;
        itemClickListener = (OnItemClickListener<T>) (var1, item, position) -> {
            if (onClickListener != null) {
                onClickListener.invoke(item, position);
            }
        };
        itemCallback = new ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem == newItem;
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem == newItem;
            }
        };
    }

    public ListPopup setItems(List listData, Functions.Fun4 onClickListener) {
        this.listData = listData;
        this.onClickListener = onClickListener;
        view(onCreateContent(mContext));
        return this;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    protected View onCreateContent(Context context) {
        MaxRecyclerView recyclerView = new MaxRecyclerView(context, maxHeight);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        BaseDataBindingAdapter adapter = createAdapter();
        adapter.setOnItemClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.submitList(listData);
        return recyclerView;
    }

    protected BaseDataBindingAdapter createAdapter() {
        ListPopupAdapter adapter = new ListPopupAdapter(mContext, itemCallback, R.layout.popup_list_item_layout, this.itemHeight);
        return adapter;
    }

}
