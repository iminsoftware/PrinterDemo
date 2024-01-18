package com.feature.tui.widget.pickerview.timer;

import com.feature.tui.widget.pickerview.view.WheelView;

import java.util.TimerTask;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:快速滑动松开手后需要滑动的Task
 */
public class InertiaTimerTask extends TimerTask {

    /**
     * 当前滑动速度
     */
    private float mCurrentVelocityY;
    /**
     * 手指离开屏幕时的初始速度
     */
    private final float mFirstVelocityY;
    private final WheelView mWheelView;
    private boolean isUp;

    /**
     * @param wheelView 滚轮对象
     * @param velocityY Y轴滑行速度
     */
    public InertiaTimerTask(WheelView wheelView, float velocityY) {
        super();
        this.mWheelView = wheelView;
        this.mFirstVelocityY = velocityY;
        mCurrentVelocityY = Integer.MAX_VALUE;
        isUp = mFirstVelocityY < 0.0f;
    }

    @Override
    public void run() {
        //防止闪动，对速度做一个限制。
        if (mCurrentVelocityY == Integer.MAX_VALUE) {
            if (Math.abs(mFirstVelocityY) > 2000F) {
                mCurrentVelocityY = isUp ? -2000F : 2000F;
            } else {
                mCurrentVelocityY = mFirstVelocityY;
            }
        }

        //发送handler消息 处理平顺停止滚动逻辑
        if (Math.abs(mCurrentVelocityY) >= 0.0F && Math.abs(mCurrentVelocityY) <= 20F) {
            mWheelView.cancelFuture();
            mWheelView.getHandler().sendEmptyMessage(MessageHandler.WHAT_SMOOTH_SCROLL);
            return;
        }

        float dy = 20.0F;
        if (isUp) {
            dy = -dy;
        }
        mWheelView.setTotalScrollY(mWheelView.getTotalScrollY() - dy);
        if (!mWheelView.isLoop()) {
            if ((mWheelView.getPreCurrentIndex() == 0 && mCurrentVelocityY >= 0)) {
                mWheelView.setTotalScrollY(0);
                mCurrentVelocityY = 0F;
            }
            if ((mWheelView.getPreCurrentIndex() == mWheelView.getItemsCount() - 1 && mCurrentVelocityY <= 0)) {
                mWheelView.setTotalScrollY(0);
                mCurrentVelocityY = 0F;
            }
        }

        if (isUp) {
            mCurrentVelocityY = mCurrentVelocityY + 20F;
        } else {
            mCurrentVelocityY = mCurrentVelocityY - 20F;
        }

        //刷新UI
        mWheelView.getHandler().sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
    }

}
