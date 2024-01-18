package com.feature.tui.widget.loading;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.feature.tui.R;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 15:20
 */
public class LoadingView extends AppCompatImageView {
    private AnimationDrawable anim = null;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    private void initAnim() {
        setImageResource(R.drawable.xui_loading);
        anim = (AnimationDrawable) getDrawable();
        if (anim != null) {
            anim.start();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (anim != null && getVisibility() == View.VISIBLE) {
            anim.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (anim != null) {
            anim.stop();
        }
    }

}
