package com.feature.tui.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import com.feature.tui.dialog.Functions;

public class AnimationUtil {

    public static void alphaAnim(View view, float alphaStart, float alphaEnd, Functions.Fun0 start, Functions.Fun0 end) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(view, "alpha", new float[]{alphaStart, alphaEnd});
        oa.addListener(new Animator.AnimatorListener() {

            public void onAnimationStart(Animator animation) {
                start.invoke();
            }

            public void onAnimationEnd(Animator animation) {
                end.invoke();
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationCancel(Animator animation) {
            }
        });
        oa.setDuration(500L);
        oa.start();
    }

}
