package com.feature.tui.popup;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;

import com.feature.tui.R;
import com.feature.tui.R.drawable;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;
import com.feature.tui.modle.PopupItemDescription;

public class ListPopupAdapter extends SimpleDataBindingAdapter<PopupItemDescription, ListPopupAdapter.PopupListViewHolder> {

    private int itemHeight;

    public ListPopupAdapter(@NonNull Context context, @Nullable ItemCallback diffCallback, int layoutId) {
        super(context, layoutId, diffCallback);
    }

    public ListPopupAdapter(@NonNull Context context, @Nullable ItemCallback diffCallback, int layoutId, int itemHeight) {
        super(context, layoutId, diffCallback);
        this.itemHeight = itemHeight;
    }

    @Override
    public PopupListViewHolder createMyViewHolder(View view, ViewGroup parent, int viewType) {
        return new PopupListViewHolder(view);
    }

    @Override
    public void onBindItem(@NonNull PopupListViewHolder holder, @NonNull PopupItemDescription item, int position) {
        holder.popupItemTitle.setText(item.getTitle());

        if (item.getResId() > 0) {
            holder.popupItemIcon.setVisibility(View.VISIBLE);
            holder.popupItemIcon.setImageResource(item.getResId());
        } else {
            holder.popupItemIcon.setVisibility(View.GONE);
        }

        holder.itemView.setEnabled(item.isEnable());
        holder.popupItemTitle.setEnabled(item.isEnable());

        if (position == 0) {
            holder.itemView.setBackgroundResource(drawable.list_item_bg_top);
        } else if (position == getItemCount() - 1) {
            holder.itemView.setBackgroundResource(drawable.list_item_bg_bottom);
        } else {
            holder.itemView.setBackgroundResource(drawable.list_item_bg);
        }

        if (position == 0 && this.getItemCount() == 1) {
            holder.itemView.setBackgroundResource(drawable.list_item_bg_all);
        }
    }

    class PopupListViewHolder extends BaseDataBindingAdapter.BaseBindingViewHolder {

        TextView popupItemTitle;

        ImageView popupItemIcon;

        PopupListViewHolder(View itemView) {
            super(itemView);
            if (itemHeight > 0) {
                ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                lp.height = itemHeight;
                itemView.setLayoutParams(lp);
            }
            popupItemTitle = itemView.findViewById(R.id.popup_item_title);
            popupItemIcon = itemView.findViewById(R.id.popup_item_icon);
        }

    }

}
