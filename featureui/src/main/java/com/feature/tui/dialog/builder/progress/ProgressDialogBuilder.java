package com.feature.tui.dialog.builder.progress;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.feature.tui.R;
import com.feature.tui.dialog.builder.BaseDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.loading.progress.ProgressView;

public class ProgressDialogBuilder
        extends BaseDialogBuilder<ProgressDialogBuilder> {
    private ProgressView mProgressView;
    private float mProgress;
    private float mMaxProgress;
    private String mText;

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        mProgressView = new ProgressView(context);
        mProgressView.setTextPadding(context.getResources().getDimensionPixelSize(R.dimen.dp_8));
        mProgressView.setProgress(mProgress);
        mProgressView.setMaxValue(mMaxProgress);
        mProgressView.setText(mText);
        mProgressView.setId(R.id.dialog_progress_view);
        return mProgressView;
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        return layoutParams;
    }

    /**
     * 设置进度条右边的文字
     */
    public  ProgressDialogBuilder setRightText(String text) {
        mText = text;
        if (mProgressView != null)
            mProgressView.setText(text);
        return this;
    }

    /**
     * 设置当前进度
     */
    public  ProgressDialogBuilder setProgress(float progress) {
        mProgress = progress;
        if (mProgressView != null)
            mProgressView.setProgress(progress);
        return this;
    }

    /**
     * 设置最大进度
     */
    public  ProgressDialogBuilder setMaxProgress(float maxValue) {
        mMaxProgress = maxValue;
        if (mProgressView != null)
            mProgressView.setMaxValue(maxValue);
        return this;
    }

    public ProgressDialogBuilder(Context context) {
        super(context);
    }
}
