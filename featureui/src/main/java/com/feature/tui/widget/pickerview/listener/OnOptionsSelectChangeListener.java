package com.feature.tui.widget.pickerview.listener;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:选择器回调监听
 */
public interface OnOptionsSelectChangeListener {

    /**
     * 选择器回调
     *
     * @param options1
     * @param options2
     * @param options3
     */
    void onOptionsSelectChanged(int options1, int options2, int options3);

}
