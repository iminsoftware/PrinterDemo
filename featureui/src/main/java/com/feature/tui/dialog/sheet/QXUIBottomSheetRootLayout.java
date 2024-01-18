package com.feature.tui.dialog.sheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.feature.tui.R;
import com.feature.tui.layout.QXUIPriorityLinearLayout;
import com.feature.tui.util.XUiResHelper;

public class QXUIBottomSheetRootLayout extends QXUIPriorityLinearLayout {

    private int mUsePercentMinHeight;
    private float mHeightPercent;
    private int mMaxWidth;

    public QXUIBottomSheetRootLayout(Context context) {
        this(context, null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public QXUIBottomSheetRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bottom_sheet_root_bg);
//        setBackground(getResources().getDrawable(R.drawable.bottom_sheet_root_bg));
//        QXUISkinValueBuilder builder = QXUISkinValueBuilder.acquire();
//        builder.background(R.attr.qxui_skin_support_bottom_sheet_bg);
//        QXUISkinHelper.setSkinValue(this, builder);
//        builder.release();

        int radius = XUiResHelper.getAttrDimen(context, R.attr.qxui_bottom_sheet_radius);
        if (radius > 0) {
            setRadius(36, HIDE_RADIUS_SIDE_BOTTOM);
        }
        mUsePercentMinHeight = XUiResHelper.getAttrDimen(context, R.attr.qxui_bottom_sheet_use_percent_min_height);
        mHeightPercent = 0.75f;
        mMaxWidth = 1500;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (widthSize > mMaxWidth) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxWidth, widthMode);
        }
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize >= mUsePercentMinHeight) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    (int) (heightSize * mHeightPercent), View.MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
