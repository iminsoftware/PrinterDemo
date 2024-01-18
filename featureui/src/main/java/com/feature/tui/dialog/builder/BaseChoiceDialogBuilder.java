package com.feature.tui.dialog.builder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.adapter.DialogListContentAdapter;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.layout.MaxRecyclerView;


import java.util.List;

public abstract class BaseChoiceDialogBuilder<T>
        extends BaseDialogBuilder<T> {
    @Nullable
    protected SimpleDataBindingAdapter<DialogItemDescription, ?> mAdapter;
    private float titleTextSize;
    @Nullable
    private List<DialogItemDescription> listData;
    @Nullable
    private Functions.Fun1 onClickListener;
    private DiffUtil.ItemCallback<DialogItemDescription> itemCallback = new DiffUtil.ItemCallback<DialogItemDescription>() {
        @Override
        public boolean areItemsTheSame(@NonNull DialogItemDescription oldItem, @NonNull DialogItemDescription newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull DialogItemDescription oldItem, @NonNull DialogItemDescription newItem) {
            return oldItem == newItem;
        }
    };
    private BaseDataBindingAdapter.OnItemClickListener<DialogItemDescription> itemClickListener = new BaseDataBindingAdapter.OnItemClickListener<DialogItemDescription>() {
        @Override
        public void onItemClick(View view, DialogItemDescription item, int position) {
            itemClick(view, item, position);
            if (onClickListener != null)
                onClickListener.invoke(getMDialog(), position);
        }
    };
    private boolean hasIcon;

    @Nullable
    protected SimpleDataBindingAdapter<DialogItemDescription, ?> getMAdapter() {
        return mAdapter;
    }

    protected void setMAdapter(@Nullable SimpleDataBindingAdapter<DialogItemDescription, ?> simpleDataBindingAdapter) {
        mAdapter = simpleDataBindingAdapter;
    }

    protected float getTitleTextSize() {
        return titleTextSize;
    }

    protected void setTitleTextSize(float f) {
        titleTextSize = f;
    }

    @Nullable
    protected List<DialogItemDescription> getListData() {
        return listData;
    }

    protected void setListData(@Nullable List<DialogItemDescription> list) {
        this.listData = list;
    }

    @Nullable
    protected Functions.Fun1 getOnClickListener() {
        return  onClickListener;
    }

    protected void setOnClickListener(@Nullable Functions.Fun1 function2) {
        onClickListener = function2;
    }

    protected DiffUtil.ItemCallback<DialogItemDescription> getItemCallback() {
        return itemCallback;
    }

    protected BaseDataBindingAdapter.OnItemClickListener<DialogItemDescription> getItemClickListener() {
        return itemClickListener;
    }

    @Override
    @Nullable
    protected View onCreateContent(@Nullable XUiDialog dialog, @Nullable LinearLayout parent, @Nullable Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.xui_recycler_view, null);
        MaxRecyclerView recyclerView = view.findViewById(R.id.xui_recycler_view);
        recyclerView.setMaxHeight(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_430));
        mAdapter = new DialogListContentAdapter(context, itemCallback, this instanceof SingleChoiceDialogBuilder, hasIcon);
        mAdapter.setOnItemClickListener(this.getItemClickListener());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter.submitList(listData);
        return view;
    }

    @Override
    @Nullable
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(@Nullable Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        return layoutParams;
    }

    /**
     * 遍历出默认被选中的条目
     */
    protected abstract void setCheckedItems(@Nullable List<DialogItemDescription> listData);

    /**
     * 设置选中的条目
     */
    protected abstract void itemClick(View view, DialogItemDescription item, int position);

    /**
     * 设置数据源和点击回调监听
     */
    public final T setItems(@Nullable List<DialogItemDescription> listData, Functions.Fun1 onClickListener) {
        this.setCheckedItems(listData);
        this.listData = listData;
        this.onClickListener = onClickListener;
        return (T) this;
    }

    /**
     * 设置item标题的字体大小
     */
    public T setItemTitleTextSize(float textSize) {
        this.titleTextSize = textSize;
        return (T) this;
    }

    public BaseChoiceDialogBuilder(@Nullable Context context, boolean hasIcon) {
        super(context);
        this.hasIcon = hasIcon;
    }

}
