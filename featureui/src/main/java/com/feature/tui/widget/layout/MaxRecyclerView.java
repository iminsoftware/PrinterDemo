package com.feature.tui.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 13:59
 */
public class MaxRecyclerView extends RecyclerView {
    private int mMaxHeight = Integer.MAX_VALUE >> 4;


    public MaxRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MaxRecyclerView(@NonNull Context context,int mMaxHeight) {
        super(context);
        this.mMaxHeight = mMaxHeight;
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置最大高度
     */
    public void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        int heightSize = MeasureSpec.getSize(heightSpec);
        int maxHeight = Math.min(heightSize,mMaxHeight);
        int expandSpec = 0;
        if (lp != null && lp.height > 0) {
            expandSpec = MeasureSpec.makeMeasureSpec(Math.min(mMaxHeight,lp.height), MeasureSpec.EXACTLY);
        } else {
            expandSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, expandSpec);

    }

}
