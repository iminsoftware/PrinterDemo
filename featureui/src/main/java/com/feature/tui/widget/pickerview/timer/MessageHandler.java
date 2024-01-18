package com.feature.tui.widget.pickerview.timer;

import android.os.Handler;
import android.os.Message;

import com.feature.tui.widget.pickerview.view.WheelView;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:
 */
public class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;

    private WheelView wheelView;

    public MessageHandler(WheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_INVALIDATE_LOOP_VIEW:
                wheelView.invalidate();
                break;

            case WHAT_SMOOTH_SCROLL:
                wheelView.smoothScroll(WheelView.ACTION.FLING);
                break;

            case WHAT_ITEM_SELECTED:
                if (wheelView.getTotalScrollY() != 0) {
                    wheelView.setTotalScrollY(0);
                    wheelView.invalidate();
                }
                wheelView.onItemSelected();
                break;
            default:
                break;
        }
    }

}
