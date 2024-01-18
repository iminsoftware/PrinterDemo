package com.feature.tui.widget.buttonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.R.color;
import com.feature.tui.R.string;
import com.feature.tui.R.styleable;
import com.feature.tui.util.XUiDisplayHelper;

@RequiresApi(Build.VERSION_CODES.M)
public class ButtonView extends RelativeLayout {
    private ImageView ivIcon;
    private TextView tvInfo;
    private int bvIconResource;
    private float bvIconHeight;
    private float bvIconWidth;
    private int bvTextContent;
    private float bvTextContentSize = 14.0F;
    private int bvTextContentColor;
    private float bvIconTextMargin;
    private boolean bvIconVisibility;

    public ButtonView(Context context) {
        super(context);
    }

    public ButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(AttributeSet attributeSet) {
        ivIcon = new ImageView(getContext());
        tvInfo = new TextView(getContext());
        TypedArray attrArray = getContext().obtainStyledAttributes(attributeSet, styleable.ButtonView);
        bvIconResource = attrArray.getResourceId(styleable.ButtonView_bv_icon_resource, R.drawable.ic_common_ic_camera);
        bvIconHeight = attrArray.getDimension(styleable.ButtonView_bv_icon_height, (float) XUiDisplayHelper.dp2px(getContext(), 24));
        bvIconWidth = attrArray.getDimension(styleable.ButtonView_bv_icon_width, (float) XUiDisplayHelper.dp2px(getContext(), 24));
        bvTextContent = attrArray.getResourceId(styleable.ButtonView_bv_text_content, string.bv_confirm);
        bvTextContentSize = attrArray.getDimension(styleable.ButtonView_bv_text_content_size, (float) XUiDisplayHelper.sp2px(getContext(), 14));
        bvTextContentColor = attrArray.getResourceId(styleable.ButtonView_bv_text_content_color, color.xui_config_color_white);
        bvIconTextMargin = attrArray.getDimension(styleable.ButtonView_bv_icon_text_margin, (float) XUiDisplayHelper.dp2px(getContext(), 8));
        bvIconVisibility = attrArray.getBoolean(styleable.ButtonView_bv_icon_visibility, false);
        attrArray.recycle();
        addChild();
    }

    private void addChild() {
        LinearLayout llContent = new LinearLayout(getContext());
        llContent.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParams.addRule(CENTER_IN_PARENT);
        llContent.setLayoutParams(rlParams);
        addView(llContent);

        android.widget.LinearLayout.LayoutParams llParams = new android.widget.LinearLayout.LayoutParams(XUiDisplayHelper.dp2px(getContext(), 24), XUiDisplayHelper.dp2px(getContext(), 24));
        llParams.gravity = Gravity.CENTER_VERTICAL;
        ivIcon.setBackgroundResource(bvIconResource);
        ivIcon.setLayoutParams(llParams);
        llContent.addView(ivIcon);
        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, bvTextContentSize);
        tvInfo.setTextColor(getContext().getColor(bvTextContentColor));
        tvInfo.setText(getContext().getString(bvTextContent));
        llParams = new android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = Gravity.CENTER_VERTICAL;
        llParams.setMarginStart((int) bvIconTextMargin);
        tvInfo.setLayoutParams(llParams);
        llContent.addView(tvInfo);
        showIcon(bvIconVisibility);
    }

    public void showIcon(boolean show) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvInfo.getLayoutParams();
        if (show) {
            ivIcon.setVisibility(View.VISIBLE);
            params.setMarginStart((int) bvIconTextMargin);
        } else {
            ivIcon.setVisibility(View.GONE);
            params.setMarginStart(0);
        }

    }

    public void enableView(boolean enable, boolean isStroke) {
        setEnabled(enable);
        if (isEnabled()) {
            if (isStroke) {
                setBackgroundResource(R.drawable.button_08c5ac_stroke_selector);
            } else {
                setBackgroundResource(R.drawable.button_08c5ac_solid_selector);
            }
        } else if (isStroke) {
            setBackgroundResource(R.drawable.corner18_a7e0d8_stoke_bg);
            tvInfo.setTextColor(getContext().getColor(color.xui_color_a7e0d8));
        } else {
            setBackgroundResource(R.drawable.corner20_a7e0d8_bg);
        }

    }

    public TextView getTv() {
        return tvInfo;
    }
}
