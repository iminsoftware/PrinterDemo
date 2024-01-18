package com.feature.tui.widget.pickerview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.adapter.WheelAdapter;
import com.feature.tui.widget.pickerview.listener.LoopViewGestureListener;
import com.feature.tui.widget.pickerview.listener.OnItemSelectedListener;
import com.feature.tui.widget.pickerview.timer.InertiaTimerTask;
import com.feature.tui.widget.pickerview.timer.MessageHandler;
import com.feature.tui.widget.pickerview.timer.SmoothScrollTimerTask;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WheelView extends View {

    /**
     * 点击，滑翔(滑到尽头)，拖拽事件
     */
    public enum ACTION {
        //点击
        CLICK,
        //完成
        FLING,
        //拖拽
        DRAG
    }

    private static final String[] TIME_NUM = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09"};

    /**
     * 星期的字符编码（以它为标准高度）
     */
    private String TEXT_EXAMPLES;

    private Handler handler;
    private GestureDetector gestureDetector;
    private OnItemSelectedListener onItemSelectedListener;

    private boolean isCenterLabel = false;

    private ScheduledExecutorService mExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintCenterLabel;
    private Paint paintIndicator;
    private Paint paintShader;
    private LinearGradient topBackGradient;
    private LinearGradient bottomBackGradient;


    private WheelAdapter<?> adapter;
    /**
     * 附加单位
     */
    private String label;
    /**
     * 选项的文字大小
     */
    private int textSize;
    private int maxTextWidth;
    private int centerTextHeight;
    private int outTextHeight;
    private int textXOffset;
    private float itemHeight;
    private int labelPadding;

    /**
     * 字体样式，默认是等宽字体
     */
    private Typeface typeface = Typeface.MONOSPACE;
    private int textColorOut;
    private int textColorCenter;
    private int dividerColor;
    private int dividerWidth;
    private boolean isLoop;
    private boolean hasDivider = true;
    /**
     * 第一条线Y坐标值
     */
    private float firstLineY;
    /**
     * 第二条线Y坐标
     */
    private float secondLineY;
    /**
     * 中间label绘制的Y坐标
     */
    private float centerY;
    /**
     * 当前滚动y值
     */
    private float totalScrollY;
    /**
     * 初始化默认选中项
     */
    private int initPosition;
    /**
     * 选中的Item是第几个
     */
    private int selectedItem;
    private int preCurrentIndex;
    /**
     * 绘制几个条目
     */
    private int itemsVisible = 5;
    /**
     * WheelView 控件高度
     */
    private int mHeight;
    /**
     * WheelView 控件宽度
     */
    private int mWidth;

    private float previousY = 0;
    private long startTime = 0;
    /**
     * 修改这个值可以改变滑行速度
     */
    private static final int VELOCITY_FLING = 10;
    private int widthMeasureSpec;
    private int heightMeasureSpec;

    private int mGravity = Gravity.CENTER;
    /**
     * 中间选中文字开始绘制位置
     */
    private int drawCenterContentStart = 0;
    /**
     * 非中间文字开始绘制位置
     */
    private int drawOutContentStart = 0;
    /**
     * 偏移量
     */
    private float mCenterContentOffset;

    /**
     * 滑动系数，值越大 ，滑动越慢
     */
    private float mSlidingCoefficient = 10.0f;

    private float mOffset;

    /**
     * 当前为月滚动条，是否自动计算滚动年份
     */
    private boolean isAutoUpdateYears;
    private int lastSelectItem;
    private int startYear;
    private int endYear;
    private int currentYear;

    private CallBack callBack;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TEXT_EXAMPLES = getContext().getString(R.string.pickerview_weeks);
        //默认大小
        textSize = getResources().getDimensionPixelSize(R.dimen.sp_18);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 屏幕密度比（0.75/1.0/1.5/2.0/3.0）
        float density = dm.density;
        //根据密度不同进行适配
        if (density < 1) {
            mCenterContentOffset = 2.4F;
        } else if (1 <= density && density < 2) {
            mCenterContentOffset = 4.0F;
        } else if (2 <= density && density < 3) {
            mCenterContentOffset = 6.0F;
        } else if (density >= 3) {
            mCenterContentOffset = density * 2.5F;
        }

        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.pickerview, 0, 0);
            mGravity = a.getInt(R.styleable.pickerview_wheelview_gravity, Gravity.CENTER);
            textColorOut = a.getColor(R.styleable.pickerview_wheelview_textColorOut, 0xFFa8a8a8);
            textColorCenter = a.getColor(R.styleable.pickerview_wheelview_textColorCenter, 0xFF2a2a2a);
            dividerColor = a.getColor(R.styleable.pickerview_wheelview_dividerColor, 0xFFeeeeee);
            dividerWidth = a.getDimensionPixelSize(R.styleable.pickerview_wheelview_dividerWidth, 1);
            textSize = a.getDimensionPixelOffset(R.styleable.pickerview_wheelview_textSize, textSize);
            a.recycle();
        }

        initLoopView(context);
    }


    private void initLoopView(Context context) {
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);
        isLoop = false;

        totalScrollY = 0;
        initPosition = -1;
        initPaints();
    }

    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(textColorOut);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(typeface);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(textColorCenter);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTypeface(typeface);
        paintCenterText.setTextSize(textSize);

        paintCenterLabel = new Paint();
        paintCenterLabel.setColor(textColorCenter);
        paintCenterLabel.setAntiAlias(true);
        paintCenterLabel.setTypeface(typeface);
        paintCenterLabel.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(dividerColor);
        paintIndicator.setStrokeWidth(dividerWidth);
        paintIndicator.setAntiAlias(true);

        paintShader = new Paint();
        setBackGradient();

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void setBackGradient() {
        //在小米手机上，这里的透明色值为#00ffffff才行
        int colorTransparent = getContext().getResources().getColor(R.color.xui_color_transparent);
        topBackGradient = new LinearGradient(0, 0, 0, itemHeight, new int[]{Color.WHITE, Color.WHITE, colorTransparent}, new float[]{0.0f, 0.25f, 1.0f}, Shader.TileMode.CLAMP);
        bottomBackGradient = new LinearGradient(0, itemHeight * (itemsVisible - 1), 0, itemHeight * itemsVisible, new int[]{colorTransparent, Color.WHITE, Color.WHITE,}, new float[]{0.0f, 0.75f, 1.0f}, Shader.TileMode.CLAMP);

    }

    /**
     * 重新测量
     */
    private void reMeasure() {
        if (adapter == null) {
            return;
        }

        measureTextWidthHeight();

        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (MeasureSpec.AT_MOST == mode) {
            mHeight = (int) (itemHeight * itemsVisible);
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        //控件宽度，这里支持weight
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        //计算两条横线 和 选中项画笔的基线Y位置
        firstLineY = (mHeight - itemHeight) / 2.0F;
        secondLineY = (mHeight + itemHeight) / 2.0F;
        centerY = secondLineY - (itemHeight - centerTextHeight) / 2.0f - mCenterContentOffset;
        //初始化显示的item的position
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (adapter.getItemsCount() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }
        initPosition = Math.min(Math.max(0, getCurrentItem()), adapter.getItemsCount() - 1);
        preCurrentIndex = initPosition;
    }

    /**
     * 计算最大length的Text的宽高度
     */
    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        for (int i = 0; i < adapter.getItemsCount(); i++) {
            String s1 = getContentText(adapter.getItem(i));
            paintCenterText.getTextBounds(s1, 0, s1.length(), rect);

            int textWidth = rect.width();
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
        }
        // 星期的字符编码（以它为标准高度）
        paintCenterText.getTextBounds(TEXT_EXAMPLES, 0, 2, rect);
        centerTextHeight = rect.height();
        paintOuterText.getTextBounds(TEXT_EXAMPLES, 0, 2, rect);
        outTextHeight = rect.height();
        if (itemHeight <= 0) {
            itemHeight = 2 * centerTextHeight;
        }
    }

    /**
     * 平滑滚动的实现
     *
     * @param action 动作枚举
     */
    public void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DRAG) {
            mOffset = (int) (totalScrollY % itemHeight);
            if (mOffset >= 0) {
                //如果超过Item高度的一半，滚动到下一个Item去
                if (mOffset > itemHeight / 2.0F) {
                    mOffset = itemHeight - mOffset;
                } else {
                    mOffset = -mOffset;
                }
            } else {
                //如果超过Item高度的一半，滚动到下一个Item去
                if (-mOffset > itemHeight / 2.0F) {
                    mOffset = -itemHeight - mOffset;
                } else {
                    mOffset = -mOffset;
                }
            }

        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS);
    }

    /**
     * 滚动惯性的实现
     *
     * @param velocityY
     */
    public final void scrollBy(float velocityY) {
        cancelFuture();
        if (!isLoop && (preCurrentIndex == 0 && velocityY >= 0)) {
            onItemSelected();
            return;
        }
        if (!isLoop && (preCurrentIndex == adapter.getItemsCount() - 1 && velocityY <= 0)) {
            onItemSelected();
            return;
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY / mSlidingCoefficient), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    public final void setCyclic(boolean cyclic) {
        isLoop = cyclic;
    }

    /**
     * @param font 设置字体样式
     */
    public final void setTypeface(Typeface font) {
        typeface = font;
        paintOuterText.setTypeface(typeface);
        paintCenterText.setTypeface(typeface);
    }

    /**
     * 设置当前的条目
     *
     * @param currentItem
     */
    public final void setCurrentItem(int currentItem) {
        //不添加这句,当这个wheelView不可见时,默认都是0,会导致获取到的时间错误
        this.selectedItem = currentItem;
        initPosition = Math.min(Math.max(0, currentItem), adapter.getItemsCount() - 1);
        preCurrentIndex = initPosition;
        //回归顶部，不然重设setCurrentItem的话位置会偏移的，就会显示出不对位置的数据
        totalScrollY = 0;
        invalidate();
    }

    /**
     * 设置选择器的监听
     *
     * @param onItemSelectedListener
     */
    public final void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public final void setAdapter(WheelAdapter adapter) {
        this.adapter = adapter;
        reMeasure();
        invalidate();
    }

    /**
     * 设置滚轮的最大可见数目
     */
    public void setItemsVisibleCount(int visibleCount) {
        if (visibleCount % 2 == 0) {
            visibleCount += 1;
        }
        this.itemsVisible = visibleCount;
        setBackGradient();
    }

    /**
     * 得到适配器
     */
    public final WheelAdapter getAdapter() {
        return adapter;
    }

    /**
     * 得到当前的条目
     *
     * @return
     */
    public final int getCurrentItem() {
        // return selectedItem;
        if (adapter == null) {
            return 0;
        }
        if (isLoop && (selectedItem < 0 || selectedItem >= adapter.getItemsCount())) {
            return Math.max(0, Math.min(Math.abs(Math.abs(selectedItem) - adapter.getItemsCount()), adapter.getItemsCount() - 1));
        }
        return Math.max(0, Math.min(selectedItem, adapter.getItemsCount() - 1));
    }

    public final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemSelectedListener.onItemSelected(getCurrentItem());
                }
            }, 200L);
        }
    }

    private void calculationRealTotalScrollY() {
        int offset = 2;
        float remainder = totalScrollY % itemHeight;
        if (remainder > 0) {
            if (offset > remainder) {
                totalScrollY = totalScrollY - remainder;
            } else if (remainder > itemHeight - offset) {
                totalScrollY = totalScrollY + itemHeight - remainder;
            }
        } else {
            if (offset > -remainder) {
                totalScrollY = totalScrollY - remainder;
            } else if (-remainder > itemHeight - offset) {
                totalScrollY = totalScrollY - itemHeight - remainder;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapter == null) {
            return;
        }
        calculationRealTotalScrollY();
        //滚动的Y值高度除去每行Item的高度，得到滚动了多少个item，即change数
        int change = (int) (totalScrollY / itemHeight);
        if (change != 0) {
            preCurrentIndex = preCurrentIndex + change;
            previousY = previousY - itemHeight * change;
            totalScrollY = totalScrollY % itemHeight;
        }

        //不循环的情况
        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = adapter.getItemsCount() - 1;
            }
        } else {
            //循环
            //举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
            if (preCurrentIndex < 0) {
                preCurrentIndex = adapter.getItemsCount() + preCurrentIndex;
            }
            //同理上面,自己脑补一下
            if (preCurrentIndex > adapter.getItemsCount() - 1) {
                preCurrentIndex = preCurrentIndex - adapter.getItemsCount();
            }
        }

        if (hasDivider) {
            //画分割线
            canvas.drawLine(0.0F, firstLineY, mWidth, firstLineY, paintIndicator);
            canvas.drawLine(0.0F, secondLineY, mWidth, secondLineY, paintIndicator);
        }


        // 设置数组中每个元素的值
        int counter = 0;
        while (counter < itemsVisible) {
            Object showText;
            //索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
            int index = preCurrentIndex - (itemsVisible / 2 - counter);
            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            if (isLoop) {
                index = getLoopMappingIndex(index);
                showText = adapter.getItem(index);
            } else if (index < 0) {
                showText = "";
            } else if (index > adapter.getItemsCount() - 1) {
                showText = "";
            } else {
                showText = adapter.getItem(index);
            }

            //获取内容文字
            String contentText;

            //如果是label每项都显示的模式，并且item内容不为空、label 也不为空
            if (!isCenterLabel && !TextUtils.isEmpty(label) && !TextUtils.isEmpty(getContentText(showText))) {
                contentText = getContentText(showText) + label;
            } else {
                contentText = getContentText(showText);
            }
            //计算开始绘制的位置
            measuredCenterContentStart(contentText);
            measuredOutContentStart(contentText);
            if (itemsVisible / 2 == counter) {
                int textWidth = getTextWidth(paintCenterText, contentText);
                canvas.drawText(getShowContent(contentText, textWidth, paintCenterText), drawCenterContentStart + textXOffset, centerY - totalScrollY, paintCenterText);
                //只显示选中项Label文字的模式，并且Label文字不为空，则进行绘制
                if (!TextUtils.isEmpty(label) && isCenterLabel) {
                    int drawLabelStart = drawCenterContentStart + textXOffset + labelPadding + textWidth;
                    canvas.drawText(label, drawLabelStart, centerY, paintCenterLabel);
                }
                //设置选中项
                selectedItem = preCurrentIndex - (itemsVisible / 2 - counter);
            } else {
                int textWidth = getTextWidth(paintOuterText, contentText);
                float Y = itemHeight * (counter + 1) - (itemHeight - outTextHeight) / 2.0f - mCenterContentOffset;
                canvas.drawText(getShowContent(contentText, textWidth, paintOuterText), drawOutContentStart + textXOffset, Y - totalScrollY, paintOuterText);

                if (counter == 0) {
                    paintShader.setShader(topBackGradient);
                    canvas.drawRect(0, 0, mWidth, itemHeight, paintShader);
                }
                if (counter == itemsVisible - 1) {
                    paintShader.setShader(bottomBackGradient);
                    canvas.drawRect(0, itemHeight * (itemsVisible - 1), mWidth, itemHeight * itemsVisible, paintShader);
                }

            }
            counter++;
        }

        checkYearsChange(selectedItem);

    }

    private String getShowContent(String content, int textWidth, Paint paint) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }

        if (textWidth < mWidth) {
            return content;
        }

        String ellipsis = "..";
        Rect rect = new Rect();
        paint.getTextBounds(ellipsis, 0, ellipsis.length(), rect);

        int iRet = rect.width();
        StringBuilder builder = new StringBuilder();
        int len = content.length();
        float[] widths = new float[len];
        paint.getTextWidths(content, widths);
        for (int j = 0; j < len; j++) {
            iRet += (int) Math.ceil(widths[j]);
            if (iRet > mWidth) {
                break;
            }
            builder.append(content.substring(j, j + 1));
        }
        builder.append(ellipsis);

        return builder.toString();
    }

    /**
     * 递归计算出对应的index
     *
     * @param index
     * @return
     */
    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index = index + adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        } else if (index > adapter.getItemsCount() - 1) {
            index = index - adapter.getItemsCount();
            index = getLoopMappingIndex(index);
        }
        return index;
    }

    /**
     * 获取所显示的数据源
     *
     * @param item data resource
     * @return 对应显示的字符串
     */
    private String getContentText(Object item) {
        if (item == null) {
            return "";
        } else if (item instanceof Integer) {
            //如果为整形则最少保留两位数.
            return getFixNum((int) item);
        }
        return item.toString();
    }

    private String getFixNum(int timeNum) {
        return timeNum >= 0 && timeNum < 10 ? TIME_NUM[timeNum] : String.valueOf(timeNum);
    }

    private void measuredCenterContentStart(String content) {
        int textWidth = getTextWidth(paintCenterText, content);
        switch (mGravity) {
            case Gravity.CENTER://显示内容居中
                if (textWidth < mWidth) {
                    drawCenterContentStart = (int) Math.ceil((mWidth - textWidth) * 0.5);
                } else {
                    drawCenterContentStart = 0;
                }
                break;
            case Gravity.LEFT:
                drawCenterContentStart = 0;
                break;
            case Gravity.RIGHT://添加偏移量
                drawCenterContentStart = mWidth - textWidth - (int) mCenterContentOffset;
                break;
            default:
                break;
        }
    }

    private void measuredOutContentStart(String content) {
        int textWidth = getTextWidth(paintOuterText, content);
        switch (mGravity) {
            case Gravity.CENTER:
                drawOutContentStart = (int) Math.ceil((mWidth - textWidth) * 0.5);
                if (textWidth < mWidth) {
                    drawOutContentStart = (int) Math.ceil((mWidth - textWidth) * 0.5);
                } else {
                    drawOutContentStart = 0;
                }
                break;
            case Gravity.LEFT:
                drawOutContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawOutContentStart = mWidth - textWidth - (int) mCenterContentOffset;
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        reMeasure();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        //超过边界滑动时，不再绘制UI。
        boolean isIgnore = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                totalScrollY = dy;


                if (!isLoop && (preCurrentIndex == 0 && dy <= 0)) {
                    isIgnore = true;
                    totalScrollY = 0.0f;
                }
                if (!isLoop && (preCurrentIndex == adapter.getItemsCount() - 1 && dy >= 0)) {
                    isIgnore = true;
                    totalScrollY = 0.0f;
                }


                break;

            case MotionEvent.ACTION_UP:
            default:
                //未消费掉事件
                if (!eventConsumed) {
                    if ((System.currentTimeMillis() - startTime) > 200) {
                        // 处理拖拽事件
                        smoothScroll(ACTION.DRAG);
                    } else {
                        // 处理条目点击事件
                        int position = (int) (event.getY() / itemHeight);
                        int centerPosition = itemsVisible / 2;
                        int movePosition = position - centerPosition;
                        if (isLoop || (preCurrentIndex + movePosition >= 0 && preCurrentIndex + movePosition <= adapter.getItemsCount() - 1)) {
                            mOffset = movePosition * itemHeight;
                        } else {
                            mOffset = 0;
                        }
                        smoothScroll(ACTION.CLICK);
                    }
                }

                break;
        }
        if (!isIgnore && event.getAction() != MotionEvent.ACTION_DOWN) {
            invalidate();
        }
        return true;
    }

    /**
     * 得到item的总数
     *
     * @return
     */
    public int getItemsCount() {
        return adapter != null ? adapter.getItemsCount() : 0;
    }

    /**
     * 得到显示的第一个item的位置
     *
     * @return
     */
    public int getPreCurrentIndex() {
        return preCurrentIndex;
    }

    /**
     * 设置附加单位
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 当 isCenterLabel=true时，此值作为文案和单位的间距
     *
     * @param labelPadding
     */
    public void setLabelPadding(int labelPadding) {
        this.labelPadding = labelPadding;
    }

    /**
     * 当 isCenterLabel=true时，设置单位的字体的大小
     *
     * @param textSizeLabel
     */
    public void setLabelTextSize(int textSizeLabel) {
        paintCenterLabel.setTextSize(textSizeLabel);
    }

    /**
     * 是否只有中间item才显示单位
     *
     * @param isCenterLabel
     */
    public void isCenterLabel(boolean isCenterLabel) {
        this.isCenterLabel = isCenterLabel;
    }

    /**
     * @param gravity 设置内容显示的位置 Gravity.CENTER、Gravity.LEFT、Gravity.RIGHT
     */
    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    /**
     * 得到文字的宽度
     *
     * @param paint
     * @param str
     * @return
     */
    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }

        return iRet;
    }

    /**
     * @param textColorOut    设置分割线以外文字的颜色，为color,不是资源id
     * @param textColorCenter 设置分割线之间的文字的颜色，为color,不是资源id
     */
    public void setTextColor(int textColorOut, int textColorCenter) {
        this.textColorOut = textColorOut;
        this.textColorCenter = textColorCenter;
        paintOuterText.setColor(textColorOut);
        paintCenterText.setColor(textColorCenter);
        paintCenterLabel.setColor(textColorCenter);
    }

    /**
     * @param textSizeOut    设置分割线以外文字的大小，为dimension,不是资源id
     * @param textSizeCenter 设置分割线之间的文字的大小，为dimension,不是资源id
     */
    public final void setTextSize(int textSizeOut, int textSizeCenter) {
        paintOuterText.setTextSize(textSizeOut);
        paintCenterText.setTextSize(textSizeCenter);
    }

    /**
     * 设置 文字的X轴偏移量
     *
     * @param textXOffset
     */
    public void setTextXOffset(int textXOffset) {
        this.textXOffset = textXOffset;
    }

    /**
     * 设置item的高度
     *
     * @param height
     */
    public void setItemHeight(float height) {
        this.itemHeight = height;
        setBackGradient();
    }

    /**
     * 设置分割线的宽度，为dimension,不是资源id
     *
     * @param dividerWidth
     */
    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        paintIndicator.setStrokeWidth(dividerWidth);
    }

    /**
     * 设置分割线的颜色，为color,不是资源id
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        paintIndicator.setColor(dividerColor);
    }

    /**
     * 是否有中心区域的分割线，默认true
     */
    public void hasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
    }

    /**
     * 是否循环
     *
     * @return
     */
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * 得到Y的滚动值
     *
     * @return
     */
    public float getTotalScrollY() {
        return totalScrollY;
    }

    /**
     * 设置Y的滚动值
     *
     * @return
     */
    public void setTotalScrollY(float totalScrollY) {
        this.totalScrollY = totalScrollY;
    }

    /**
     * 得到item的高度
     *
     * @return
     */
    public float getItemHeight() {
        return itemHeight;
    }

    /**
     * 得到初始的位置
     *
     * @return
     */
    public int getInitPosition() {
        return initPosition;
    }

    /**
     * 滑动系数，值越大 ，滑动越慢
     */
    public void setSlidingCoefficient(float slidingCoefficient) {
        mSlidingCoefficient = slidingCoefficient;
    }

    public void startAutoUpdateYears(int startYear, int endYear, int currentYear) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.currentYear = currentYear;
        lastSelectItem = selectedItem;
        isAutoUpdateYears = true;
    }

    public boolean isAutoUpdateYears() {
        return isAutoUpdateYears;
    }

    public int getAutoUpdateYears() {
        return currentYear;
    }

    /**
     * 更新年份
     */
    private void checkYearsChange(int selectedItem) {
        if (selectedItem == lastSelectItem)
            return;
        if (callBack != null)
            callBack.onScroll(selectedItem);
        if (!isAutoUpdateYears)
            return;
        if (Math.abs(selectedItem - lastSelectItem) > 7) {//年份切换
            if (selectedItem > 6) {//向下滑动,年份-1
                if (currentYear <= startYear)
                    return;
                currentYear--;
            } else {//向上滑动,年份+1
                if (currentYear >= endYear)
                    return;
                currentYear++;
            }
        }
        lastSelectItem = selectedItem;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    public interface CallBack {
        void onScroll(int selectedItem);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}