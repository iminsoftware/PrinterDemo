package com.feature.tui.widget.horizontaltablist;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.demo.adapter.SimpleDataBindingAdapter;
import com.feature.tui.util.XUiDisplayHelper;


public class HorizontalTabAdapter extends SimpleDataBindingAdapter<HorizontalTabItem, HorizontalTabAdapter.ItemHorizontalTabViewHolder> {
    private TabItemClickListener tabItemClickListener;
    private Context context;
    private boolean showSecondTitle;

    public HorizontalTabAdapter(@NonNull Context context, @Nullable ItemCallback diffCallback, int layoutId, boolean showSecondTitle) {
        super(context, layoutId, diffCallback);
        this.context = context;
        this.showSecondTitle = showSecondTitle;
        setOnItemClickListener((view, item, position) -> {
            if (tabItemClickListener != null) {
                tabItemClickListener.onClick(position);
            }
        });
    }

    public void setOnItemClickListener(TabItemClickListener clickListener) {
        tabItemClickListener = clickListener;
    }


    @RequiresApi(23)
    @Override
    public void onBindItem(ItemHorizontalTabViewHolder binding, @NonNull HorizontalTabItem item, @NonNull int position) {
        if (showSecondTitle) {
            binding.tvName2.setVisibility(View.VISIBLE);
        } else {
            binding.tvName2.setVisibility(View.GONE);
        }

        binding.tvName.setText(item.getName());
        binding.tvName2.setText(item.getName2());
        if (item.getIcon() == 0) {
            binding.ivIcon.setBackgroundColor(context.getColor(R.color.xui_config_color_main));
        } else {
            binding.ivIcon.setBackgroundResource(item.getIcon());
        }

        LayoutParams layoutParams = binding.llContent.getLayoutParams();
        if (getCurrentList().size() < 5) {
            if (layoutParams != null) {
                layoutParams.width = XUiDisplayHelper.getScreenWidth(context) / getCurrentList().size();
            }
        } else if (layoutParams != null) {
            layoutParams.width = 2 * (XUiDisplayHelper.getScreenWidth(context) / 9);
        }

        android.widget.LinearLayout.LayoutParams llParam;
        if (getCurrentList().size() == 3) {
            llParam = (android.widget.LinearLayout.LayoutParams) binding.ivIcon.getLayoutParams();
            llParam.width = XUiDisplayHelper.dp2px(context, 36);
            llParam.height = XUiDisplayHelper.dp2px(context, 36);
        }

        if (getCurrentList().size() == 2) {
            binding.llContent.setOrientation(LinearLayout.HORIZONTAL);
            binding.tvName.setTextSize(16.0F);
            llParam = (android.widget.LinearLayout.LayoutParams) binding.llNameContent.getLayoutParams();
            llParam.topMargin = 0;
            llParam.setMarginStart(XUiDisplayHelper.dp2px(context, 12));

            llParam = (android.widget.LinearLayout.LayoutParams) binding.ivIcon.getLayoutParams();
            llParam.setMarginStart(XUiDisplayHelper.dp2px(context, 20));
            if (showSecondTitle) {
                llParam.width = XUiDisplayHelper.dp2px(context, 48);
                llParam.height = XUiDisplayHelper.dp2px(context, 48);

                llParam = (android.widget.LinearLayout.LayoutParams) binding.tvName2.getLayoutParams();
                llParam.gravity = Gravity.NO_GRAVITY;
            }
        }

    }

    @Override
    public ItemHorizontalTabViewHolder createMyViewHolder(View view, ViewGroup parent, int viewType) {
        return new ItemHorizontalTabViewHolder(view);
    }

    class ItemHorizontalTabViewHolder extends BaseDataBindingAdapter.BaseBindingViewHolder {

        TextView tvName;
        TextView tvName2;
        ImageView ivIcon;
        LinearLayout llContent;
        LinearLayout llNameContent;

        ItemHorizontalTabViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvName2 = itemView.findViewById(R.id.tv_name2);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            llContent = itemView.findViewById(R.id.ll_content);
            llNameContent = itemView.findViewById(R.id.ll_name_content);
        }

    }


}
