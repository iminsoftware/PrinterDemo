package com.feature.tui.widget.recyclerview.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 10:19
 */
public class ListDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int mOrientation = 0;
    private Paint mDividerPaint = new Paint();
    private float dividerWidth = 1f;
    private boolean isDrawLast = true;
    /**
     * 横向布局
     */
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    /**
     * 垂直布局
     */
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public ListDividerItemDecoration(Context context, int orientation,
                                     int dividerColor,
                                     float dividerWidth,
                                     boolean isDrawLast) {
        mDividerPaint.setStrokeWidth(dividerWidth);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(context.getResources().getColor(dividerColor));
        setOrientation(orientation);
        this.isDrawLast = isDrawLast;
    }

    public ListDividerItemDecoration(Context context, int orientation) {
        this(context, orientation, R.color.xui_config_module_divider_color_deep, 1f, true);
    }

    private void setOrientation(int orientation) {
        mOrientation = orientation;
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            if (isDrawLast || i != childCount - 1) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                c.drawLine(
                        left,
                        top,
                        right,
                        top,
                        mDividerPaint);
            }
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.isDrawLast || i != childCount - 1) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                c.drawLine(
                        left,
                        top,
                        left,
                        bottom,
                        mDividerPaint
                );
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (this.mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, (int)dividerWidth);
        } else {
            outRect.set(0, 0, (int)dividerWidth, 0);
        }
    }
}
