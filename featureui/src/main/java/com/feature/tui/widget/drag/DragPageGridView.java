package com.feature.tui.widget.drag;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.feature.tui.R;

import java.util.ArrayList;
import java.util.List;

public class DragPageGridView extends ViewGroup {

    private final int animationDuration = 200;

    private final byte EVENT_SPC = 1;

    private byte columns = 4, rows, vecRows;

    private List<DragBean> datas = new ArrayList<>();

    private List<View> views = new ArrayList<>();

    private DragPageGridView otherSidePageView;

    //自己大小
    private int mWidth, mHeight;

    //item大小
    private int childrenWidth, childrenHeight;

    private int currentPage;

    //是否开始拖动过
    private boolean isBeginMove = false;

    private Scroller scroller;

    private int xDown, yDown, lastX;

    private VelocityTracker velocityTracker;

    private MyCallBack myCallBack;

    private boolean isAnimationIng = false;

    private PageIndicator layPoint;

    public DragPageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragPageGridView);
        rows = (byte) typedArray.getInteger(R.styleable.DragPageGridView_rows, columns);
        vecRows = rows;
        typedArray.recycle();
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
        setPaddingForCfgChange();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setPaddingForCfgChange();
    }

    /**
     * 横竖屏适配
     */
    private void setPaddingForCfgChange() {
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = 6;
            rows = 1;
        } else {
            columns = 4;
            rows = vecRows;
        }
        if (getVisibility() == View.VISIBLE)
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToPage(0);
                }
            }, 50);
    }

    public void setData(List<DragBean> mDataMap) {
        datas = mDataMap;
    }

    public void resetView() {
        removeAllViews();
        views = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            View son = getViewByIndex(i);
            addView(son);
            views.add(son);
        }
        setMeasuredDimension(mWidth * getPageSize(), mHeight);
        postInvalidate();
    }

    private View getViewByIndex(int index) {
        DragBean ti = datas.get(index);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.drag_item, null);
        ImageView imgView = v.findViewById(R.id.img_view);
        TextView textView = v.findViewById(R.id.text_view);
        imgView.setImageDrawable(ti.icon);
        textView.setText(ti.label);
        imgView.setTag(ti);
        if (index % 2 == 0) {
            ti.statusOpen = true;
        }
        v.setTag(index);

        View imgContainer = v.findViewById(R.id.img_view_container);
        if (ti.statusOpen) {
            imgContainer.setBackgroundResource(R.drawable.drag_bg_item_open);
        } else {
            imgContainer.setBackgroundResource(R.drawable.drag_bg_item);
        }

        v.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isBeginMove = true;
                myCallBack.onItemLongClick(v, DragPageGridView.this);
                return false;
            }
        });
        return v;
    }

    public int getPageSize() {
        return (int) Math.ceil(1.0f * datas.size() / (columns * rows));
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean goToPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex > getPageSize() - 1)
            return false;
        currentPage = pageIndex;
        scroller.startScroll(getScrollX(), 0, pageIndex * mWidth - getScrollX(), 0, 600);
        invalidate();
        myCallBack.pageChange(this, currentPage);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        int x = 0, y = 0;
        int[] array;
        for (int i = 0; i < views.size(); i++) {
            array = getLocationByIndex(i, columns, rows, childrenWidth, childrenHeight);
            x = array[0];
            y = array[1];
            views.get(i).layout(x, y, x + childrenWidth, y + childrenHeight);
        }
    }

    /**
     * 获取item的位置
     * 相对自己 即SelfPageGridView
     */
    private int[] getLocationByIndex(int index, int columns, int rows, int childrenWidth, int childrenHeight) {
        int x, y;
        int page = index / (columns * rows);
        x = (index % columns) * childrenWidth + page * columns * childrenWidth;
        y = (index / columns) * childrenHeight - page * rows * childrenHeight;
        return new int[]{x, y};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        childrenWidth = mWidth / columns;
        childrenHeight = mHeight / rows;
        for (int i = 0; i < views.size(); i++) {
            views.get(i).measure(childrenWidth, childrenWidth);
        }
    }

    private void smoothScrollBy(int dx, int dy) {
        scroller.startScroll(getScrollX(), 0, dx, 0, 600);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isBeginMove) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = x;
                yDown = y;
                break;
            case MotionEvent.ACTION_MOVE:
                //item拖动
                if (Math.abs(xDown - x) > EVENT_SPC || Math.abs(yDown - y) > EVENT_SPC)
                    isIntercept = true;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        lastX = x;
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scroller.isFinished()) {
            isBeginMove = false;
            return true;
        }
        velocityTracker.addMovement(ev);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(xDown - x) > EVENT_SPC || Math.abs(yDown - y) > EVENT_SPC) {
                    int deltaX = x - lastX;
                    scrollBy(-deltaX, 0);
                    pagePointAnim(xDown, x);
                }
                break;
            case MotionEvent.ACTION_UP:
                int sX = getScrollX();
                velocityTracker.computeCurrentVelocity(500);
                float xVelocity = velocityTracker.getXVelocity();
                //快速滑动
                if (Math.abs(xVelocity) > 1000) {
                    currentPage = xVelocity > 0 ? currentPage - 1 : currentPage + 1;
                } else {
                    //本次滑动距离
                    int changeX = sX - currentPage * mWidth;
                    //滑出超过一半
                    if (Math.abs(changeX) > mWidth / 2) {
                        currentPage = changeX > 0 ? currentPage + 1 : currentPage - 1;
                    }
                }
                currentPage = Math.max(0, Math.min(currentPage, getPageSize() - 1));
                int dx = currentPage * mWidth - sX;
                smoothScrollBy(dx, 0);
                velocityTracker.clear();
                myCallBack.pageChange(this, currentPage);
                break;
        }
        lastX = x;
        return true;
    }

    /**
     * 分页小点粘性动画
     *
     * @param xDown
     * @param xMove
     */
    public void pagePointAnim(int xDown, int xMove) {
        if (getCurrentPage() == 0 && xMove > xDown)
            return;
        layPoint.setLocation(getCurrentPage() - (xMove - xDown) * 1.0f / mWidth);
    }

    /**
     * 拖动ItemView  播放动画后重新布局
     */
    public void onEventMove(int x, int y, View realMoveView, DragPageGridView movePageView) {
        int[] index = getInsertIndex(x, y);
        if (index != null) {
            //拖动到的位置无效，拖动到最后一页空白处   get(index[2])IndexOutOfBoundsException
            if (index[2] >= movePageView.getViews().size()) {
                index[2] = movePageView.getViews().size();
                if (movePageView.isAnimationIng)
                    return;
                onEventMove(index[2], realMoveView, movePageView);
                return;
            }
            if (movePageView.getViews().get(index[2]) != realMoveView) {
                if (movePageView.isAnimationIng)
                    return;
                onEventMove(index[2], realMoveView, movePageView);
            }
        }
    }

    /**
     * 手指弹起 播放动画归位
     */
    public void onEventUp(final View realMoveView, final View forgeMoveView, final DragMoveView moveViewContainer,
                          final DragPageGridView movePageView, final DragMoveView.DataChangeCallback dataChangeCallBack) {
        if (movePageView.isAnimationIng || movePageView.otherSidePageView.isAnimationIng) {
            //确保现有动画播放完再播放
            movePageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animForEventUp(realMoveView, forgeMoveView, moveViewContainer, movePageView, dataChangeCallBack);
                }
            }, animationDuration);
            return;
        }
        animForEventUp(realMoveView, forgeMoveView, moveViewContainer, movePageView, dataChangeCallBack);
    }

    private void animForEventUp(final View realMoveView, final View forgeMoveView, final DragMoveView moveViewContainer,
                                final DragPageGridView movePageView, final DragMoveView.DataChangeCallback dataChangeCallBack) {
        int[] oldLocation = moveViewContainer.getUpLocationForgeMoveView();
        int[] newLocation = moveViewContainer.getUpLocationRealMoveView((int) realMoveView.getTag(), realMoveView);
        TranslateAnimation ta = new TranslateAnimation(0, newLocation[0] - oldLocation[0], 0, newLocation[1] - oldLocation[1]);
        ta.setDuration(animationDuration);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onUpAnimationEnd(realMoveView, forgeMoveView, moveViewContainer, movePageView, dataChangeCallBack);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        forgeMoveView.startAnimation(ta);
    }

    /**
     * 弹起手指播放动画完
     */
    private void onUpAnimationEnd(View realMoveView, View forgeMoveView, DragMoveView moveViewContainer, DragPageGridView finalMovePageView, DragMoveView.DataChangeCallback dataChangeCallBack) {
        Log.i("Event", "onUpAnimationEnd");
        isAnimationIng = false;
        moveViewContainer.resetRealAndForgeMoveView();
        //重新检查页面
        finalMovePageView.getMyCallBack().pageChange(finalMovePageView, finalMovePageView.getCurrentPage());
        finalMovePageView.getOtherSidePageView().getMyCallBack().pageChange(finalMovePageView.getOtherSidePageView(), finalMovePageView.getOtherSidePageView().getCurrentPage());

        if (dataChangeCallBack != null) {
            DragBean db = (DragBean) realMoveView.findViewById(R.id.img_view).getTag();
            int newIndex = (int) realMoveView.getTag();
            int oldIndex = moveViewContainer.getOldIndex();
            boolean newIn = moveViewContainer.getTopPageView().getViews().contains(realMoveView);
            boolean oldIn = moveViewContainer.isOldIn();
            //位置改了才发通知
            if (!(newIndex == oldIndex && newIn == oldIn)) {
                dataChangeCallBack.notifyDataChange(moveViewContainer.getTopPageView().getDatas(), moveViewContainer.getBottomPageView().getDatas());
            }
        }
    }

    private void onEventMove(final int index, final View realMoveView, final DragPageGridView movePageView) {
        int[] oldLocation, newLocation;
        int oldIndex = (int) realMoveView.getTag();

        DragPageGridView otherSide = movePageView.getOtherSidePageView();
        List<View> views = otherSide.getViews();
        //对面的pageView需要移除真实Item  出队动画
        boolean isAnim = false;
        if (views.contains(realMoveView)) {
            //需要动画最后item即本页下一item
            int maxIndex = (otherSide.getCurrentPage() + 1) * otherSide.getColumns() * otherSide.getRows();
            for (int i = oldIndex + 1; i <= Math.min(maxIndex, views.size() - 1); i++) {
                isAnim = true;
                oldLocation = getLocationByIndex(i, otherSide.getColumns(), otherSide.getRows(), otherSide.getChildrenWidth(), otherSide.getChildrenHeight());
                newLocation = getLocationByIndex(i - 1, otherSide.getColumns(), otherSide.getRows(), otherSide.getChildrenWidth(), otherSide.getChildrenHeight());
                initAnimation(views, i, oldLocation, newLocation, index, realMoveView, movePageView);
            }
        }

        views = movePageView.getViews();
        //拖动的真实item不在pageView里 入队动画
        if (!views.contains(realMoveView)) {
            //需要动画最后item即本页最后item
            int maxIndex = (movePageView.getCurrentPage() + 1) * movePageView.getColumns() * movePageView.getRows() - 1;
            for (int i = index; i <= Math.min(maxIndex, views.size() - 1); i++) {
                isAnim = true;
                oldLocation = getLocationByIndex(i, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                newLocation = getLocationByIndex(i + 1, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                initAnimation(views, i, oldLocation, newLocation, index, realMoveView, movePageView);
            }
        } else {
            //拖动的真实item在pageView里
            //往后拖
            if (index > oldIndex) {
                for (int i = oldIndex + 1; i <= index; i++) {
                    isAnim = true;
                    oldLocation = getLocationByIndex(i, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                    newLocation = getLocationByIndex(i - 1, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                    initAnimation(views, i, oldLocation, newLocation, index, realMoveView, movePageView);
                }
            } else {
                //往前拖
                for (int i = index; i < oldIndex; i++) {
                    isAnim = true;
                    oldLocation = getLocationByIndex(i, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                    newLocation = getLocationByIndex(i + 1, movePageView.getColumns(), movePageView.getRows(), movePageView.getChildrenWidth(), movePageView.getChildrenHeight());
                    initAnimation(views, i, oldLocation, newLocation, index, realMoveView, movePageView);
                }
            }
        }
        //没有动画-拖动的item在空白处，解决最后一个item拖动到对面空白处无效问题
        if (!isAnim) {
            resetLocation(index, realMoveView, movePageView);
        }
    }

    private void initAnimation(final List<View> views, int index, int[] oldLocation, int[] newLocation, final int reSetIndex, final View realMoveView, final DragPageGridView movePageView) {
        if (index >= views.size())
            return;
        final View v = views.get(index);
        TranslateAnimation ta = new TranslateAnimation(0, newLocation[0] - oldLocation[0], 0, newLocation[1] - oldLocation[1]);
        ta.setDuration(animationDuration);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //解决闪动问题
                v.clearAnimation();
                resetLocation(reSetIndex, realMoveView, movePageView);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isAnimationIng = false;
                        otherSidePageView.isAnimationIng = false;
                    }
                }, 5);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(ta);
        isAnimationIng = true;
    }

    /**
     * 插入新位置
     */
    private void resetLocation(int index, View realMoveView, DragPageGridView movePageView) {
        DragBean db = (DragBean) realMoveView.findViewById(R.id.img_view).getTag();
        DragPageGridView otherSide = movePageView.getOtherSidePageView();
        //对面的pageView需要移除
        if (otherSide.getViews().remove(realMoveView)) {
            otherSide.getDatas().remove(db);
            otherSide.removeView(realMoveView);
            otherSide.requestLayout();
        }
        movePageView.getViews().remove(realMoveView);
        movePageView.getDatas().remove(db);
        //最后一页可能越界，越界移动到最后位置
        if (index > movePageView.getViews().size()) {
            movePageView.getDatas().add(db);
            movePageView.getViews().add(realMoveView);
        } else {
            movePageView.getDatas().add(index, db);
            movePageView.getViews().add(index, realMoveView);
        }
        //不在该pageView需要add
        if (!includeView(movePageView, realMoveView)) {
            movePageView.addView(realMoveView);
        }
        movePageView.requestLayout();
    }

    private boolean includeView(DragPageGridView pageView, View son) {
        for (int i = 0; i < pageView.getChildCount(); i++) {
            if (pageView.getChildAt(i) == son)
                return true;
        }
        return false;
    }

    /**
     * @return 获取需要拖动到的位置 {x,y,index} 拖动到最后一页空白item处index即为最大为末位位置
     */
    private int[] getInsertIndex(int x, int y) {
        int xIndex = x / childrenWidth;
        int yIndex = y / childrenHeight;
        float xIndexF = 1.0f * x / childrenWidth;
        float yIndexF = 1.0f * y / childrenHeight;

        float mX = xIndexF - xIndex;
        float nY = yIndexF - yIndex;
        //滑动到item位置了
        if (0.02 < mX && mX < 0.98 && 0.02 < nY && nY < 0.98 && yIndex < rows) {
            int listIndex = currentPage * columns * rows + xIndex + yIndex * columns;
            if (listIndex > views.size())
                listIndex = views.size();
            return new int[]{xIndex, yIndex, listIndex};
        }
        return null;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (views == null || datas == null)
            return;
        for (int i = 0; i < views.size(); i++) {
            if (i >= datas.size())
                return;
            views.get(i).findViewById(R.id.img_view).setTag(datas.get(i));
            views.get(i).setTag(i);
        }
    }

    public MyCallBack getMyCallBack() {
        return myCallBack;
    }

    public List<DragBean> getDatas() {
        return datas;
    }

    public List<View> getViews() {
        return views;
    }

    public DragPageGridView getOtherSidePageView() {
        return otherSidePageView;
    }

    public void setOtherSidePageView(DragPageGridView otherSidePageView) {
        this.otherSidePageView = otherSidePageView;
    }

    public void setBeginMove(boolean beginMove) {
        isBeginMove = beginMove;
    }

    public boolean isBeginMove() {
        return isBeginMove;
    }

    public int getChildrenWidth() {
        return childrenWidth;
    }

    public int getChildrenHeight() {
        return childrenHeight;
    }

    public byte getColumns() {
        return columns;
    }

    public byte getRows() {
        return rows;
    }

    public void setLayPoint(PageIndicator layPoint) {
        this.layPoint = layPoint;
    }

    public void setMyCallBack(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    interface MyCallBack {

        void pageChange(DragPageGridView selfPageGridView, int page);

        void onItemLongClick(View clickView, DragPageGridView vpCurrentMove);

    }

}

