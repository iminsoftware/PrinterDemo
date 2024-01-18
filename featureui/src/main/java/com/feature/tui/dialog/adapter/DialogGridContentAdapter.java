package com.feature.tui.dialog.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;
import com.feature.tui.modle.DialogItemDescription;

public class DialogGridContentAdapter
        extends SimpleDataBindingAdapter<DialogItemDescription, DialogGridContentAdapter.DialogGridViewHolder> {
    private float titleTextSize;
    private int itemMarginLeft;
    private int itemMarginRight;

    /**
     * 设置item标题的字体大小
     */
    public void setTitleTextSize(float textSize) {
        this.titleTextSize = textSize;
    }

    @Override
    public void onBindItem(DialogGridViewHolder binding, DialogItemDescription item, int position) {
        if (binding.dialogItemLayout != null)
            binding.dialogItemLayout.setPadding(itemMarginLeft, 0, itemMarginRight, 0);
        if (binding.dialogItemTitle != null) {
            binding.dialogItemTitle.setText(item.getTitle());
            if (titleTextSize > 0) {
                binding.dialogItemTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
            }
        }
        if (binding.dialogItemIcon != null)
            binding.dialogItemIcon.setImageResource(item.getResId());
    }

    public DialogGridContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback, int layoutId, int itemMarginLeft, int itemMarginRight) {
        super(context, layoutId, diffCallback);
        this.itemMarginLeft = itemMarginLeft;
        this.itemMarginRight = itemMarginRight;
    }

    public DialogGridContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback, int layoutId, int itemMarginLeft) {
        this(context, diffCallback, layoutId, itemMarginLeft, context.getResources().getDimensionPixelSize(R.dimen.dp_8));
    }

    public DialogGridContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback, int layoutId) {
        this(context, diffCallback, layoutId, context.getResources().getDimensionPixelSize(R.dimen.dp_8), context.getResources().getDimensionPixelSize(R.dimen.dp_8));
    }

    @Override
    public DialogGridViewHolder createMyViewHolder(View view, ViewGroup parent, int viewType) {
        return new DialogGridViewHolder(view);
    }

    public DialogGridContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback) {
        this(context, diffCallback, R.layout.dialog_grid_item_layout, context.getResources().getDimensionPixelSize(R.dimen.dp_8), context.getResources().getDimensionPixelSize(R.dimen.dp_8));
    }

    class DialogGridViewHolder extends BaseDataBindingAdapter.BaseBindingViewHolder {

        View dialogItemLayout;

        TextView dialogItemTitle;

        ImageView dialogItemIcon;

        DialogGridViewHolder(View itemView) {
            super(itemView);
            dialogItemLayout = itemView.findViewById(R.id.dialog_item_layout);
            dialogItemTitle = itemView.findViewById(R.id.dialog_item_title);
            dialogItemIcon = itemView.findViewById(R.id.dialog_item_icon);
        }

    }

}
