package com.feature.tui.widget.tabmenu;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.google.android.material.tabs.TabLayout;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/26 16:59
 */
@RequiresApi(23)
public class IndicatorTabMenu extends TabLayout {
    private void addChild() {
        setTabIndicatorFullWidth(false);
        setSelectedTabIndicatorColor(getContext().getColor(R.color.xui_config_color_main));
        setTabRippleColorResource(R.color.xui_color_transparent);
        setTabTextColors(getContext().getColor(R.color.xui_color_666666), getContext().getColor(R.color.xui_config_color_main));
    }

    public void addTabs(String[] tabs) {
        int length = tabs.length;
        for (int pos = 0; pos < length; pos++) {
            String element = tabs[pos];
            addTab(newTab().setText(element));
        }
    }

    public IndicatorTabMenu(Context ctx) {
        super(ctx);
        addChild();
    }

    public IndicatorTabMenu(Context ctx, AttributeSet attributeSet) {
        super(ctx, attributeSet);
        addChild();
    }
}
