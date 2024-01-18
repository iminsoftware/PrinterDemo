package com.feature.tui.widget.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feature.tui.R;

/**
 * Author:LiangJiaXin
 * Time:2020/12/30 14:53
 * Description:通用标题布局
 **/
public class TitleBar extends RelativeLayout {
    private TextView title;
    private TextView textRight;
    private ImageView iconLeft;
    private ImageView iconRight;
    private String titleStr = "";
    private String rightStr;
    private boolean isCenterTitle;
    private boolean showBottom;
    private TitleBar.OnBack onBack;

    public TitleBar.OnBack getOnBack() {
        return onBack;
    }

    public void setOnBack(TitleBar.OnBack onBack) {
        this.onBack = onBack;
    }

    private void addChild() {
        RelativeLayout rlContent = new RelativeLayout(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlContent.setLayoutParams(params);
        addView(rlContent);


        View view = LayoutInflater.from(getContext()).inflate(R.layout.title_bar_layout, rlContent, false);
        rlContent.addView(view);

        title = findViewById(R.id.tv_title);
        textRight = findViewById(R.id.tv_right);
        iconLeft = findViewById(R.id.iv_left);
        iconRight = findViewById(R.id.iv_right);

        iconLeft.setOnClickListener(it -> {
            if (onBack != null) {
                onBack.showGoBack();
            }
            finish();

        });
        title.setText(titleStr);
        setRightText(rightStr, 0, null);
        if (isCenterTitle) {
            setCenterTitle();
        }

        if (!showBottom) {
            findViewById(R.id.bottom_line).setVisibility(View.GONE);
        }
    }

    private void finish() {
        if (getContext() instanceof Activity) {
            Context context = getContext();
            Activity mActivity = (Activity) context;
            mActivity.finish();
        }
    }

    private void setCenterTitle() {
        LayoutParams layoutParams = (LayoutParams) title.getLayoutParams();
        layoutParams.addRule(CENTER_IN_PARENT);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getTextRight() {
        return textRight;
    }

    public ImageView getIconRight() {
        return iconRight;
    }

    public ImageView getIconLeft() {
        return iconLeft;
    }

    public void setRightText(String text, int color, OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            textRight.setVisibility(View.GONE);
        } else {
            textRight.setVisibility(View.VISIBLE);
            textRight.setText(text);
            if (color != 0)
                textRight.setTextColor(color);
            textRight.setOnClickListener(listener);
        }
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public TitleBar(Context ctx) {
        super(ctx);
    }

    public TitleBar(Context ctx, AttributeSet attributeSet) {
        super(ctx, attributeSet);
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TitleBar);
        titleStr = String.valueOf(typedArray.getString(R.styleable.TitleBar_tb_title));
        isCenterTitle = typedArray.getBoolean(R.styleable.TitleBar_tb_center_title, false);
        showBottom = typedArray.getBoolean(R.styleable.TitleBar_tb_show_line, showBottom);
        rightStr = typedArray.getString(R.styleable.TitleBar_tb_right_str);
        typedArray.recycle();
        addChild();
    }

    public interface OnBack {
        boolean showGoBack();
    }
}
