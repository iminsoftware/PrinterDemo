package com.feature.tui.widget.drag;

import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.feature.tui.R;

import java.util.ArrayList;
import java.util.List;

public class DragContainer extends LinearLayout implements View.OnClickListener, DragPageGridView.MyCallBack {

    private View view;

    private DragPageGridView vpTop, vpBottom;

    private PageIndicator layPointTop, layPointBottom;

    private DragMoveView moveView;

    private long lastClickTime;

    private int downX, downY;

    //最初原始数据
    private List<DragBean> initDataTop, initDataBottom;

    private boolean isLayMenuAnimationIng = false;

    public DragContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.drag_panel, null);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_more).setOnClickListener(this);
        view.findViewById(R.id.tv_reset).setOnClickListener(this);

        vpTop = view.findViewById(R.id.vp_top);
        vpBottom = view.findViewById(R.id.vp_bottom);
        layPointTop = view.findViewById(R.id.lay_point_top);
        layPointBottom = view.findViewById(R.id.lay_point_bottom);
        vpTop.setLayPoint(layPointTop);
        vpBottom.setLayPoint(layPointBottom);

        addView(view);
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
        RelativeLayout vpTopContainer = view.findViewById(R.id.lay_container_top);
        RelativeLayout vpBottomContainer = view.findViewById(R.id.lay_container_bottom);
        LayoutParams lpT = (LayoutParams) vpTopContainer.getLayoutParams();
        LayoutParams lpB = (LayoutParams) vpBottomContainer.getLayoutParams();
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lpT.weight = 1;
            lpB.weight = 1;
        } else {
            lpT.weight = 54;
            lpB.weight = 73;
        }
        vpTopContainer.setLayoutParams(lpT);
        vpBottomContainer.setLayoutParams(lpB);
    }

    public void initData(List<DragBean> aDataTop, List<DragBean> aDataBottom) {
        initDataTop = new ArrayList<>(aDataTop);
        initDataBottom = new ArrayList<>(aDataBottom);
        vpTop.setData(aDataTop);
        vpBottom.setData(aDataBottom);
        initViewPager();
    }

    public void setMoveView(DragMoveView moveView) {
        this.moveView = moveView;
    }

    private void initViewPager() {
        vpTop.resetView();
        vpBottom.resetView();

        vpTop.setOtherSidePageView(vpBottom);
        vpBottom.setOtherSidePageView(vpTop);

        resetPointTips();

        vpTop.setMyCallBack(this);
        vpBottom.setMyCallBack(this);
    }

    /**
     * 设置SelfPageView页码显示(小点提示)
     */
    private void resetPointTips() {
        pageChange(vpTop.getCurrentPage(), true);
        pageChange(vpBottom.getCurrentPage(), false);
    }

    private void pageChange(int pageIndex, boolean isTop) {
        PageIndicator linearLayout = layPointTop;
        int newPages = vpTop.getPageSize();
        if (!isTop) {
            linearLayout = layPointBottom;
            newPages = vpBottom.getPageSize();
        }
        //拖动控件可能导致总页数减少或增加
        if (pageIndex > newPages - 1) {
            pageIndex = newPages - 1;
            if (isTop)
                vpTop.goToPage(pageIndex);
            else
                vpBottom.goToPage(pageIndex);
        }

        linearLayout.setNumPages(newPages, Color.WHITE);
        linearLayout.setLocation(pageIndex);
    }

    @Override
    public void onClick(View v) {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 300) {
            return;
        }
        lastClickTime = now;
        int id = v.getId();
        if (id == R.id.img_back) {
            vpTop.goToPage(0);
            vpBottom.goToPage(0);
            if (moveView.getDataChangeCallBack() != null)
                moveView.getDataChangeCallBack().hide();
        } else if (id == R.id.img_more) {
            showOrHiddenMenu(false);
        } else if (id == R.id.tv_reset) {
            vpTop.goToPage(0);
            vpBottom.goToPage(0);
            initData(initDataTop, initDataBottom);
            moveView.getDataChangeCallBack().resetData();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
            if (moveView.getDataChangeCallBack() != null)
                moveView.getDataChangeCallBack().hide();
        }
        return true;
    }

    /**
     * 右上角菜单显示与隐藏
     */
    private void showOrHiddenMenu(boolean onTouch) {
        final LinearLayout layMenu = findViewById(R.id.lay_menu);
        if (onTouch && layMenu.getVisibility() == View.GONE) {
            return;
        }
        if (isLayMenuAnimationIng)
            return;
        int state = 0;
        if (layMenu.getVisibility() == View.VISIBLE)
            state = 1;
        else if (!onTouch)
            state = 2;

        if (state > 0) {
            AlphaAnimation menuAlphaAnim = state == 1 ? new AlphaAnimation(1, 0) : new AlphaAnimation(0, 1);
            menuAlphaAnim.setDuration(200);
            final int stateTag = state;
            menuAlphaAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isLayMenuAnimationIng = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isLayMenuAnimationIng = false;
                    layMenu.setVisibility(stateTag == 1 ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            layMenu.startAnimation(menuAlphaAnim);
        }
    }

    @Override
    public void pageChange(DragPageGridView selfPageGridView, int page) {
        pageChange(page, selfPageGridView == vpTop ? true : false);
    }

    @Override
    public void onItemLongClick(View clickView, DragPageGridView vpCurrentMove) {
        Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(70);

        int topTitleHeight = findViewById(R.id.lay_title).getBottom();
        int topPageViewMarginTop = topTitleHeight + vpTop.getTop();
        int bottomContainerMarginToParentTop = findViewById(R.id.lay_container_bottom).getTop();
        int tvBottomHeight = findViewById(R.id.tv_bottom).getHeight();
        int bottomPageViewMarginTop = topTitleHeight + bottomContainerMarginToParentTop + tvBottomHeight;

        moveView.initView(clickView, downX, downY, topPageViewMarginTop, bottomPageViewMarginTop, getHeight(), vpTop, vpBottom);
        moveView.myScrollTo(downX, downY);
        moveView.setVisibility(View.VISIBLE);

        clickView.setVisibility(View.GONE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        showOrHiddenMenu(true);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Event", "onInterceptTouchEvent-down");
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("Event", "onInterceptTouchEvent-move");
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Event", "onInterceptTouchEvent-up");
                vpTop.setBeginMove(false);
                vpBottom.setBeginMove(false);
                //解决长按不移动可能item消失问题
                moveView.resetRealAndForgeMoveView();
                break;
        }
        return vpTop.isBeginMove() || vpBottom.isBeginMove();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!(vpTop.isBeginMove() || vpBottom.isBeginMove())) {
            return true;
        }
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Event", "onTouchEvent-up");
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("Event", "onTouchEvent-move");
                moveView.myScrollTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Event", "onTouchEvent-up");
                vpTop.setBeginMove(false);
                vpBottom.setBeginMove(false);
                moveView.onEventUp(x, y);
                break;
        }
        return true;
    }

    public static int dipToPx(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
