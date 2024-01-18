package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.feature.tui.R;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.util.XUiResHelper;

public class CheckDialogBuilder
        extends MessageDialogBuilder {
    private int checkBoxResId;
    private boolean isChecked;
    private String checkBoxMsg;
    private int bottomMargin;
    private Functions.Fun3 mCheckedChangeListener;

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        View view = super.onCreateContent(dialog, parent, context);
        if (view != null) {
            layout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        CheckBox checkBox = new CheckBox(context);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCheckedChangeListener.invoke(isChecked);
        });
        checkBox.setText(checkBoxMsg);
        checkBox.setChecked(isChecked);
        XUiResHelper.assignTextViewWithAttr(checkBox, R.attr.xui_dialog_content_checkbox_style);
        checkBox.setButtonDrawable(checkBoxResId);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        layoutParams.bottomMargin = bottomMargin;
        layout.addView(checkBox, layoutParams);
        return layout;
    }

    public CheckDialogBuilder(Context context, int checkBoxResId, boolean isChecked, String checkBoxMsg, int bottomMargin, Functions.Fun3 mCheckedChangeListener) {
        super(context);
        this.checkBoxResId = checkBoxResId;
        this.isChecked = isChecked;
        this.checkBoxMsg = checkBoxMsg;
        this.bottomMargin = bottomMargin;
        this.mCheckedChangeListener = mCheckedChangeListener;
    }

    public CheckDialogBuilder(Context context, int checkBoxResId, Functions.Fun3 mCheckedChangeListener) {
        this(context, checkBoxResId, false, context.getString(R.string.no_tips), XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20), mCheckedChangeListener);
    }

    public CheckDialogBuilder(Context context, int checkBoxResId, String checkBoxMsg, Functions.Fun3 mCheckedChangeListener) {
        this(context, checkBoxResId, false, checkBoxMsg, XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20), mCheckedChangeListener);
    }

}
