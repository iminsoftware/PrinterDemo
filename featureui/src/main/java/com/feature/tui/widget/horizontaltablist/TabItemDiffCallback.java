package com.feature.tui.widget.horizontaltablist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Description:
 *
 * @Author: hulq
 * Date: 2021/4/27 14:21
 */
public class TabItemDiffCallback extends DiffUtil.ItemCallback<HorizontalTabItem> {
    @Override
    public boolean areItemsTheSame(@NonNull HorizontalTabItem oldItem, @NonNull HorizontalTabItem newItem) {
        return oldItem.getType() == newItem.getType();
    }

    @Override
    public boolean areContentsTheSame(@NonNull HorizontalTabItem oldItem, @NonNull HorizontalTabItem newItem) {
        return oldItem == newItem;
    }
}
