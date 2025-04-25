package com.imin.newprinter.demo.view;

import android.util.SparseLongArray;
import android.view.View;

/**
 * @Author: hy
 * @date: 2025/4/25
 * @description:
 */
public abstract class OnSingleClickListener implements View.OnClickListener {
    private static final int MIN_CLICK_INTERVAL = 1000;
    private static final SparseLongArray clickTimeMap = new SparseLongArray();

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        long lastClickTime = clickTimeMap.get(viewId, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
            clickTimeMap.put(viewId, currentTime);
            onSingleClick(v);
        }
    }

    public abstract void onSingleClick(View v);
}

