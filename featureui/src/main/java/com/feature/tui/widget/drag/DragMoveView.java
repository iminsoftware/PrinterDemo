package com.feature.tui.widget.drag;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R;

import java.util.List;

public class DragMoveView extends LinearLayout {

    private final byte TO_PREVIOUS = -10, TO_NEXT = 10, GO_TO_OTHER_PAGE = 110;

    private Scroller scroller;

    //真实、伪造的item
    private View realMoveView, forgeMoveView;

    //当前滑动的ViewPage、上、下ViewPage
    private DragPageGridView currentPageView, topPageView, bottomPageView;

    //容器高度
    private int containerHeight;

    //上下viewPage距离顶部边框距离
    private int topPageViewMarginTop, bottomPageViewMarginTop;

    //伪造滑动item大小
    private int contentWidth, contentHeight;

    //上下ViewPage分割线
    private int topBottomDevLineY;

    //垂直、水平图标margin
    private int marginVer, marginLand;

    //刚点击坐标与图标边缘间距 为计算伪造item位置完全重合
    private int currentCreviceX, currentCreviceY;

    //跳转页面消息
    private MyMessage myMsg;

    private DataChangeCallback dataChangeCallBack;

    private int oldIndex;

    private boolean oldIn;

    public DragMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        forgeMoveView = findViewById(R.id.content);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    public void initView(View clickView, int downX, int downY, int topPageViewMarginTop, int bottomPageViewMarginTop,
                         int containerHeight, DragPageGridView topPage, DragPageGridView bottomPage) {
        this.realMoveView = clickView;
        this.topPageViewMarginTop = topPageViewMarginTop;
        this.bottomPageViewMarginTop = bottomPageViewMarginTop;
        this.containerHeight = containerHeight;
        this.topPageView = topPage;
        this.bottomPageView = bottomPage;

        topBottomDevLineY = topPageViewMarginTop + topPage.getHeight() + DragContainer.dipToPx(getContext(), 12) / 2;
        if (downY < topBottomDevLineY) {
            currentPageView = topPageView;
        } else {
            currentPageView = bottomPageView;
        }

        oldIndex = (int) clickView.getTag();
        oldIn = currentPageView == topPageView;

        contentWidth = currentPageView.getChildrenWidth();
        contentHeight = currentPageView.getChildrenHeight();

        ViewGroup.LayoutParams lp = forgeMoveView.getLayoutParams();
        lp.width = contentWidth;
        lp.height = contentHeight;
        forgeMoveView.setLayoutParams(lp);

        DragBean ti = (DragBean) clickView.findViewById(R.id.img_view).getTag();
        ImageView imgView = forgeMoveView.findViewById(R.id.img_view);
        TextView textView = forgeMoveView.findViewById(R.id.text_view);
        imgView.setImageDrawable(ti.icon);
        textView.setText(ti.label);

        View imgContainer = findViewById(R.id.img_view_container);
        if (ti.statusOpen) {
            imgContainer.setBackgroundResource(R.drawable.drag_bg_item_open);
        } else {
            imgContainer.setBackgroundResource(R.drawable.drag_bg_item);
        }

        currentCreviceX = downX + currentPageView.getScrollX() - clickView.getLeft();//-clickView.getLeft()保证伪造item与拖动item完全重合
        currentCreviceY = downY - clickView.getTop() - (currentPageView == topPageView ? topPageViewMarginTop : bottomPageViewMarginTop);//-clickView.getTop() 保证伪造item与拖动item完全重合

        postDelayed(new Runnable() {
            @Override
            public void run() {
                View imgViewContainer = forgeMoveView.findViewById(R.id.img_view_container);
                marginLand = imgViewContainer.getLeft();
                marginVer = imgViewContainer.getTop();
            }
        }, 10);
    }

    /**
     * 坐标转换滑动距离 计算重合
     */
    private int[] initTrueLocation(int x, int y) {
        x = -x;
        y = -y;
        x += currentCreviceX;
        y += currentCreviceY;
        if (y > -topBottomDevLineY) {
            currentPageView = topPageView;
        } else {
            currentPageView = bottomPageView;
        }
        int[] array = checkSideAndChangePage(x, y);
        x = array[0];
        y = array[1];
        int iY = Math.abs(y) - ((currentPageView == topPageView) ? topPageViewMarginTop : bottomPageViewMarginTop) + 2 * marginVer;
        currentPageView.onEventMove(Math.abs(x) + 2 * marginLand, iY, realMoveView, currentPageView);
        return new int[]{x, y};
    }

    /**
     * scrollTo向左上为正，右下为负,x y 相对与父容器坐标，即SelfQSCustomizer左上角
     */
    public void myScrollTo(int x, int y) {
        Log.i("Event", "myScrollTo");
        int[] array = initTrueLocation(x, y);
        super.scrollTo(array[0], array[1]);
    }

    /**
     * 不能滑出容器(图标)
     */
    private int[] checkSideAndChangePage(int x, int y) {
        int[] array = {x, y};
        //顶部边界
        if (y > marginVer) {
            array[1] = marginVer;
        }
        int mY = -(containerHeight - (getResources().getDimensionPixelSize(R.dimen.drag_item_icon_height) + marginVer));
        //底部边界
        if (y < mY) {
            array[1] = mY;
        }
        //左边界
        if (x > marginLand) {
            array[0] = marginLand;
            putChangePageMsg(TO_PREVIOUS, array[0], array[1]);
        }
        int mX = -(currentPageView.getWidth() + marginLand - contentWidth);
        //右边界
        if (x < mX) {
            array[0] = mX;
            putChangePageMsg(TO_NEXT, array[0], array[1]);
        }
        //撤销跳到隔壁页面
        if (x < marginLand - 20 && x > mX + 20) {
            cancelChangePage();
        }
        return array;
    }

    public void onEventUp(int x, int y) {
        Log.i("Event", "onEventUp");
        initTrueLocation(x, y);
        //弹起使用动画归位
        currentPageView.onEventUp(realMoveView, forgeMoveView, this, currentPageView, dataChangeCallBack);
        cancelChangePage();
    }

    public void resetRealAndForgeMoveView() {
        //解决闪动问题
        if (forgeMoveView != null)
            forgeMoveView.clearAnimation();
        scrollTo(0, 0);
        if (realMoveView != null)
            realMoveView.setVisibility(VISIBLE);
        setVisibility(View.GONE);
    }

    private void cancelChangePage() {
        handler.removeMessages(GO_TO_OTHER_PAGE);
    }

    /**
     * 滑动到左右边界准备跳页面了
     */
    private void putChangePageMsg(byte tag, int x, int y) {
        if (myMsg == null)
            myMsg = new MyMessage();
        myMsg.whichPage = tag;
        myMsg.x = x;
        myMsg.y = y;
        if (handler.hasMessages(GO_TO_OTHER_PAGE)) {
            return;
        }
        Message msg = handler.obtainMessage(GO_TO_OTHER_PAGE);
        //在左右两边缘停留一小会即跳到隔壁页面
        handler.sendMessageDelayed(msg, 1500);
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull final Message msg) {
            if (msg.what == GO_TO_OTHER_PAGE) {
                if (myMsg.whichPage == TO_PREVIOUS) {
                    currentPageView.goToPage(currentPageView.getCurrentPage() - 1);
                } else if (myMsg.whichPage == TO_NEXT) {
                    currentPageView.goToPage(currentPageView.getCurrentPage() + 1);
                }
            }
            return false;
        }
    });

    private class MyMessage {
        public byte whichPage;
        public int x;
        public int y;
    }

    /**
     * 手指弹起时伪造item的坐标
     * 相对QSContainer
     */
    public int[] getUpLocationForgeMoveView() {
        return new int[]{-getScrollX(), -getScrollY()};
    }

    /**
     * 获取item的位置
     * 相对QSContainer
     */
    public int[] getUpLocationRealMoveView(int index, View realMoveView) {
        int columns, rows, childrenWidth, childrenHeight;
        int x, y, page, realMoveViewPage, currentPage;

        DragPageGridView currentPageView;
        if (topPageView.getViews().contains(realMoveView)) {
            currentPageView = topPageView;
        } else {
            currentPageView = bottomPageView;
        }

        columns = currentPageView.getColumns();
        rows = currentPageView.getRows();
        childrenWidth = currentPageView.getChildrenWidth();
        childrenHeight = currentPageView.getChildrenHeight();

        page = index / (currentPageView.getColumns() * currentPageView.getRows());
        realMoveViewPage = (int) realMoveView.getTag() / (currentPageView.getColumns() * currentPageView.getRows());
        currentPage = currentPageView.getCurrentPage();
        //判断realMoveView是否在当前页
        int forChangePage = ((currentPage - realMoveViewPage > 0) ? -1 : 1) * currentPageView.getWidth();
        if (currentPage - realMoveViewPage == 0) {
            forChangePage = 0;
        }
        x = (index % columns) * childrenWidth + forChangePage;
        y = (index / columns) * childrenHeight - page * rows * childrenHeight + (topPageView.getViews().contains(realMoveView) ? topPageViewMarginTop : bottomPageViewMarginTop);
        return new int[]{x, y};
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public boolean isOldIn() {
        return oldIn;
    }

    public DragPageGridView getTopPageView() {
        return topPageView;
    }

    public DragPageGridView getBottomPageView() {
        return bottomPageView;
    }

    public void setDataChangeCallBack(DataChangeCallback dataChangeCallBack) {
        this.dataChangeCallBack = dataChangeCallBack;
    }

    public DataChangeCallback getDataChangeCallBack() {
        return dataChangeCallBack;
    }

    public interface DataChangeCallback {

        void notifyDataChange(List<DragBean> newTopList, List<DragBean> newBottomList);

        void resetData();

        void hide();

    }

}
