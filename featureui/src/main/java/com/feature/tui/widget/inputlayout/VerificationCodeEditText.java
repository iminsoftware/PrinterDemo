package com.feature.tui.widget.inputlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.R.color;
import com.feature.tui.R.drawable;
import com.feature.tui.R.styleable;
import com.feature.tui.util.XUiDisplayHelper;

/**
 * Author:LiangJiaXin
 * Time:2020/12/16 14:57
 * Description:验证码获取+输入框的视图
 **/
public class VerificationCodeEditText extends RelativeLayout {
    private final EditText etCode;
    private final TextView btnCode;
    private String etHint;
    private int etHintColor;
    private int etTextLength;
    private float etTextSize;
    private String etText;
    private int etTextColor;
    private int etBackgroundColor;
    private int etBackgroundDrawable;
    private String btnText;
    private float btnTextSize;
    private int btnTextColor;
    private int btnBackgroundColor;
    private Drawable btnBackgroundDrawable;
    private int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
    private int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
    private CountDownTimer countDownTimer;

    @RequiresApi(Build.VERSION_CODES.M)
    public VerificationCodeEditText(@NonNull Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        etCode = new EditText(getContext());
        btnCode = new TextView(getContext());
        TypedArray attrArray = getContext().obtainStyledAttributes(attrs, styleable.VerificationCodeEditText);
        etTextLength = attrArray.getInteger(styleable.VerificationCodeEditText_et_length, 6);
        etHint = String.valueOf(attrArray.getString(styleable.VerificationCodeEditText_et_hint));
        etHintColor = attrArray.getColor(styleable.VerificationCodeEditText_et_hint_color, Color.rgb(121, 121, 121));
        etTextSize = attrArray.getDimension(styleable.VerificationCodeEditText_et_text_size, 14.0F);
        etText = String.valueOf(attrArray.getString(styleable.VerificationCodeEditText_et_text));
        etTextColor = attrArray.getColor(styleable.VerificationCodeEditText_et_text_color, Color.rgb(0, 0, 0));
        etBackgroundColor = attrArray.getColor(styleable.VerificationCodeEditText_et_background_color, Color.rgb(255, 255, 255));
        etBackgroundDrawable = attrArray.getResourceId(styleable.VerificationCodeEditText_et_background_drawable, drawable.xui_dialog_bg);
        btnText = String.valueOf(attrArray.getString(styleable.VerificationCodeEditText_btn_text));
        btnTextSize = attrArray.getDimension(styleable.VerificationCodeEditText_btn_text_size, 14.0F);
        btnTextColor = attrArray.getColor(styleable.VerificationCodeEditText_btn_text_color, Color.rgb(8, 197, 172));
        btnBackgroundColor = attrArray.getColor(styleable.VerificationCodeEditText_btn_background_color, Color.rgb(255, 255, 255));
        btnBackgroundDrawable = attrArray.getDrawable(styleable.VerificationCodeEditText_btn_background_drawable);
        attrArray.recycle();
        addChild();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void addChild() {
        LayoutParams params = new LayoutParams(matchParent, matchParent);
        RelativeLayout rlContent = new RelativeLayout(getContext());
        rlContent.setLayoutParams(params);
        rlContent.setBackgroundColor(getContext().getColor(color.xui_config_color_white));
        addView(rlContent);
        params = new LayoutParams(matchParent, matchParent);
        params.setMarginStart(XUiDisplayHelper.dp2px(getContext(), 20));
        params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
        params.addRule(15);
        etCode.setInputType(2);
        etCode.setLayoutParams(params);
        rlContent.addView((View) etCode);
        LayoutParams btnParams = new LayoutParams(wrapContent, wrapContent);
        btnParams.addRule(15);
        btnParams.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
        btnParams.addRule(21);
        btnCode.setLayoutParams(btnParams);
        rlContent.addView(btnCode);
        final TextView backLine = new TextView(getContext());
        params = new LayoutParams(-1, XUiDisplayHelper.dp2px(getContext(), 1));
        params.addRule(12);
        params.setMarginStart(XUiDisplayHelper.dp2px(getContext(), 20));
        params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
        backLine.setLayoutParams(params);
        backLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
        rlContent.addView(backLine);
        etCode.setHintTextColor(etHintColor);
        etCode.setHint(etHint);
        etCode.setMaxEms(etTextLength);
        etCode.setFilters(new InputFilter[]{new LengthFilter(etTextLength)});
        etCode.setTextColor(getContext().getColor(color.xui_color_333333));
        etCode.setTextSize(etTextSize);
        etCode.setText(etText);
        etCode.setTextColor(etTextColor);
        etCode.setBackgroundColor(etBackgroundColor);
        etCode.setBackgroundResource(etBackgroundDrawable);
        btnCode.setText(btnText);
        btnCode.setTextSize(btnTextSize);
        btnCode.setTextColor(btnTextColor);
        btnCode.setBackgroundResource(drawable.xui_dialog_bg);
        if (btnBackgroundColor > 0)
            btnCode.setBackgroundColor(btnBackgroundColor);
        if (btnBackgroundDrawable != null)
            btnCode.setBackgroundDrawable(btnBackgroundDrawable);
        etCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                backLine.setBackgroundColor(getContext().getColor(color.xui_config_color_main));
            } else {
                backLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
            }
        });
    }

    public VerificationCodeEditText setButtonClick(@NonNull OnClickListener clickListener) {
        btnCode.setOnClickListener(clickListener);
        return this;
    }

    public String getText() {
        return etCode.getText().toString();
    }

    public VerificationCodeEditText setText(@NonNull String str) {
        etCode.setText(str);
        return this;
    }

    public VerificationCodeEditText setButtonTextColor(int textColor) {
        btnCode.setTextColor(textColor);
        return this;
    }

    public VerificationCodeEditText setButtonText(@NonNull String str) {
        btnCode.setText(str);
        return this;
    }

    public VerificationCodeEditText setButtonBackgroundColor(int color) {
        btnCode.setBackgroundColor(color);
        return this;
    }

    public VerificationCodeEditText setButtonBackgroundDrawable(@Nullable Drawable drawable) {
        btnCode.setBackground(drawable);
        return this;
    }

    public VerificationCodeEditText setEtTextColor(int textColor) {
        etCode.setTextColor(textColor);
        return this;
    }

    public VerificationCodeEditText setEtHintText(@NonNull String str) {
        etCode.setHint(str);
        return this;
    }

    public VerificationCodeEditText setEtHintColor(int color) {
        etCode.setHintTextColor(color);
        return this;
    }

    public VerificationCodeEditText setEtBackgroundColor(int color) {
        etCode.setBackgroundColor(color);
        return this;
    }

    public VerificationCodeEditText setEtBackgroundDrawable(@Nullable Drawable drawable) {
        etCode.setBackground(drawable);
        return this;
    }

    public VerificationCodeEditText setEtTextSize(float size) {
        etCode.setTextSize(size);
        return this;
    }

    public VerificationCodeEditText setBtnTextSize(float size) {
        btnCode.setTextSize(size);
        return this;
    }

    public final CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(@Nullable CountDownTimer var1) {
        countDownTimer = var1;
    }

    public void startCountDown(final int seconds) {
        btnCode.setEnabled(false);
        final LayoutParams params = (LayoutParams) btnCode.getLayoutParams();
        countDownTimer = new CountDownTimer((long) seconds * 1000L, 1000L) {
            public void onTick(long millisUntilFinished) {
                btnCode.setText(String.valueOf(millisUntilFinished / (long) 1000));
            }

            public void onFinish() {
                btnCode.setEnabled(true);
                btnCode.setText(getContext().getString(R.string.resend));
                params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
            }
        };
        CountDownTimer var3 = countDownTimer;
        var3.start();

        params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 36));
    }

}
