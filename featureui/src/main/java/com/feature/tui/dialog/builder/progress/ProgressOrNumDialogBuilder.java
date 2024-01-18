package com.feature.tui.dialog.builder.progress;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.feature.tui.R;
import com.feature.tui.dialog.builder.BaseDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.loading.progress.ProgressView;

public  class ProgressOrNumDialogBuilder
        extends BaseDialogBuilder<ProgressOrNumDialogBuilder> {
    private ProgressView mProgressView;
    private TextView mLeftTextView;
    private TextView mRightTextView;
    private float mProgress;
    private float mMaxProgress;
    private CharSequence mLeftText;
    private CharSequence mRightText;

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        addProgressView(context, constraintLayout);
        addLeftTextView(context, constraintLayout);
        addRightTextView(context, constraintLayout);
        return constraintLayout;
    }

    private  void addProgressView(Context context, ConstraintLayout constraintLayout) {
        mProgressView = new ProgressView(context);
        mProgressView.setProgress(mProgress);
        mProgressView.setMaxValue(mMaxProgress);
        mProgressView.setProgressWidth(context.getResources().getDimensionPixelSize(R.dimen.dp_280));
        mProgressView.setId(R.id.dialog_progress_view);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.dp_280), ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        constraintLayout.addView(mProgressView, lp);
    }

    private  void addLeftTextView(Context context, ConstraintLayout constraintLayout) {
        mLeftTextView = new TextView(context);
        mLeftTextView.setText(mLeftText);
        mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_14));
        mLeftTextView.setTextColor(context.getResources().getColor(R.color.xui_color_333333));
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.topToBottom = mProgressView.getId();
        lp.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_4);
        constraintLayout.addView(mLeftTextView, lp);
    }

    private  void addRightTextView(Context context, ConstraintLayout constraintLayout) {
        mRightTextView = new TextView(context);
        mRightTextView.setText(mRightText);
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_14));
        mRightTextView.setTextColor(context.getResources().getColor(R.color.xui_color_333333));
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.topToBottom = mProgressView.getId();
        lp.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_4);
        constraintLayout.addView(mRightTextView, lp);
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.setMarginStart(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20));
        layoutParams.setMarginEnd(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20));
        return layoutParams;
    }

    /**
     * 设置进度条下面左边的文字
     */
    public  ProgressOrNumDialogBuilder setLeftText(CharSequence text) {
        mLeftText = text;
        if (mLeftTextView != null)
            mLeftTextView.setText(text);
        return this;
    }

    /**
     * 设置进度条下面右边的文字
     */
    public  ProgressOrNumDialogBuilder setRightText(CharSequence text) {
        mRightText = text;
        if (mRightTextView != null)
            mRightTextView.setText(text);
        return this;
    }

    /**
     * 设置当前进度
     */
    public  ProgressOrNumDialogBuilder setProgress(float progress) {
        mProgress = progress;
        if (mProgressView != null)
            mProgressView.setProgress(progress);
        return this;
    }

    /**
     * 设置最大进度
     */
    public  ProgressOrNumDialogBuilder setMaxProgress(float maxValue) {
        mMaxProgress = maxValue;
        if (mProgressView != null)
            mProgressView.setMaxValue(maxValue);
        return this;
    }

    public ProgressOrNumDialogBuilder(Context context) {
        super(context);
    }
}
