package com.feature.tui.widget.layout;

import com.feature.tui.widget.pulllayout.XUIPullLayout;

public class QXUIAlwaysFollowOffsetCalculator implements XUIPullLayout.ActionViewOffsetCalculator {

    @Override
    public int calculateOffset(XUIPullLayout.PullAction pullAction, int targetOffset) {
        return targetOffset + pullAction.getActionInitOffset();
    }
}
