package com.feature.tui.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;

import com.feature.tui.R;
import com.feature.tui.R.drawable;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;

public class SimpleCornerAdapter extends SimpleDataBindingAdapter<String, SimpleCornerAdapter.PopupListViewHolder> implements View.OnClickListener, Filterable {

    private int itemHeight;

    public SimpleCornerAdapter(@NonNull Context context, @Nullable ItemCallback diffCallback, int layoutId) {
        super(context, layoutId, diffCallback);
    }

    public SimpleCornerAdapter(@NonNull Context context, @Nullable ItemCallback diffCallback, int layoutId, int itemHeight) {
        super(context, layoutId, diffCallback);
        this.itemHeight = itemHeight;
    }

    @Override
    public PopupListViewHolder createMyViewHolder(View view, ViewGroup parent, int viewType) {
        return new PopupListViewHolder(view);
    }

    @Override
    public void onBindItem(@NonNull PopupListViewHolder holder, @NonNull String item, int position) {
        holder.popupItemTitle.setText(item);
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

    @Override
    public void onClick(View v) {
        removeItem((String) v.getTag());
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class PopupListViewHolder extends BaseDataBindingAdapter.BaseBindingViewHolder {

        TextView popupItemTitle;

        PopupListViewHolder(View itemView) {
            super(itemView);
            if (itemHeight > 0) {
                ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                lp.height = itemHeight;
                itemView.setLayoutParams(lp);
            }
            popupItemTitle = itemView.findViewById(R.id.popup_item_title);
        }

    }

}
