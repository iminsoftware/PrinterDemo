package com.feature.tui.alpha;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.feature.tui.R;
import com.feature.tui.util.AnimationUtil;

public class AlphaMaskLinearLayout
        extends LinearLayout {
    private float alphaValue;
    private boolean isAnimate;

    public AlphaMaskLinearLayout(Context context) {
        this(context, null, 0);
    }

    public AlphaMaskLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaMaskLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isAnimate = true;
        setBackgroundColor(Color.BLACK);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlphaMaskLinearLayout);
        TypedArray attrArray = typedArray;
        alphaValue = attrArray.getFloat(R.styleable.AlphaMaskLinearLayout_alpha, 0.5f);
        setAlpha(alphaValue);
        isAnimate = attrArray.getBoolean(R.styleable.AlphaMaskLinearLayout_animate, isAnimate);
        attrArray.recycle();
    }

    public boolean onTouchEvent(MotionEvent event) {
        setVisibility(View.GONE);
        return true;
    }

    public void setVisibility(int visibility) {
        if (isAnimate) {
            if (getVisibility() == visibility) {
                return;
            }
            super.setVisibility(VISIBLE);
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                AnimationUtil.alphaAnim(this, getAlpha(), 0f, () -> {
                }, () -> {
                    super.setVisibility(visibility);
                    setAlpha(alphaValue);
                });
            } else {
                AnimationUtil.alphaAnim(this, 0.0f, getAlpha(), () -> {
                }, () -> {
                });
            }
        } else {
            super.setVisibility(visibility);
        }
    }

}
