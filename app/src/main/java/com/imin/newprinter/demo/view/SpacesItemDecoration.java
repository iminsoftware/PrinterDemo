package com.imin.newprinter.demo.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.utils.Utils;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 列数
     */
    private int mSpanCount;
    /**
     * 间距
     */
    private int mSpace;

    public SpacesItemDecoration(int spanCount, int space) {
        this.mSpanCount = spanCount;
        this.mSpace = Utils.dp2px(IminApplication.mContext,space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();
        if (position == 0 || position == 1) {
            outRect.top = 0;
        }
        outRect.bottom = mSpace;
        if (spanIndex == 0) {
            outRect.left = mSpace;
            outRect.right = mSpace / 2;
        } else {
            outRect.left = mSpace / 2;
            outRect.right = mSpace;
        }
    }
}