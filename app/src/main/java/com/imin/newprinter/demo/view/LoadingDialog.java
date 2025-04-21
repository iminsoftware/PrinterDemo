package com.imin.newprinter.demo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.imin.newprinter.demo.R;

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
public class LoadingDialog extends android.app.Dialog {

    public enum AnimationType { SPIN, DOTS }

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialogStyle);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    public LoadingDialog setText(String text) {
        TextView textView = (TextView) findViewById(R.id.tv_text);
        if (textView != null) {
            textView.setText(text);
        }
        return this;
    }

    public LoadingDialog setAnimationType(AnimationType type) {
        ImageView ivLoading = (ImageView) findViewById(R.id.iv_loading);
        ViewGroup dotContainer = (ViewGroup) findViewById(R.id.dot_container);

        if (ivLoading == null || dotContainer == null) return this;

        switch (type) {
            case SPIN:
                ivLoading.setVisibility(View.VISIBLE);
                dotContainer.setVisibility(View.GONE);
                startSpinAnimation(ivLoading);
                break;
            case DOTS:
                ivLoading.setVisibility(View.GONE);
                dotContainer.setVisibility(View.VISIBLE);
                startDotAnimation(dotContainer);
                break;
        }
        return this;
    }

    private void startSpinAnimation(ImageView view) {
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotate);
    }

    private void startDotAnimation(ViewGroup container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View dot = container.getChildAt(i);
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    dot, "translationY", 0f, -20f, 0f
            );
            anim.setDuration(600);
            anim.setRepeatCount(ValueAnimator.INFINITE);
            anim.setStartDelay(i * 200L);
            anim.start();
        }
    }
}

