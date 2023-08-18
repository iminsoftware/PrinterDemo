package com.imin.newprinterdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.imin.newprinterdemo.R;
import com.imin.newprinterdemo.utils.Utils;


public class ProgressLine extends View {
    private Paint paint;
    private int width;
    private int height;
    Path path;
    private int startHeight;
    private int margin;


    public ProgressLine(Context context) {
        this(context, null);
    }

    public ProgressLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.light_gray));
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(Utils.dp2px(getContext(), 2));
        path = new Path();
        margin = Utils.dp2px(getContext(), 20);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        startHeight = height >> 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0, startHeight);
        path.lineTo(width, startHeight);
        canvas.drawPath(path, paint);
        path.reset();

        path.moveTo(margin, startHeight);
        path.lineTo(margin, Utils.dp2px(getContext(), 8) + startHeight);
        canvas.drawPath(path, paint);
        path.reset();

        path.moveTo(width - margin, startHeight);
        path.lineTo(width - margin, Utils.dp2px(getContext(), 8) + startHeight);
        canvas.drawPath(path, paint);
    }
}
