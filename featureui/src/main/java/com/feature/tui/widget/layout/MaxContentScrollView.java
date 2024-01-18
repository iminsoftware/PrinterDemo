package com.feature.tui.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;


/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 11:26
 */
public class MaxContentScrollView extends ScrollView {
    private int mMaxHeight = Integer.MAX_VALUE >> 4;

    public final void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int maxHeight = Math.min(heightSize, this.mMaxHeight);

        int expandSpec = (lp != null && lp.height > 0) ?
                MeasureSpec.makeMeasureSpec(Math.min(this.mMaxHeight, lp.height), MeasureSpec.EXACTLY)
                :
                MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public MaxContentScrollView(Context context) {
        super(context);
    }

    public MaxContentScrollView(Context context, int maxHeight) {
        super(context);
        this.mMaxHeight = maxHeight;
    }

    public MaxContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxContentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
