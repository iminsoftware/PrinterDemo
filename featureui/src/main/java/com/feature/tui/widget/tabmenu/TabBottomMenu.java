package com.feature.tui.widget.tabmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.tabview.TabSelectedCallback;
import com.feature.tui.widget.tabview.TabView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:LiangJiaXin
 * Time:2020/12/28 10:32
 * Description:底部菜单栏
 **/
public class TabBottomMenu extends LinearLayout {
    private List<TabView> mTabs;
    private TabSelectedCallback tabSelectedCallback;
    private TabView tabViewMore;
    private View popupView;
    private PopupWindow popupWindow;

    /**
     * 添加tab集合
     */
    @RequiresApi(23)
    public void addTab(List tabs) {
        setBackgroundColor(Color.WHITE);
        mTabs.clear();
        mTabs.addAll(tabs);
        if (mTabs.size() > 5) {
            for (int i = 0; i < 4; i++) {
                initTab(i);
            }

            tabViewMore = new TabView(getContext());
            tabViewMore.initTab(R.mipmap.common_default_icon, R.mipmap.common_default_icon, "更多");

            tabViewMore.setMarkView(false, "");
            addView(tabViewMore);
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0F;
            tabViewMore.setLayoutParams(params);
        } else {
            int size = mTabs.size();
            for (int i = 0; i < size; i++) {
                initTab(i);
            }
        }

    }

    @RequiresApi(23)
    private void initTab(int index) {
        TabView curTab = mTabs.get(index);
        addView(curTab);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0F;
        curTab.setLayoutParams(params);
        curTab.setOnClickListener(it -> {

            for (int clickPosition = 0; clickPosition < mTabs.size(); clickPosition++) {
                if (index == clickPosition) {
                    (mTabs.get(clickPosition)).selected(true);
                    if (tabSelectedCallback != null) {
                        tabSelectedCallback.selected(clickPosition);
                    }
                } else {
                    (mTabs.get(clickPosition)).selected(false);
                }
            }
        });
    }


    /**
     * 获取tab集合
     */
    public List<TabView> getTabs() {
        return mTabs;
    }

    /**
     * 设置选中tab回调
     */
    public void setSelectedCallback(TabSelectedCallback selectedCallback) {
        this.tabSelectedCallback = selectedCallback;
    }

    /**
     * 选中某个Tab
     */
    @RequiresApi(23)
    public void setSelected(int position) {
        if (mTabs.size() > position) {
            if (tabSelectedCallback != null) {
                tabSelectedCallback.selected(position);
            }
            for (int index = 0; index < mTabs.size(); index++) {
                if (index == position) {
                    (mTabs.get(index)).selected(true);
                } else {
                    (mTabs.get(index)).selected(false);
                }
            }
        }
    }


    /**
     * 设置更多按钮的弹出布局
     */
    @RequiresApi(21)
    public void setMoreClickLayout(int layoutId) {
        tabViewMore.setOnClickListener(it -> {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llContent = new LinearLayout(getContext());
            llContent.setOrientation(LinearLayout.VERTICAL);
            int[] location = new int[2];
            TabBottomMenu.this.getLocationOnScreen(location);

            //添加一个填充view
            TextView fillContent = new TextView(getContext());
            fillContent.setBackgroundColor(Color.argb(128, 0, 0, 0));
            llContent.addView(fillContent);

            popupView = LayoutInflater.from(getContext()).inflate(layoutId, llContent);

            popupWindow.setContentView(popupView);
            popupWindow.getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(TabBottomMenu.this, 0, 0, 0);

            //设置填充view的参数
            LayoutParams param = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    location[1] - popupWindow.getContentView().getMeasuredHeight() - XUiDisplayHelper.getStatusBarHeight(
                            getContext()));

            fillContent.setLayoutParams(param);
            fillContent.setOnClickListener(it1 -> popupWindow.dismiss());

            //状态栏颜色配合变化
            setStatusBarColor(Color.argb(128, 0, 0, 0));
            popupWindow.setOnDismissListener((PopupWindow.OnDismissListener) (new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setStatusBarColor(Color.argb(0, 0, 0, 0));
                }
            }));
        });
    }

    @RequiresApi(21)
    private void setStatusBarColor(int color) {
        Context context = getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(color);
        }
    }

    public View getMorePopView() {
        return popupView;
    }

    public TabBottomMenu(Context ctx) {
        super(ctx);
        mTabs = new ArrayList();
        setOrientation(HORIZONTAL);
    }

    public TabBottomMenu(Context ctx, AttributeSet attributeSet) {
        super(ctx, attributeSet);
        mTabs = new ArrayList();
        setOrientation(HORIZONTAL);
    }
}
