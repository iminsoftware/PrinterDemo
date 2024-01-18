package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.feature.tui.dialog.center.XUiDialog;

public class CustomContentDialogBuilder
        extends BaseDialogBuilder<CustomContentDialogBuilder> {

    private static final String TAG = "CustomContentDialogBuilder";

    private View view;
    private int mProgress = 0;


    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        return view;
    }

    /**
     * 设置内容View
     */
    public CustomContentDialogBuilder setContentView(View view) {
        this.view = view;
        return this;
    }

    public CustomContentDialogBuilder(Context context) {
        super(context);
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER_HORIZONTAL;
        return layoutParams;
    }

    /**
     * 获取EditText中的内容
     */
    public int getProgress() {
        return mProgress;
    }
}
