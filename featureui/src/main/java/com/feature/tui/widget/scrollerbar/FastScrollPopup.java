package com.feature.tui.widget.scrollerbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.feature.tui.R;

public class FastScrollPopup {
    private final int mCornerRadius;
    private final int mBackgroundHeight;
    private final Path mBackgroundPath = new Path();
    private final RectF mBackgroundRect = new RectF();
    private final Paint mBackgroundPaint;

    private final Rect mTmpRect = new Rect();

    private final Rect mBgBounds = new Rect();

    private String mSectionName;

    private final Paint mTextPaint;
    private final Rect mTextBounds = new Rect();

    private boolean mVisible;
    private final int mMargin;

    FastScrollPopup(Context context) {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int textSize = (int) context.getResources().getDimension(R.dimen.sp_12);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(Color.BLACK);
        mBackgroundHeight = (int) context.getResources().getDimension(R.dimen.dp_24);
        mCornerRadius = mBackgroundHeight / 2;
        mBackgroundPaint.setColor(Color.WHITE);
        mMargin = (int) context.getResources().getDimension(R.dimen.dp_20);
    }

    public void animateVisibility(boolean visible) {
        if (mVisible != visible) {
            mVisible = visible;
        }
    }

    private float[] createRadii() {
        return new float[]{mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius, mCornerRadius};
    }

    public void draw(Canvas canvas, int top, int left) {
        if (isVisible()) {
            int restoreCount = canvas.save();
            mBackgroundPaint.setShadowLayer(5f, 0, 3, Color.GRAY);
            mBgBounds.left = left - mBgBounds.width() - mMargin;

            mBgBounds.top = top;
            int bgPadding = Math.round((mBackgroundHeight - mTextBounds.height()) / 10f) * 5;

            int bgWidth = mTextBounds.width() + (4 * bgPadding);

            mBgBounds.right = mBgBounds.left + bgWidth;
            mBgBounds.bottom = mBgBounds.top + mBackgroundHeight;

            canvas.translate(mBgBounds.left, mBgBounds.top);
            mTmpRect.set(mBgBounds);
            mTmpRect.offsetTo(0, 0);
            mBackgroundPath.reset();
            mBackgroundRect.set(mTmpRect);

            float[] radii = createRadii();
            float baselinePosition;

            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            baselinePosition = (mBgBounds.height() - fontMetrics.ascent - fontMetrics.descent) / 2f;

            mBackgroundPath.addRoundRect(mBackgroundRect, radii, Path.Direction.CW);

            canvas.drawPath(mBackgroundPath, mBackgroundPaint);
            canvas.drawText(
                    mSectionName,
                    (mBgBounds.width() - mTextBounds.width()) / 2f,
                    baselinePosition,
                    mTextPaint
            );
            canvas.restoreToCount(restoreCount);
        }
    }

    public void setSectionName(String sectionName) {
        if (!sectionName.equals(mSectionName)) {
            mSectionName = sectionName;
            mTextPaint.getTextBounds(sectionName, 0, sectionName.length(), mTextBounds);
            mTextBounds.right = (int) (mTextBounds.left + mTextPaint.measureText(sectionName));
        }
    }

    public boolean isVisible() {
        return !TextUtils.isEmpty(mSectionName) && mVisible;
    }

}
