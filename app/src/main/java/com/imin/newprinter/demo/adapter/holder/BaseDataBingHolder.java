package com.imin.newprinter.demo.adapter.holder;

/**
 * @Author: Mark
 * @date: 2023/12/7 Time：9:41
 * @description:
 */
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class BaseDataBingHolder extends BaseViewHolder {

    private final ViewDataBinding bind;

    public BaseDataBingHolder(@NonNull View view) {
        super(view);
        bind = DataBindingUtil.bind(view);
    }

    @Nullable
    @Override
    public <B extends ViewDataBinding> B getBinding() {
        return super.getBinding();
    }

}
