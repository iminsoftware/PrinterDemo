package com.imin.newprinter.demo.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: Mark
 * @date: 2023/12/7 Time：17:22
 * @description:
 */
public class CustomDividerItemDecoration extends DividerItemDecoration {

    private final int dividerHeight;
    /**
     * @param context       Current context, it will be used to access resources.
     * @param orientation   Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     * @param dividerHeight
     */
    public CustomDividerItemDecoration(Context context, int orientation, int dividerHeight) {
        super(context, orientation);
        this.dividerHeight = dividerHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int lastPosition = parent.getAdapter().getItemCount() - 1;
        if (position == lastPosition) {
            // 最后一行不添加分割线
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, dividerHeight);
        }
    }
}
