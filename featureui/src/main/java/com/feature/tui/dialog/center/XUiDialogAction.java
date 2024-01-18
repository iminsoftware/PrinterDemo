package com.feature.tui.dialog.center;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.widget.TextView;

import com.feature.tui.R;
import com.feature.tui.dialog.Functions;


import java.lang.annotation.RetentionPolicy;

public class XUiDialogAction {
    private TextView mButton;
    private CharSequence mStr;
    private Functions.Fun1 mOnClickListener;
    private int mActionProp;
    private int mStrColorRes;
    private int mStrSizeRes;
    //一般为确定按钮,默认为青色
    public static final int ACTION_PROP_POSITIVE = 0;
    //一般为取消按钮,默认为青色
    public static final int ACTION_PROP_NEUTRAL = 1;
    //一般为删除按钮具有提示效果,默认为红色
    public static final int ACTION_PROP_NEGATIVE = 2;
    private boolean mIsEnabled = true;

    public XUiDialogAction prop(int prop) {
        mActionProp = prop;
        return this;
    }

    public XUiDialogAction onClick(Functions.Fun1 listener) {
        this.mOnClickListener = listener;
        return this;
    }

    /**
     * 设置按钮是否可点击
     */
    public XUiDialogAction setEnabled(boolean enabled) {
        mIsEnabled = enabled;
        if (mButton != null)
            mButton.setEnabled(enabled);
        return this;
    }

    /**
     * 获取一个按钮View
     */
    public TextView getButton() {
        return mButton;
    }

    /**
     * 创建一个操纵按钮
     */
    public TextView buildActionView(XUiDialog dialog, int index) {
        mButton = generateActionButton(dialog.getContext(), mStr);
        mButton.setOnClickListener((v) -> {
            if (mButton.isEnabled() && mOnClickListener != null) {
                mOnClickListener.invoke(dialog, index);
            }
        });
        return mButton;
    }

    /**
     * 生成适用于对话框的按钮
     */
    private TextView generateActionButton(Context context, CharSequence text) {
        TextView button = new TextView(context);
        button.setBackground(null);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        TypedArray ta = context.obtainStyledAttributes(null, R.styleable.XUIDialogActionStyle, R.attr.xui_dialog_action_style, 0);
        int count = ta.getIndexCount();
        ColorStateList negativeTextColor = null;
        ColorStateList positiveTextColor = null;
        ColorStateList neutralTextColor = null;
        int paddingLeft = button.getPaddingLeft();
        int paddingRight = button.getPaddingRight();
        int paddingTop = button.getPaddingTop();
        int paddingBottom = button.getPaddingBottom();
        int n = 0;
        while (n < count) {
            int attr2 = ta.getIndex(n);
            if (attr2 == R.styleable.XUIDialogActionStyle_android_gravity) {
                button.setGravity(ta.getInt(attr2, -1));
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_textColor) {
                button.setTextColor(ta.getColorStateList(attr2));
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_textSize) {
                button.setTextSize(0, (float) ta.getDimensionPixelSize(attr2, 0));
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_background) {
                button.setBackground(ta.getDrawable(attr2));
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_minWidth) {
                int miniWidth = ta.getDimensionPixelSize(attr2, 0);
                button.setMinWidth(miniWidth);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_paddingLeft) {
                paddingLeft = ta.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_paddingRight) {
                paddingRight = ta.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_paddingTop) {
                paddingTop = ta.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_paddingBottom) {
                paddingBottom = ta.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_height) {
                int height = ta.getDimensionPixelSize(attr2, 0);
                button.setHeight(height);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_xui_dialog_positive_action_text_color) {
                positiveTextColor = ta.getColorStateList(attr2);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_xui_dialog_negative_action_text_color) {
                negativeTextColor = ta.getColorStateList(attr2);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_xui_dialog_neutral_action_text_color) {
                neutralTextColor = ta.getColorStateList(attr2);
            } else if (attr2 == R.styleable.XUIDialogActionStyle_android_textStyle) {
                int styleIndex = ta.getInt(attr2, -1);
                button.setTypeface(null, styleIndex);
            }
            ++n;
        }
        ta.recycle();
        button.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        button.setText(text);
        button.setClickable(true);
        button.setEnabled(mIsEnabled);
        if (mActionProp == ACTION_PROP_NEGATIVE) {
            button.setTextColor(negativeTextColor);
        } else if (mActionProp == ACTION_PROP_POSITIVE) {
            button.setTextColor(positiveTextColor);
        } else if (mActionProp == ACTION_PROP_NEUTRAL) {
            button.setTextColor(neutralTextColor);
        }
        try {
            if (mStrColorRes > 0) {
                button.setTextColor(button.getResources().getColorStateList(mStrColorRes));
            }
            if (mStrSizeRes > 0) {
                button.setTextSize(0, button.getResources().getDimension(mStrSizeRes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return button;
    }

    public XUiDialogAction(CharSequence mStr, Functions.Fun1 mOnClickListener, int mActionProp, int mStrColorRes, int mStrSizeRes) {
        this.mStr = mStr;
        this.mOnClickListener = mOnClickListener;
        this.mActionProp = mActionProp;
        this.mStrColorRes = mStrColorRes;
        this.mStrSizeRes = mStrSizeRes;
    }

    public XUiDialogAction(CharSequence mStr, Functions.Fun1 mOnClickListener) {
        this(mStr, mOnClickListener, ACTION_PROP_NEUTRAL, 0, 0);
    }

    public XUiDialogAction(CharSequence mStr, Functions.Fun1 mOnClickListener, int mActionProp) {
        this(mStr, mOnClickListener, mActionProp, 0, 0);
    }

    public XUiDialogAction(CharSequence mStr, Functions.Fun1 mOnClickListener, int mActionProp, int mStrColorRes) {
        this(mStr, mOnClickListener, mActionProp, mStrColorRes, 0);
    }

    @java.lang.annotation.Retention(value = RetentionPolicy.SOURCE)
    public static @interface Prop {
    }

    public static interface ActionListener {
        public void onClick(XUiDialog var1, int var2);
    }

}
