package com.feature.tui.widget.cornermarkview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;

/**
 * Author:LiangJiaXin
 * Time:2020/12/24 14:52
 * Description：角标demo
 **/
public class CornerMarkView extends AppCompatTextView {

    public CornerMarkView(@NonNull Context context) {
        super(context);
        initText();
    }

    public CornerMarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    public CornerMarkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initText();
    }

    private void initText() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);
        //设置角标背景
        setBackgroundResource(R.drawable.corner16_ee0424_bg);
        setMarkInfo(9);
        setTextColor(Color.rgb(255, 255, 255));
        setTextSize(12f);
        setPadding(
                XUiDisplayHelper.dp2px(getContext(), 6), XUiDisplayHelper.dp2px(getContext(), 1),
                XUiDisplayHelper.dp2px(getContext(), 6), XUiDisplayHelper.dp2px(getContext(), 1)
        );

    }

    public <T> void setMarkInfo(T param) {
        if (param instanceof Integer) {
            setTextSize(12f);
            setText((Integer) param > 99 ? "99+" : param.toString());
        } else if (param instanceof String) {
            setTextSize(8f);
            setText((String) param);
        }
    }


}
