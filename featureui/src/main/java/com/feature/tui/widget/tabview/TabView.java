package com.feature.tui.widget.tabview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.cornermarkview.CornerMarkView;

/**
 * Author:LiangJiaXin
 * Time:2020/12/24 15:27
 * Description:tabview 底部菜单栏item或者grid菜单item
 **/
public class TabView extends RelativeLayout {
    private TextView title;
    private ImageView icon;
    private CornerMarkView cornerMarkView;
    private int mSelectedResource;
    private int mNormalResource;

    @RequiresApi(Build.VERSION_CODES.M)
    private void addChild() {
        //添加容器
        RelativeLayout rlContent = new RelativeLayout(getContext());
        Context context = getContext();

        int width = XUiDisplayHelper.dp2px(context, 72);
        int height = XUiDisplayHelper.dp2px(context, 52);
        LayoutParams rlParams = new LayoutParams(width, height);
        rlParams.addRule(CENTER_HORIZONTAL);
        rlContent.setLayoutParams(rlParams);
        addView(rlContent);

        //添加图标
        int icon_width = XUiDisplayHelper.dp2px(context, 24);
        int icon_height = XUiDisplayHelper.dp2px(context, 24);
        LayoutParams params = new LayoutParams(icon_width, icon_height);
        params.addRule(CENTER_HORIZONTAL);
        params.topMargin = XUiDisplayHelper.dp2px(getContext(), 8);
        icon.setLayoutParams(params);
        rlContent.addView(icon);

        //添加标题
        title.setText(context.getString(R.string.search));
        title.setTextSize(10F);
        title.setTextColor(getContext().getColor(R.color.xui_config_color_999999));


        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.topMargin = XUiDisplayHelper.dp2px(getContext(), 36);
        title.setLayoutParams(params);
        rlContent.addView(title);

        //添加角标
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(XUiDisplayHelper.dp2px(getContext(), 40));
        params.topMargin = XUiDisplayHelper.dp2px(getContext(), 2);
        cornerMarkView.setLayoutParams(params);
        rlContent.addView(cornerMarkView);
        setMarkView(true, "NEW");
    }

    /**
     * 选中后切换字体颜色、图标
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public void selected(boolean select) {
        if (select) {
            if (mSelectedResource == 0) {
                icon.setBackgroundResource(R.mipmap.common_default_icon);
            } else {
                icon.setBackgroundResource(mSelectedResource);
            }

            title.setTextColor(getContext().getColor(R.color.xui_config_color_main));
        } else {
            if (mSelectedResource == 0) {
                icon.setBackgroundResource(R.mipmap.common_default_icon);
            } else {
                icon.setBackgroundResource(mNormalResource);
            }

            title.setTextColor(getContext().getColor(R.color.xui_config_color_999999));
        }

    }

    /**
     * 是否显示角标
     */
    public void setMarkView(boolean show, Object content) {
        if (show) {
            cornerMarkView.setVisibility(View.VISIBLE);
            cornerMarkView.setMarkInfo(content);
        } else {
            cornerMarkView.setVisibility(View.GONE);
        }

    }

    /**
     * 设置tab图标（本地资源）、文字
     */
    public TabView initTab(int selectedResource, int normalResource, String text) {
        icon.setBackgroundResource(normalResource);
        mSelectedResource = selectedResource;
        mNormalResource = normalResource;
        title.setText(text);
        return this;
    }

    /**
     * 设置tab图标（网络资源）、文字
     */
    public TabView initTab(String iconUrl, String text) {
        return this;
    }


    @RequiresApi(Build.VERSION_CODES.M)
    public TabView(Context ctx) {
        super(ctx);
        title = new TextView(ctx);
        icon = new ImageView(ctx);
        cornerMarkView = new CornerMarkView(ctx);
        addChild();
    }


    @RequiresApi(Build.VERSION_CODES.M)
    public TabView(Context ctx, AttributeSet attributeSet) {
        super(ctx, attributeSet);
        title = new TextView(ctx);
        icon = new ImageView(ctx);
        cornerMarkView = new CornerMarkView(ctx);
        addChild();
    }
}
