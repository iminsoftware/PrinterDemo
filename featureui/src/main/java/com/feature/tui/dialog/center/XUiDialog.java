package com.feature.tui.dialog.center;

import android.content.Context;

import com.feature.tui.R;
import com.feature.tui.dialog.sheet.BaseDialog;

public class XUiDialog
        extends BaseDialog {
    public XUiDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public XUiDialog(Context context) {
        this(context, R.style.XUI_BaseDialog);
    }
}
