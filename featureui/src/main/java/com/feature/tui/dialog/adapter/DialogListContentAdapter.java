package com.feature.tui.dialog.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;
import com.feature.tui.modle.DialogItemDescription;

public class DialogListContentAdapter
        extends SimpleDataBindingAdapter<DialogItemDescription, DialogListContentAdapter.DialogListViewHolder> {
    private Context context;
    private boolean isSingle;
    private boolean hasIcon;

    @Override
    public void onBindItem(DialogListViewHolder binding, DialogItemDescription item, int position) {
        if (binding.dialogItemTitle != null) {
            binding.dialogItemTitle.setText(item.getTitle());
        }
        if (!TextUtils.isEmpty(item.getDescription())) {
            if (binding.dialogItemDescription != null) {
                binding.dialogItemDescription.setVisibility(View.VISIBLE);
                binding.dialogItemDescription.setText(item.getDescription());
            }
            if (binding.dialogItemLayout != null) {
                binding.dialogItemLayout.setPadding(0, this.context.getResources().getDimensionPixelSize(R.dimen.dp_13), 0, this.context.getResources().getDimensionPixelSize(R.dimen.dp_13));
            }
        } else {
            if (binding.dialogItemDescription != null) {
                binding.dialogItemDescription.setVisibility(View.GONE);
            }
            if (binding.dialogItemLayout != null)
                binding.dialogItemLayout.setPadding(0, this.context.getResources().getDimensionPixelSize(R.dimen.dp_15), 0, this.context.getResources().getDimensionPixelSize(R.dimen.dp_15));
        }
        if (hasIcon) {
            if (item.getResId() != 0)
                binding.dialogItemIcon.setImageResource(item.getResId());
        } else {
            if (isSingle) {
                if (binding.dialogItemIcon != null) {
                    binding.dialogItemIcon.setImageResource(R.drawable.common_icon_gou_selector);
                }
            } else {
                if (binding.dialogItemIcon != null) {
                    binding.dialogItemIcon.setImageResource(R.drawable.common_icon_rb2_selector);
                }
            }
            if (binding.dialogItemIcon != null)
                binding.dialogItemIcon.setSelected(item.isChecked());
        }
    }

    public DialogListContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback,
                                    boolean isSingle, boolean hasIcon, int layoutId) {
        super(context, layoutId, diffCallback);
        this.context = context;
        this.isSingle = isSingle;
        this.hasIcon = hasIcon;
    }

    public DialogListContentAdapter(Context context, DiffUtil.ItemCallback<DialogItemDescription> diffCallback,
                                    boolean isSingle, boolean hasIcon) {
        super(context, R.layout.dialog_list_item_layout, diffCallback);
        this.context = context;
        this.isSingle = isSingle;
        this.hasIcon = hasIcon;
    }

    @Override
    public DialogListViewHolder createMyViewHolder(View view, ViewGroup parent, int viewType) {
        return new DialogListViewHolder(view);
    }

    class DialogListViewHolder extends BaseDataBindingAdapter.BaseBindingViewHolder {

        View dialogItemLayout;
        TextView dialogItemTitle;
        TextView dialogItemDescription;
        ImageView dialogItemIcon;

        DialogListViewHolder(View itemView) {
            super(itemView);
            dialogItemLayout = itemView.findViewById(R.id.dialog_item_layout);
            dialogItemTitle = itemView.findViewById(R.id.dialog_item_title);
            dialogItemDescription = itemView.findViewById(R.id.dialog_item_description);
            dialogItemIcon = itemView.findViewById(R.id.dialog_item_icon);
        }

    }

}
