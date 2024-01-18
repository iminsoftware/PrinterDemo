package com.feature.tui.widget.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 9:56
 */
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    private float mDividerWidth = 1f;
    private Paint mDividerPaint = new Paint();
    private int mSpanCount;

    public GridDividerItemDecoration(Context context, int mSpanCount, int dividerColor, float dividerWidth) {
        mDividerWidth = dividerWidth;
        mDividerPaint.setStrokeWidth(mDividerWidth);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(context.getResources().getColor(dividerColor));
        this.mSpanCount = mSpanCount;
    }

    public GridDividerItemDecoration(Context context,
                                     int mSpanCount) {
        this(context,mSpanCount, R.color.xui_config_module_divider_color_deep,1f);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(child);
            int column = (position + 1) % mSpanCount;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int childBottom = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            int childRight = child.getRight() + params.rightMargin +
                    Math.round(ViewCompat.getTranslationX(child));
            if (childBottom < parent.getHeight()) {
                c.drawLine(
                        child.getLeft(),
                        childBottom,
                        childRight,
                        childBottom,
                        mDividerPaint
                );
            }
            if (column < mSpanCount) {
                c.drawLine(
                        childRight,
                        child.getTop(),
                        childRight,
                        childBottom,
                        mDividerPaint
                );
            }
        }
    }
}
