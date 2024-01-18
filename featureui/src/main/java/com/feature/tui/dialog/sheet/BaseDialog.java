package com.feature.tui.dialog.sheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;

public class BaseDialog
        extends Dialog {

    public void dismiss() {
        Context context = getContext();
        if (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            Context activity = context;
            if (((Activity) activity).isDestroyed() || ((Activity) activity).isFinishing()) {
                return;
            }
            super.dismiss();
        } else {
            try {
                super.dismiss();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
}
