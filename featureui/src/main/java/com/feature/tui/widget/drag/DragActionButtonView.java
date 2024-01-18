package com.feature.tui.widget.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.feature.tui.util.XUiDisplayHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: zhongxing
 * @Date: 2021/4/19 10:46
 * @Description:
 */
public class DragActionButtonView extends View {

    /**
     * 触发挂断、接听事件可滑动距离
     */
    private int ACTION_FOCUS_LENGTH = 120;

    private int FOCUS_ALPHA = 255, FOCUS_ALPHA_ACTION = 150, FOCUS_STROKE = 6, FOCUS_STROKE_ACTION = 10;

    /**
     * 波纹密度 越小越密
     */
    private int CIRCLE_DENSITY = 35;

    /**
     * 控件大小
     */
    private final int[] SIZE = new int[2];

    /**
     * 水波纹最小半径
     */
    private final int MIN_SIZE = XUiDisplayHelper.dp2px(getContext(), 40);
    /**
     * 水波纹最大半径
     */
    private final int MAX_SIZE = XUiDisplayHelper.dp2px(getContext(), 70);

    /**
     * 挂断事件触发水平距离
     */
    private final int LEFT_EVENT_X = XUiDisplayHelper.dp2px(getContext(), 78);
    /**
     * 接听事件触发水平距离
     */
    private int RIGHT_EVENT_X;

    private List<Circle> circles = Collections.synchronizedList(new ArrayList<>());

    private Paint paint;
    private Paint paintFocus;
    private Paint paintLandScape;

    private Timer timer;

    /**
     * 是否处于拖动状态
     */
    private boolean isInTouch = false;

    private int downX;

    /**
     * 触发挂断、接听横坐标
     */
    private int focusX;

    private List<LandscapeCircle> leftCircles = Collections.synchronizedList(new ArrayList<>());
    private List<LandscapeCircle> rightCircles = Collections.synchronizedList(new ArrayList<>());

    /**
     * 水平圆点移动距离屏幕左右边缘长度
     */
    private int pointScrollToSide;

    private CallBack callBack;

    private boolean isStop;

    /**
     * 用与判断水波纹add时机
     */
    private int addEvent = 0;

    /**
     * 水波纹是否滑动到边缘数量变少
     */
    private boolean isRemoving = false;

    public DragActionButtonView(Context context) {
        super(context);
        init();
    }

    public DragActionButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragActionButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DragActionButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
                if (circles.size() <= 2) {
                    isRemoving = false;
                    if (leftCircles.size() > 0 || rightCircles.size() > 0)
                        isRemoving = true;
                }
                if (addEvent % CIRCLE_DENSITY == 0 && !isRemoving) {
                    if (!isInTouch) {
                        circles.add(new Circle(MIN_SIZE));
                        addPoints();
                    }
                }
                addEvent++;
            }
        }, 10, 10);

        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);

        paintFocus = new Paint();
        paintFocus.setColor(Color.GRAY);
        paintFocus.setStyle(Paint.Style.STROKE);
        paintFocus.setAlpha(FOCUS_ALPHA);
        paintFocus.setStrokeWidth(FOCUS_STROKE);

        paintLandScape = new Paint();
        paintLandScape.setColor(Color.WHITE);
        paintLandScape.setStyle(Paint.Style.FILL);

        circles.add(new Circle(MIN_SIZE, false));
        circles.add(new Circle(MAX_SIZE, false));
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                SIZE[0] = getMeasuredWidth();
                SIZE[1] = getMeasuredHeight();
                RIGHT_EVENT_X = SIZE[0] - LEFT_EVENT_X;
                pointScrollToSide = LEFT_EVENT_X - XUiDisplayHelper.dp2px(getContext(), 50);
            }
        }, 10);
    }

    private void addPoints() {
        leftCircles.add(new LandscapeCircle(XUiDisplayHelper.getScreenWidth(getContext()) / 2 - MIN_SIZE, true));
        rightCircles.add(new LandscapeCircle(XUiDisplayHelper.getScreenWidth(getContext()) / 2 + MIN_SIZE, false));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStop)
            return;
        if (isInTouch) {
            int radius = MIN_SIZE;
            paintFocus.setColor(Color.GRAY);
            paintFocus.setAlpha(FOCUS_ALPHA);
            paintFocus.setStrokeWidth(FOCUS_STROKE);
            if (focusX < LEFT_EVENT_X + ACTION_FOCUS_LENGTH) { //挂断
                paintFocus.setColor(Color.RED);
                paintFocus.setAlpha(FOCUS_ALPHA_ACTION);
                paintFocus.setStrokeWidth(FOCUS_STROKE_ACTION);
                radius -= XUiDisplayHelper.dp2px(getContext(), 5);
            } else if (focusX > RIGHT_EVENT_X - ACTION_FOCUS_LENGTH) {//接听
                paintFocus.setColor(Color.GREEN);
                paintFocus.setAlpha(FOCUS_ALPHA_ACTION);
                paintFocus.setStrokeWidth(FOCUS_STROKE_ACTION);
                radius -= XUiDisplayHelper.dp2px(getContext(), 5);
            }

            if (focusX < LEFT_EVENT_X) {
                focusX = LEFT_EVENT_X;
            } else if (focusX > RIGHT_EVENT_X) {
                focusX = RIGHT_EVENT_X;
            }

            canvas.drawCircle(focusX, SIZE[1] / 2, radius, paintFocus);
            return;
        }
        for (int i = 0; i < circles.size(); i++) {
            circles.get(i).draw(canvas);
        }

        for (int i = 0; i < leftCircles.size(); i++) {
            leftCircles.get(i).draw(canvas);
        }
        for (int i = 0; i < rightCircles.size(); i++) {
            rightCircles.get(i).draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                int spc = XUiDisplayHelper.dp2px(getContext(), 60);
                if (downX < LEFT_EVENT_X + spc || downX > RIGHT_EVENT_X - spc) {
                    return false;
                }
                isInTouch = true;
                focusX = SIZE[0] / 2;
                break;
            case MotionEvent.ACTION_MOVE:
                focusX = SIZE[0] / 2 + (int) event.getRawX() - downX;
                break;
            case MotionEvent.ACTION_UP:
                if (isInTouch && callBack != null) {
                    if (focusX < LEFT_EVENT_X + ACTION_FOCUS_LENGTH) { //挂断
                        isStop = true;
                        timer.cancel();
                        callBack.leftAction();
                    } else if (focusX > RIGHT_EVENT_X - ACTION_FOCUS_LENGTH) {//接听
                        isStop = true;
                        timer.cancel();
                        callBack.rightAction();
                    }
                }
                isInTouch = false;
                clearCircle();
                leftCircles.clear();
                rightCircles.clear();
                break;
        }
        return true;
    }

    private void clearCircle() {
        circles.removeIf(circle -> circle.isSizeChange);
    }

    class Circle {
        int alpha = 255;
        float size;
        float stroke = 2;
        /**
         * 大小是否可变
         */
        boolean isSizeChange = true;

        Circle(int size) {
            this.size = size;
        }

        Circle(int size, boolean isSizeChange) {
            this.size = size;
            this.isSizeChange = isSizeChange;
        }

        void update() {
            if (isSizeChange) {
                size += 0.7f;
            }
            if (size > MAX_SIZE) {
                circles.remove(this);
                isRemoving = true;
                size = MIN_SIZE;
            }
            alpha = (int) (MAX_SIZE - size);
            paint.setColor(Color.GRAY);
            stroke = size / 35;
            if (!isSizeChange) {
                if (size == MIN_SIZE) {
                    stroke = FOCUS_STROKE;
                    alpha = FOCUS_ALPHA;
                } else {
                    stroke = 3;
                    alpha = 20;
                }
            }
        }

        void draw(Canvas canvas) {
            update();
            paint.setAlpha(alpha);
            paint.setStrokeWidth(stroke);
            canvas.drawCircle(SIZE[0] / 2, SIZE[1] / 2, size, paint);
        }
    }

    class LandscapeCircle extends Circle {
        /**
         * 小点滑动速度
         */
        final float speed = 8;
        /**
         * 小点最大半径
         */
        final float maxSize = XUiDisplayHelper.dp2px(getContext(), 6);

        final float pointSize = 0.7f;
        int x;
        int initX;
        boolean isLeft;
        boolean isRemoved = false;

        LandscapeCircle(int x, boolean isLeft) {
            super(x);
            this.x = x;
            this.isLeft = isLeft;
            initX = x;
            size = maxSize;
        }

        void update() {
            if (isLeft) {
                x -= speed;
                if (x < pointScrollToSide) {
                    leftCircles.remove(this);
                    isRemoved = true;
                    return;
                }
                size = (maxSize / (SIZE[0] / 2 - MIN_SIZE)) * x * pointSize;
            } else {
                x += speed;
                if (x > SIZE[0] - pointScrollToSide) {
                    rightCircles.remove(this);
                    isRemoved = true;
                    return;
                }
                size = (maxSize / (getMeasuredWidth() - SIZE[0] / 2 - MIN_SIZE)) * (getMeasuredWidth() - x) * pointSize;
            }
            paintLandScape.setAlpha((int) size * 4);
        }

        @Override
        void draw(Canvas canvas) {
            update();
            if (isRemoved)
                return;
            canvas.drawCircle(x, SIZE[1] / 2, size, paintLandScape);
        }
    }

    public interface CallBack {

        void leftAction();

        void rightAction();

    }

}
