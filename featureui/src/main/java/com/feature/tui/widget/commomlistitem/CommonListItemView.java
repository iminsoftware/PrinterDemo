package com.feature.tui.widget.commomlistitem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R;
import com.feature.tui.R.layout;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.dialog.Functions;
import com.feature.tui.util.XUiDisplayHelper;

public class CommonListItemView extends RelativeLayout {
    public CommonListItemBinding binding;

    public CommonListItemView(@NonNull Context ctx) {
        super(ctx);
        this.initView();
    }

    public CommonListItemView(@NonNull Context ctx, @Nullable AttributeSet attributeSet) {
        super(ctx, attributeSet);
        this.initView();
    }

    private void initView() {
        this.binding = new CommonListItemBinding(LayoutInflater.from(getContext()).inflate(layout.common_list_item, this, false));
        this.addView(binding.itemView);
    }

    /**
     * 设置右边第二个图标
     *
     * @param res
     */
    public void setRightImgTwo(int res) {
        if (res != 0) {
            binding.imgViewRightTwo.setImageResource(res);
            binding.imgViewRightTwo.setVisibility(View.VISIBLE);
        } else
            binding.imgViewRightTwo.setVisibility(View.GONE);
    }

    /**
     * 设置右边第二个图标
     *
     * @param drawable
     */
    public void setRightImgTwo(Drawable drawable) {
        if (drawable != null) {
            binding.imgViewRightTwo.setImageDrawable(drawable);
            binding.imgViewRightTwo.setVisibility(View.VISIBLE);
        } else
            binding.imgViewRightTwo.setVisibility(View.GONE);
    }

    /**
     * 设置右边图标
     *
     * @param res
     */
    public void setRightImg(int res) {
        if (res != 0) {
            binding.imgViewRight.setImageResource(res);
            binding.imgViewRight.setVisibility(View.VISIBLE);
        } else
            binding.imgViewRight.setVisibility(View.GONE);
    }

    /**
     * 设置右边图标
     *
     * @param drawable
     */
    public void setRightImg(Drawable drawable) {
        if (drawable != null) {
            binding.imgViewRight.setImageDrawable(drawable);
            binding.imgViewRight.setVisibility(View.VISIBLE);
        } else
            binding.imgViewRight.setVisibility(View.GONE);
    }

    /**
     * 设置左边图标
     *
     * @param res
     */
    public void setLeftImg(int res) {
        if (res != 0) {
            binding.imgView.setImageResource(res);
            binding.imgView.setVisibility(View.VISIBLE);
        } else
            binding.imgView.setVisibility(View.GONE);
    }

    /**
     * 设置左边图标
     *
     * @param drawable
     */
    public void setLeftImg(Drawable drawable) {
        if (drawable != null) {
            binding.imgView.setImageDrawable(drawable);
            binding.imgView.setVisibility(View.VISIBLE);
        } else
            binding.imgView.setVisibility(View.GONE);
    }

    /**
     * 设置主标题
     *
     * @param res
     */
    public void setTitle(int res) {
        setTitle(getContext().getString(res));
    }

    /**
     * 设置主标题
     *
     * @param title
     */
    public void setTitle(String title) {
        binding.tvTitle.setText(title);
        binding.tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置次标题
     *
     * @param res
     */
    public void setContent(int res) {
        this.setContent(getContext().getString(res));
    }

    /**
     * 设置次标题
     *
     * @param content
     */
    public void setContent(@NonNull String content) {
        binding.tvContent.setText(content);
        binding.tvContent.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        binding.tvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (binding.tvContent.getLineCount() == 2) {
                    binding.layLeft.setPadding(0, 0, 0, XUiDisplayHelper.dp2px(getContext(), 16));
                    binding.layRight.setPadding(0, 0, 0, XUiDisplayHelper.dp2px(getContext(), 16));
                } else {
                    binding.layLeft.setPadding(0, 0, 0, 0);
                    binding.layRight.setPadding(0, 0, 0, 0);
                }
            }
        }, 10);
    }

    /**
     * 设置右边显示文字
     *
     * @param res
     */
    public void setRightText(int res) {
        this.setRightText(getContext().getString(res));
    }

    /**
     * 设置右边显示文字
     *
     * @param rightText
     */
    public void setRightText(@NonNull String rightText) {
        binding.tvRight.setText(rightText);
        binding.tvRight.setVisibility(TextUtils.isEmpty(rightText) ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置右边开关，可点击标记
     *
     * @param isShowToggle
     * @param callback
     * @param showClickTag
     */
    public void showToggle(boolean isShowToggle, final Functions.Fun3 callback, boolean showClickTag) {
        this.showToggle(isShowToggle, callback, showClickTag, null, 0, 0);
    }

    /**
     * 设置右边开关，可点击标记,可修改图标，大小
     *
     * @param isShowToggle
     * @param callback
     * @param showClickTag
     */
    public void showToggle(boolean isShowToggle, final Functions.Fun3 callback, boolean showClickTag, Drawable drawable, int width, int height) {
        if (isShowToggle) {
            binding.toggleLine.setVisibility(View.VISIBLE);
            binding.toggle.setVisibility(View.VISIBLE);
            binding.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (callback != null) {
                    callback.invoke(isChecked);
                }
            });
            binding.layToggle.setOnClickListener(v -> binding.toggle.performClick());
            if (drawable != null)
                binding.toggle.setBackgroundDrawable(drawable);
            if (width != 0 && height != 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.toggle.getLayoutParams();
                lp.width = width;
                lp.height = height;
                binding.toggle.setLayoutParams(lp);
            }
        } else {
            binding.toggleLine.setVisibility(View.GONE);
            binding.toggle.setVisibility(View.GONE);
        }
        binding.imgViewRight.setVisibility(showClickTag ? View.VISIBLE : View.GONE);
    }

    /**
     * 重置默认状态
     */
    public void reset() {
        setLeftImg(0);
        setTitle("");
        setContent("");
        showToggle(false, null, false);
        setRightText("");
    }

    public class CommonListItemBinding extends BaseDataBindingAdapter.BaseBindingViewHolder {

        public View itemLay;
        public ImageView imgView;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvRight;
        public ToggleButton toggle;
        public View toggleLine;
        public View layToggle;
        public ImageView imgViewRight;
        public ImageView imgViewRightTwo;
        public LinearLayout layLeft;
        public LinearLayout layRight;


        CommonListItemBinding(View view) {
            super(view);
            itemLay = view.findViewById(R.id.item_lay);
            imgViewRight = view.findViewById(R.id.img_view_right);
            imgViewRightTwo = view.findViewById(R.id.img_view_right_two);
            imgView = view.findViewById(R.id.img_view);
            tvTitle = view.findViewById(R.id.tv_title);
            tvContent = view.findViewById(R.id.tv_content);
            tvRight = view.findViewById(R.id.tv_right);
            toggle = view.findViewById(R.id.toggle);
            toggleLine = view.findViewById(R.id.toggle_line);
            layToggle = view.findViewById(R.id.lay_toggle);
            layLeft = view.findViewById(R.id.lay_left);
            layRight = view.findViewById(R.id.lay_right);
        }

    }

}
