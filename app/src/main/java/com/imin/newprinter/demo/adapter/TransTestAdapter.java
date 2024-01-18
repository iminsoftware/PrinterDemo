package com.imin.newprinter.demo.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.modle.DialogItemDescription;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imin.newprinter.demo.adapter.holder.BaseDataBingHolder;
import com.imin.newprinter.demo.databinding.ItemTransBinding;

import java.util.List;

/**
 * @Author: Mark
 * @date: 2023/12/7 Time：17:06
 * @description:
 */
public class TransTestAdapter extends BaseQuickAdapter<DialogItemDescription, BaseDataBingHolder> {

    private static final String TAG = "TransTestAdapter";

    public TransTestAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(@NonNull BaseDataBingHolder baseViewHolder, DialogItemDescription bean) {
        Log.d(TAG, "convert: bean= " + bean.toString());

        ItemTransBinding binding = baseViewHolder.getBinding();

        binding.setData(bean);
    }


    @NonNull
    @Override
    public BaseDataBingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }
}
