package com.imin.newprinter.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */
public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private boolean isScrollEnabled = false; // 默认禁止滑动

    public void setScrollEnabled(boolean enabled) {
        isScrollEnabled = enabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isScrollEnabled && super.onTouchEvent(event); // 根据开关状态响应事件‌:ml-citation{ref="2,8" data="citationList"}
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isScrollEnabled && super.onInterceptTouchEvent(event);
    }
}

