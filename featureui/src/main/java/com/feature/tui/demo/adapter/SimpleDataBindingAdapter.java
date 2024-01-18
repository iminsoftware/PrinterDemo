package com.feature.tui.demo.adapter;

import android.content.Context;

import androidx.recyclerview.widget.DiffUtil;

public abstract class SimpleDataBindingAdapter<M, B extends BaseDataBindingAdapter.BaseBindingViewHolder>
        extends BaseDataBindingAdapter<M, B> {
    private final int layoutId;

    @Override
    public int getLayoutId(int viewType) {
        return this.layoutId;
    }

    public SimpleDataBindingAdapter(Context context, int layoutId, DiffUtil.ItemCallback<M> diffCallback) {
        super(context, diffCallback);
        this.layoutId = layoutId;
    }

}
