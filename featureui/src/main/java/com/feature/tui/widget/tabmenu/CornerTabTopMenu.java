package com.feature.tui.widget.tabmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.tabview.TabSelectedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/26 17:08
 */
public class CornerTabTopMenu extends LinearLayout {
    private LinearLayout llContent;
    private List<TextView> mTabs = new ArrayList();
    private TabSelectedCallback selectedCallback = null;
    private Context context;

    public CornerTabTopMenu(Context context) {
        this(context,null);
    }

    public CornerTabTopMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addChild();
    }

    private void addChild() {
        //添加一个横向容器
        llContent = new LinearLayout(context);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        llContent.setLayoutParams(params);
        llContent.setOrientation(HORIZONTAL);
        addView(llContent);
        llContent.setBackgroundResource(R.drawable.corner16_colorf5_bg);
    }

    /**
     * 添加tab子view
     */
    public void addTabs(List<String> items) {
        mTabs.clear();
        for (String element :items){
            TextView tabView = new TextView(context);
            tabView.setText(element);
            LayoutParams param = new LayoutParams(0, XUiDisplayHelper.dp2px(context, 32));
            param.weight = 1f;
            llContent.addView(tabView);
            mTabs.add(tabView);
            tabView.setLayoutParams(param);
            tabView.setGravity(Gravity.CENTER);
            tabView.setTextSize(14f);
            tabView.setTextColor(context.getResources().getColor(R.color.xui_color_de666666));
            tabView.setOnClickListener(v -> {
                selectTab(items.indexOf(element));
                if(selectedCallback != null){
                    selectedCallback.selected(items.indexOf(element));
                }
            });
        }
        //默认选中第一个
        selectTab(0);
    }

    /**
     *tab选中
     */
    public void selectTab(int selected) {
        for (int position = 0; position < mTabs.size(); position++) {
            TextView curTab = mTabs.get(position);
            if (selected == position) {
                curTab.setTextColor(context.getResources().getColor(R.color.xui_config_color_white));
                curTab.setBackgroundResource(R.drawable.corner16_08c5ac_bg);
            } else {
                curTab.setTextColor(context.getResources().getColor(R.color.xui_color_de666666));
                curTab.setBackgroundResource(0);
            }
        }
    }

    /**
     * tab选中回调
     */
    public void setSelectedCallback(TabSelectedCallback tabSelectedCallback) {
        this.selectedCallback = tabSelectedCallback;
    }
}
