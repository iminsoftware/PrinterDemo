package com.feature.tui.widget.inputlayout;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.feature.tui.R.color;
import com.feature.tui.R.string;
import com.feature.tui.util.XUiDisplayHelper;


/**
 * Author:LiangJiaXin
 * Time:2020/12/21 09:35
 * Description:手机号带格式输入框
 **/
public class PhoneFormatEditText extends RelativeLayout {
    private final EditText etContent;

    @RequiresApi(23)
    public PhoneFormatEditText(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        etContent = new EditText(ctx);
        addChild();
    }

    @RequiresApi(23)
    private void addChild() {
        setBackgroundColor(getContext().getColor(color.xui_config_color_white));
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(XUiDisplayHelper.dp2px(getContext(), 20));
        params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
        etContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        etContent.setFilters(new InputFilter[]{new LengthFilter(13)});
        etContent.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(@Nullable Editable s) {
            }

            public void beforeTextChanged(@Nullable CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(@Nullable CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    insertSpace(String.valueOf(s));
                }

            }
        });
        etContent.setTextSize(14.0F);
        etContent.setTextColor(getContext().getColor(color.xui_color_333333));
        etContent.setHint(getContext().getString(string.input_phone));
        etContent.setLayoutParams(params);
        etContent.setBackground(null);
        addView(etContent);
        final TextView backLine = new TextView(getContext());
        params = new LayoutParams(-1, XUiDisplayHelper.dp2px(getContext(), 1));
        params.addRule(12);
        params.setMarginStart(XUiDisplayHelper.dp2px(getContext(), 20));
        params.setMarginEnd(XUiDisplayHelper.dp2px(getContext(), 20));
        backLine.setLayoutParams(params);
        backLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
        addView(backLine);
        etContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            public final void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    backLine.setBackgroundColor(getContext().getColor(color.xui_config_color_main));
                } else {
                    backLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
                }

            }
        });
    }

    private void insertSpace(CharSequence text) {
        int length = String.valueOf(text).length();
        if (length == 3 || length == 8) {
            String str = text + getContext().getString(string.str_space);
            etContent.setText(str);
            etContent.setSelection(etContent.getText().toString().length());
        }

    }

    public String getText() {
        return etContent.getText().toString().replace(" ", "");
    }
}
