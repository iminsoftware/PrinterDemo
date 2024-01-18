package com.feature.tui.dialog.builder.progress;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feature.tui.R;
import com.feature.tui.dialog.builder.BaseDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.loading.LoadingView;

public class LoadingDialogBuilder
        extends BaseDialogBuilder<LoadingDialogBuilder> {
    private LoadingView mLoadingView;
    private TextView mRightTextView;
    private String mText;
    private boolean mShowCancelBtn;
    private int loadingViewId = 911;

    public String getMText() {
        return this.mText;
    }

    public void setMText(String string) {
        this.mText = string;
    }

    public boolean getMShowCancelBtn() {
        return this.mShowCancelBtn;
    }

    public void setMShowCancelBtn(boolean bl) {
        this.mShowCancelBtn = bl;
    }

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        if (mText == null) {
            mText = getBaseContext().getString(R.string.empty_loading);
        }
        RelativeLayout relLay = new RelativeLayout(context);
        relLay.setPadding(0, 0, XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20), 0);
        addLoadingView(context, relLay);
        addRightTextView(context, relLay);
        if (mShowCancelBtn) {
            addRightCancelTextView(context, relLay);
        }
        return relLay;
    }

    private void addLoadingView(Context context, RelativeLayout linearLayout) {
        mLoadingView = new LoadingView(context);
        mLoadingView.setId(loadingViewId);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.dp_24), context.getResources().getDimensionPixelSize(R.dimen.dp_24));
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.addView(mLoadingView, lp);
    }

    private void addRightTextView(Context context, RelativeLayout linearLayout) {
        mRightTextView = new TextView(context);
        if (mText != null) {
            mRightTextView.setText(mText);
        }
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_14));
        mRightTextView.setTextColor(context.getResources().getColor(R.color.xui_color_333333));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.RIGHT_OF, loadingViewId);
        lp.setMarginStart(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_16));
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.addView(mRightTextView, lp);
    }

    private void addRightCancelTextView(Context context, RelativeLayout linearLayout) {
        TextView cancelTv = new TextView(context);
        cancelTv.setText("取消");
        cancelTv.setTextColor(context.getResources().getColorStateList(R.color.main_color_selector));
        cancelTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_15));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        linearLayout.addView(cancelTv, lp);
        cancelTv.setOnClickListener(it -> getDialog().dismiss());
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.setMarginStart(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20));
        return layoutParams;
    }

    /**
     * 设置加载条右边的文字
     */
    public LoadingDialogBuilder setRightText(String text) {
        mText = text;
        if (mRightTextView != null)
            mRightTextView.setText(text);
        return this;
    }

    /**
     * 设置是否显示取消按钮
     */
    public LoadingDialogBuilder setCancelBtn(boolean showCancelBtn) {
        mShowCancelBtn = showCancelBtn;
        return this;
    }

    public LoadingDialogBuilder(Context context) {
        super(context);
    }

}
