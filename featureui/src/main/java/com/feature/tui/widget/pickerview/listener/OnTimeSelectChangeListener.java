package com.feature.tui.widget.pickerview.listener;

import java.util.Date;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:时间选择器回调回调
 */
public interface OnTimeSelectChangeListener {

    /**
     * 时间选择器回调
     * @param date
     */
    void onTimeSelectChanged(Date date);

    default void onHeadClick(Date date) {

    }

}
