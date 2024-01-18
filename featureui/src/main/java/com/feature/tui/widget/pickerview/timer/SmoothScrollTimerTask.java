package com.feature.tui.widget.pickerview.timer;

import com.feature.tui.widget.pickerview.view.WheelView;

import java.util.TimerTask;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description: 松开手后平滑滑动到中间位置的Task
 */
public class SmoothScrollTimerTask extends TimerTask {

    private float realTotalOffset;
    private float realOffset;
    private final WheelView wheelView;

    public SmoothScrollTimerTask(WheelView wheelView, float offset) {
        this.wheelView = wheelView;
        realTotalOffset = offset;
        realOffset = 0;
    }

    @Override
    public final void run() {
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = realTotalOffset * 0.1F;

        if (realOffset == 0) {
            if (realTotalOffset < 0) {
                realOffset = -1;
            } else {
                realOffset = 1;
            }
        }

        if (Math.abs(realTotalOffset) <= 1) {
            wheelView.cancelFuture();
            wheelView.getHandler().sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED);
        } else {
            wheelView.setTotalScrollY(wheelView.getTotalScrollY() + realOffset);

            wheelView.getHandler().sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW);
            realTotalOffset = realTotalOffset - realOffset;
        }
    }

}
