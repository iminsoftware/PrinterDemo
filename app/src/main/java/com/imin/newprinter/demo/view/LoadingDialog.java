package com.imin.newprinter.demo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.imin.newprinter.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
public class LoadingDialog extends android.app.Dialog {

    private TextView textView;

    public enum AnimationType { SPIN, DOTS }

    private ObjectAnimator spinAnimator;
    private List<ValueAnimator> dotAnimators = new ArrayList<>();

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialogStyle);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.dialog_loading);
        textView = findViewById(R.id.tv_text);
        textView.setText(tips);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    String tips = "";
    public LoadingDialog setText(String text) {

        if (textView != null) {
            tips = text;
            Log.d("PrintDemo_", "WIFI_CONNECT =>" + text);
            textView.setText(text);
        }
        return this;
    }

    public LoadingDialog setAnimationType(AnimationType type) {
        ImageView ivLoading = findViewById(R.id.iv_loading);
        ViewGroup dotContainer = findViewById(R.id.dot_container);

        if (ivLoading == null || dotContainer == null) return this;

        stopAllAnimations();

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
        stopSpinAnimation();

        spinAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        spinAnimator.setDuration(1000);
        spinAnimator.setRepeatCount(ValueAnimator.INFINITE);
        spinAnimator.setInterpolator(new LinearInterpolator());
        spinAnimator.start();
    }

    private void startDotAnimation(ViewGroup container) {
        stopDotAnimations();

        for (int i = 0; i < container.getChildCount(); i++) {
            View dot = container.getChildAt(i);

            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(400);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(i * 200L);
            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                float translationY = -20f * (float) Math.sin(value * Math.PI);
                dot.setTranslationY(translationY);
            });

            animator.start();
            dotAnimators.add(animator);
        }
    }

    @Override
    public void dismiss() {
        stopAllAnimations();
        super.dismiss();
    }

    private void stopAllAnimations() {
        stopSpinAnimation();
        stopDotAnimations();
    }

    private void stopSpinAnimation() {
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }
    }

    private void stopDotAnimations() {
        for (ValueAnimator animator : dotAnimators) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
        }
        dotAnimators.clear();
    }
}

