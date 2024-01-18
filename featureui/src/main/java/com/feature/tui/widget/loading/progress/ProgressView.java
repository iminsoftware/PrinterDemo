package com.feature.tui.widget.loading.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.feature.tui.R;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 17:32
 */
public class ProgressView extends View {
    public static final int TYPE_RECT = 0;
    public static final int TYPE_ROUND_RECT = 1;
    public static final int TEXT_TO_LEFT = 0;
    public static final int TEXT_TO_RIGHT = 1;
    public static final int TEXT_TO_TOP = 2;
    public static final int TEXT_TO_BOTTOM = 3;

    private RectF mBgRect = null;
    private RectF mProgressRect = null;
    private int mOffset ;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mType = TYPE_ROUND_RECT;
    private int mProgressHeight;
    private int mProgressWidth;
    private int mProgressColor;
    private int mBackgroundColor ;
    private float mMaxValue = 100.0f;
    private float mValue = 0.0f;
    private int mTextSize;
    private int mTextColor;
    private int mTextPadding = 0;
    private int mRealPadding;
    private int mTextDirection = TEXT_TO_RIGHT;
    private Paint mBackgroundPaint ;
    private Paint mPaint ;
    private Paint mTextPaint ;
    private Rect mTextRect = new Rect();
    private String mText = "";

    private Context context;

    public ProgressView(Context context) {
        this(context,null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mBackgroundPaint = new Paint();
        mPaint = new Paint();
        mTextPaint = new Paint();

        mOffset = resources.getDimensionPixelSize(R.dimen.dp_2);
        mProgressHeight = resources.getDimensionPixelSize(R.dimen.dp_4);
        mProgressWidth = resources.getDimensionPixelSize(R.dimen.dp_240);
        mProgressColor = resources.getColor(R.color.xui_config_color_main);
        mBackgroundColor = resources.getColor(R.color.xui_config_module_divider_color_deep);
        mRealPadding = resources.getDimensionPixelSize(R.dimen.dp_8);

    }


    private void setup(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
            mType = array.getInt(R.styleable.ProgressView_view_type, mType);
            mProgressHeight = array.getDimensionPixelSize(R.styleable.ProgressView_progress_height, mProgressHeight);
            mProgressWidth = array.getDimensionPixelSize(R.styleable.ProgressView_progress_width, mProgressWidth);
            mProgressColor = array.getColor(R.styleable.ProgressView_progress_color, mProgressColor);
            mBackgroundColor = array.getColor(R.styleable.ProgressView_background_color, mBackgroundColor);
            mMaxValue = array.getFloat(R.styleable.ProgressView_max_value, mMaxValue);
            mValue = array.getFloat(R.styleable.ProgressView_current_value, mValue);
            mTextSize = array.getDimensionPixelSize(R.styleable.ProgressView_android_textSize, mTextSize);
            mTextColor = array.getColor(R.styleable.ProgressView_android_textColor, mTextColor);
            mText = array.getString(R.styleable.ProgressView_android_text);
            mRealPadding = array.getDimensionPixelSize(R.styleable.ProgressView_text_Padding, mRealPadding);
            mTextDirection = array.getInt(R.styleable.ProgressView_text_direction, mTextDirection);
            array.recycle();
        }
        if (mText == null) {
            mText = "";
        }
        if(TextUtils.isEmpty(mText)){
            mTextPadding = 0;
        }else {
            mTextPadding = mRealPadding;
        }
        configPaint(mTextColor, mTextSize);
    }

    private void configPaint(int textColor, int textSize) {
        mBgRect = new RectF((float)getPaddingLeft(), (float)getPaddingTop(), (float)(mWidth + getPaddingLeft()), (float)(mHeight + getPaddingTop()));
        mProgressRect = new RectF();

        mPaint.setColor(mProgressColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.BUTT);

        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setProgressRect();
        if (mType == TYPE_RECT) {
            drawRect(canvas);
        } else if (mType == TYPE_ROUND_RECT) {
            drawRoundRect(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.AT_MOST == heightMode) {
            if (mTextDirection == TEXT_TO_TOP || mTextDirection == TEXT_TO_BOTTOM) {
                mHeight = mProgressHeight + mTextPadding + mTextRect.height() + 2 * mOffset;
            } else {
                mHeight = Math.max(mProgressHeight,mTextRect.height()) + 2 * mOffset;
            }
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
       if (MeasureSpec.AT_MOST == widthMode) {
            if (mTextDirection == TEXT_TO_TOP || mTextDirection == TEXT_TO_BOTTOM) {
                mWidth = Math.min(mProgressWidth,mTextRect.width()) + mOffset;
            } else {
                mWidth = mProgressWidth + mTextPadding + mTextRect.width() + mOffset;
            }
        } else {
           mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    private void setProgressRect() {
        switch (mTextDirection) {
            case TEXT_TO_LEFT: {
                mBgRect.left = mTextRect.width() + mTextPadding;
                mBgRect.right = mBgRect.left + mProgressWidth;
                mBgRect.top = (mHeight - mProgressHeight) / 2.0f;
                mBgRect.bottom = (mHeight + mProgressHeight) / 2.0f;
                mProgressRect.left = mBgRect.left;
                mProgressRect.right = mBgRect.left + parseValueToWidth();
                mProgressRect.top = mBgRect.top;
                mProgressRect.bottom = mBgRect.bottom;
            }
            break;
            case TEXT_TO_RIGHT: {
                mBgRect.left = 0f;
                mBgRect.right = mBgRect.left + mProgressWidth;
                mBgRect.top = (mHeight - mProgressHeight) / 2.0f;
                mBgRect.bottom = (mHeight + mProgressHeight) / 2.0f;
                mProgressRect.left = mBgRect.left;
                mProgressRect.right = mBgRect.left + parseValueToWidth();
                mProgressRect.top = mBgRect.top;
                mProgressRect.bottom = mBgRect.bottom;
            }
            break;

            case TEXT_TO_TOP: {
                mBgRect.left = 0f;
                mBgRect.right = mBgRect.left + mProgressWidth;
                mBgRect.top = mTextPaint.getTextSize() + mTextPadding;
                mBgRect.bottom = mBgRect.top + mProgressHeight;
                mProgressRect.left = mBgRect.left;
                mProgressRect.right = mBgRect.left + parseValueToWidth();
                mProgressRect.top = mBgRect.top;
                mProgressRect.bottom = mBgRect.bottom;
            }
            break;
            case TEXT_TO_BOTTOM : {
                mBgRect.left = 0f;
                mBgRect.right = mBgRect.left + mProgressWidth;
                mBgRect.top = 0f;
                mBgRect.bottom = mBgRect.top + mProgressHeight;
                mProgressRect.left = mBgRect.left;
                mProgressRect.right = mBgRect.left + parseValueToWidth();
                mProgressRect.top = mBgRect.top;
                mProgressRect.bottom = mBgRect.bottom;
            }
            break;
        }
    }

    private void drawRect(Canvas canvas) {
        canvas.drawRect(mBgRect, mBackgroundPaint);
        canvas.drawRect(mProgressRect, mPaint);
        drawText(canvas);
    }

    private void drawRoundRect(Canvas canvas) {
        float round = mHeight / 2f;
        canvas.drawRoundRect(mBgRect, round, round, mBackgroundPaint);
        canvas.drawRoundRect(mProgressRect, round, round, mPaint);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }


        switch (mTextDirection) {
            case TEXT_TO_LEFT: canvas.drawText(
                mText,
                mTextRect.width() / 2.0f,
                (mHeight + mTextRect.height()) / 2.0f - mOffset,
                mTextPaint
            );
            break;
            case TEXT_TO_RIGHT: canvas.drawText(
                mText,
                mProgressWidth + mTextPadding + mTextRect.width() / 2.0f,
                (mHeight + mTextRect.height()) / 2.0f - mOffset,
                mTextPaint
            );
            break;
            case TEXT_TO_TOP:
                canvas.drawText(mText, mWidth / 2.0f, mTextRect.height() - mOffset, mTextPaint);
                break;
            case TEXT_TO_BOTTOM: canvas.drawText(
                mText,
                mWidth / 2.0f,
                mProgressHeight + mTextPadding + mTextRect.height() - mOffset,
                mTextPaint
            );
            break;
        }
    }

    private final float parseValueToWidth() {
        return mMaxValue <= (float)0 ? 0.0F : (float)mProgressWidth * mValue / mMaxValue;
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
     * 设置类型，是否有圆角
     */
    public final void setType(int type) {
        mType = type;
        invalidate();
    }

    /**
     * 设置进度条的底色
     */
    public final void setProgressBackgroundColor(int backgroundColor) {
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    /**
     * 设置进度条的进度颜色
     */
    public final void setProgressColor(int progressColor) {
        mPaint.setColor(progressColor);
        invalidate();
    }

    /**
     * 设置进度文案
     */
    public final void setText(String text) {
        mText = text;
        if (mText == null) {
            mText = "";
        }
        if(TextUtils.isEmpty(mText)){
            mTextPadding = 0;
        }else {
            mTextPadding = mRealPadding;
        }
        requestLayout();
    }

    /**
     * 设置文字与进度条的间距
     */
    public final void setTextPadding(int textPadding) {
        mRealPadding = textPadding;
    }


    /**
     * 设置文字在进度条的显示方向，左边、右边、上面、下面
     */
    public final void setDirection(int textDirection) {
        mTextDirection = textDirection;
        requestLayout();
    }

    /**
     * 设置进度文案的文字大小
     */
    public final void setTextSize(int textSize) {
        mTextPaint.setTextSize((float)textSize);
        invalidate();
    }

    /**
     * 设置进度文案的文字颜色
     */
    public final void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        invalidate();
    }

    /**
     * 设置进度条的宽度
     */
    public final void setProgressHeight(int progressHeight) {
        mProgressHeight = progressHeight;
        requestLayout();
    }

    /**
     * 设置进度条的高度
     */
    public final void setProgressWidth(int progressWidth) {
        mProgressWidth = progressWidth;
        requestLayout();
    }


    /**
     * 当前进度
     */
    public final float getProgress() {
        return mValue;
    }

    public final void setProgress(float progress) {
        float tempProgress = progress;
        if (progress < (float)0) {
            tempProgress = 0.0F;
        }

        if (progress > mMaxValue) {
            tempProgress = mMaxValue;
        }

        mValue = tempProgress;
        invalidate();
    }
    /**
     * 最大进度
     */
    public final float getMaxValue() {
        return mMaxValue;
    }

    public final void setMaxValue(float maxValue) {
        float tempMaxValue = maxValue;
        if (maxValue < (float)0) {
            tempMaxValue = 0.0F;
        }

        mMaxValue = tempMaxValue;
    }
}
