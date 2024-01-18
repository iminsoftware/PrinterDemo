package com.feature.tui.widget.loading.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.feature.tui.R;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 17:04
 */
public class CircleProgressView extends View {
    private int mWidth = 0;
    private int mHeight = 0;
    private float mMaxValue = 100.0f;
    private float mValue = 0.0f;
    private int mProgressColor;
    private int mBackgroundColor;
    private int mTextSize;
    private int mTextColor;
    private boolean mRoundCap = false;
    private int mStrokeWidth;
    private Paint mBackgroundPaint;
    private Paint mPaint;
    private Paint mTextPaint;
    private String mText = "";
    private float mCircleRadius = 0f;
    private Point mCenterPoint = null;
    private RectF mArcOval;
    private Context context;


    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        setup(context, attrs);
    }

    private void initView() {
        Resources resources = context.getResources();
        mProgressColor = resources.getColor(R.color.xui_config_color_main);
        mBackgroundColor = resources.getColor(R.color.xui_config_module_divider_color_deep);
        mTextSize = resources.getDimensionPixelSize(R.dimen.sp_14);
        mTextColor = resources.getColor(R.color.xui_color_333333);
        mStrokeWidth = resources.getDimensionPixelSize(R.dimen.dp_4);

        mBackgroundPaint = new Paint();
        mPaint = new Paint();
        mTextPaint = new Paint();
        mArcOval = new RectF();
    }

    private void setup(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
            mProgressColor = array.getColor(R.styleable.CircleProgressView_progress_color, mProgressColor);
            mBackgroundColor = array.getColor(R.styleable.CircleProgressView_background_color, mBackgroundColor);
            mMaxValue = array.getFloat(R.styleable.CircleProgressView_max_value, mMaxValue);
            mValue = array.getFloat(R.styleable.CircleProgressView_current_value, mValue);
            mRoundCap = array.getBoolean(R.styleable.CircleProgressView_stroke_round_cap, mRoundCap);
            mStrokeWidth = array.getDimensionPixelSize(R.styleable.CircleProgressView_stroke_width, mStrokeWidth);
            mTextSize = array.getDimensionPixelSize(R.styleable.CircleProgressView_android_textSize, mTextSize);
            mTextColor = array.getColor(R.styleable.CircleProgressView_android_textColor, mTextColor);
            mText = array.getString(R.styleable.CircleProgressView_android_text);
            array.recycle();
        }
        if (mText == null) {
            mText = "";
        }
        configPaint(mTextColor, mTextSize, mRoundCap, mStrokeWidth);
    }


    private void configShape() {
        mCircleRadius = (Math.min(mWidth,mHeight) - mStrokeWidth) / 2f;
        mCenterPoint = new Point(mWidth / 2, mHeight / 2);
    }

    private void configPaint(int textColor, int textSize, boolean isRoundCap, int strokeWidth) {
        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (isRoundCap) {
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mPaint.setStrokeCap(Paint.Cap.BUTT);
        }

        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(strokeWidth);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setDither(true);

        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.AT_MOST == heightMode) {
            mHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_100);
        } else {
            mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (MeasureSpec.AT_MOST == widthMode) {
            mWidth = context.getResources().getDimensionPixelSize(R.dimen.dp_100);
        } else {
            mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        }
        configShape();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawText(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCircleRadius, mBackgroundPaint);
        mArcOval.left = mCenterPoint.x - mCircleRadius;
        mArcOval.right = mCenterPoint.x + mCircleRadius;
        mArcOval.top = mCenterPoint.y - mCircleRadius;
        mArcOval.bottom = mCenterPoint.y + mCircleRadius;
        canvas.drawArc(mArcOval, 270.0f, 360.0f * mValue / mMaxValue, false, mPaint);

    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }

        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = mArcOval.top + (mArcOval.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(mText, mCenterPoint.x, baseline, mTextPaint);

    }

    /**
     * 进度条是否属于错误状态
     */
    void showErrorProgress(boolean isError) {
        if (isError) {
            mPaint.setColor(context.getResources().getColor(R.color.xui_config_color_error));
            mTextPaint.setColor(context.getResources().getColor(R.color.xui_config_color_error));
        } else {
            mPaint.setColor(mProgressColor);
            mTextPaint.setColor(mTextColor);
        }
        invalidate();
    }


    /**
     * 设置进度条的底色
     */
    public void setProgressBackgroundColor(int backgroundColor) {
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    /**
     * 设置进度条的进度颜色
     */
    public void setProgressColor(int progressColor) {
        mPaint.setColor(progressColor);
        invalidate();
    }

    /**
     * 设置进度文案
     */
    public void setText(String text) {
        mText = text;
        if (mText == null) {
            mText = "";
        }
        invalidate();
    }

    /**
     * 设置进度文案的文字大小
     */
    public void setTextSize(int textSize) {
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * 设置进度文案的文字颜色
     */
    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        invalidate();
    }

    /**
     * 设置环形进度条的宽度
     */
    public void  setStrokeWidth(int strokeWidth) {
        if (mStrokeWidth != strokeWidth) {
            mStrokeWidth = strokeWidth;
            if (mWidth > 0) {
                configShape();
            }
            mPaint.setStrokeWidth(strokeWidth);
            mBackgroundPaint.setStrokeWidth(strokeWidth);
            invalidate();
        }
    }

    /**
     * 设置环形进度条的两端是否有圆形的线帽
     */
    public void  setStrokeRoundCap(boolean isRoundCap) {
        mRoundCap = isRoundCap;
        if (isRoundCap) {
            mPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mPaint.setStrokeCap(Paint.Cap.BUTT);
        }
        invalidate();
    }

    /**
     * 当前进度
     */
    public final float getProgress() {
        return mValue;
    }

    public final void setProgress(float progress) {
        float tempProgress = progress;
        if (progress < 0) {
            tempProgress = 0.0F;
        }

        if (progress > mMaxValue) {
            tempProgress = mMaxValue;
        }

        mValue = tempProgress;
        this.invalidate();
    }

    /**
     * 最大进度
     */
    public final float getMaxValue() {
        return this.mMaxValue;
    }

    public final void setMaxValue(float maxValue) {
        float tempMaxValue = maxValue;
        if (maxValue < 0) {
            tempMaxValue = 0.0F;
        }

        this.mMaxValue = tempMaxValue;
    }
}
