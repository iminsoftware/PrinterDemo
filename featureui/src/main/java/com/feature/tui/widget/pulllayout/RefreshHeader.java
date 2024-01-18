package com.feature.tui.widget.pulllayout;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R;
import com.feature.tui.widget.loading.LoadingView;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 14:09
 */
public class RefreshHeader extends FrameLayout implements  XUIPullLayout.ActionPullWatcherView {

    private LoadingView loadingView;
    private TextView tipsTv;
    private Context context;

    public RefreshHeader(@NonNull Context context) {
        this(context,null);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        Resources resource = context.getResources();
        LoadingView view = new LoadingView(context);
        int dp24 = resource.getDimensionPixelOffset(R.dimen.dp_24);
        int dp12 = resource.getDimensionPixelOffset(R.dimen.dp_12);
        FrameLayout.LayoutParams lp = new LayoutParams(
                dp24,
                dp24
        );
        lp.gravity = Gravity.CENTER;
        lp.topMargin = dp12;
        lp.bottomMargin = dp12;
        addView(view, lp);
        loadingView = view;

        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(12.0f);
        textView.setTextColor(resource.getColor(R.color.xui_config_color_main));
        textView.setVisibility(View.GONE);


        FrameLayout.LayoutParams txtLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        txtLp.gravity = Gravity.CENTER;
        txtLp.topMargin = dp12;
        txtLp.bottomMargin = dp12;
        addView(textView, txtLp);
        tipsTv = textView;
    }

    @Override
    public void onPull(XUIPullLayout.PullAction pullAction, int currentTargetOffset) {

    }

    @Override
    public void onActionTriggered() {
        loadingView.setVisibility(VISIBLE);
    }

    @Override
    public void onActionFinished(boolean success, @Nullable Integer count) {
        loadingView.setVisibility(View.GONE);
        tipsTv.setVisibility(View.VISIBLE);
        if (success) {
            tipsTv.setText(context.getResources().getString(R.string.pull_refresh_success, count));
            tipsTv.setTextColor(context.getResources().getColor(R.color.xui_config_color_main));
        } else {
            tipsTv.setText(context.getResources().getString(R.string.pull_refresh_failure));
            tipsTv.setTextColor(context.getResources().getColor(R.color.xui_config_color_error));
        }
    }

    @Override
    public void onActionEnd() {
        loadingView.setVisibility(GONE);

    }

    @Override
    public void onActionStart() {
        loadingView.setVisibility(VISIBLE);
        tipsTv.setVisibility(GONE);
    }
}
