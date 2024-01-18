package com.feature.tui.widget.pickerview.listener;

import android.view.MotionEvent;

import com.feature.tui.widget.pickerview.view.WheelView;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:手势的监听
 */
public class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private WheelView wheelView;

    public LoopViewGestureListener(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        wheelView.scrollBy(velocityY);
        return true;
    }
}
