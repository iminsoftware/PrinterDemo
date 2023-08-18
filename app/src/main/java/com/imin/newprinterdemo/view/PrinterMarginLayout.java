package com.imin.newprinterdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.imin.newprinterdemo.R;
import com.imin.newprinterdemo.utils.Utils;

public class PrinterMarginLayout extends RelativeLayout {
    private ProgressLine view;
    private TextView mTextView;
    private Context mContext;
    private int viewLength, totalNum = 27, num;
    private int cX, mX;
    private int paddingWidth, leftOrRightPadding, numLength, dropBarRadius, eventX, left, right, lineLeft, currentNum;
    private Callback callback;
    private int currentPosition;

    public PrinterMarginLayout(@NonNull Context context) {
        this(context, null);
    }

    public PrinterMarginLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrinterMarginLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PrinterMarginLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        mContext = context;
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_printer_left_margin, this, true);
        view = (ProgressLine) inflate.findViewById(R.id.printer_margin_seekBar);
        mTextView = (TextView) inflate.findViewById(R.id.tv_drop_bar);
        paddingWidth = Utils.dp2px(mContext, 40);
        leftOrRightPadding = paddingWidth >> 1;

        mTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        eventX = (int) event.getX();
                        left = mTextView.getLeft();
                        right = mTextView.getRight();
                        cX = (int) (event.getRawX() - eventX);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mX = (int) (event.getRawX() - eventX - cX + 0.5);
                        if (0 < mX) {
                            if (left + mX > lineLeft + numLength - dropBarRadius) {
                                mX = 0;
                                mTextView.layout(lineLeft + numLength - dropBarRadius, mTextView.getTop(), lineLeft + numLength + dropBarRadius, mTextView.getBottom());
                                currentNum = totalNum;
                                mTextView.setText(String.valueOf(currentNum));
                                num = totalNum;
                                if (null != callback) {
                                    callback.callback(num);
                                }
                                return false;
                            }
                        } else if (mX < 0) {
                            if (left + mX < lineLeft - dropBarRadius) {
                                mX = 0;
                                mTextView.layout(lineLeft - dropBarRadius, mTextView.getTop(), lineLeft + dropBarRadius, mTextView.getBottom());
                                currentNum = 0;
                                num = 0;
                                mTextView.setText("0");
                                if (null != callback) {
                                    callback.callback(0);
                                }
                                return false;
                            }
                        }
                        mTextView.layout(left + mX, mTextView.getTop(), right + mX, mTextView.getBottom());
                        num = Math.round((mTextView.getLeft() + dropBarRadius - lineLeft) * totalNum / numLength);
                        mTextView.setText(String.valueOf(num));
                        if (null != callback) {
                            callback.callback(num);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewLength = view.getMeasuredWidth();
        numLength = viewLength - paddingWidth;
        dropBarRadius = mTextView.getMeasuredWidth() >> 1;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        lineLeft = view.getLeft() + leftOrRightPadding;
        if (currentPosition == 0) {
            mTextView.layout(lineLeft - dropBarRadius, mTextView.getTop(), lineLeft + dropBarRadius, mTextView.getBottom());
            num = 0;
        } else {
            mTextView.layout((int) (currentPosition * numLength / totalNum + lineLeft - dropBarRadius + 0.5), mTextView.getTop(),
                    (int) ((lineLeft + dropBarRadius + currentPosition * numLength / totalNum) + 0.5), mTextView.getBottom());
            num = (int) Math.round((mTextView.getLeft() + dropBarRadius - lineLeft) * totalNum / numLength + 0.5) > totalNum ? totalNum
                    : (int) Math.round((mTextView.getLeft() + dropBarRadius - lineLeft) * totalNum / numLength + 0.5);
        }
        mTextView.setText(String.valueOf(num));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getNum() {
        return num;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public void setTextViewSize(int size) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
