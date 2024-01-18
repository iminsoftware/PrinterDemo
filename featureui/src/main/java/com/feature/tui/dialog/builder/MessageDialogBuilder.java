package com.feature.tui.dialog.builder;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feature.tui.R;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.util.XUiResHelper;


public class MessageDialogBuilder
        extends BaseDialogBuilder<MessageDialogBuilder> {
    private CharSequence mMessage;
    private int mMessageColorRes;
    private int mMessageSizeRes;
    private boolean mMessageIsCenter = true;
    private int mIconResId;
    private int mDrawablePadding;
    private int mDrawableOrientation;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int mContentMaxHeight;
    private int mContentMimHeight;

    public MessageDialogBuilder setMessage(CharSequence message) {
        return this.setMessage(message, 0, 0, true);
    }

    public MessageDialogBuilder setMessage(CharSequence message, int messageColorRes, int messageSizeRes, boolean messageIsCenter) {
        this.mMessage = message;
        this.mMessageColorRes = messageColorRes;
        this.mMessageSizeRes = messageSizeRes;
        this.mMessageIsCenter = messageIsCenter;
        return this;
    }

    public MessageDialogBuilder setMessage(int resId, int messageColorRes, int messageSizeRes, boolean messageIsCenter) {
        Context context = this.getBaseContext();
        return this.setMessage(context != null && context.getResources() != null ? context.getString(resId) : null, messageColorRes, messageSizeRes, messageIsCenter);
    }

    public MessageDialogBuilder setMessage(String str, int messageColorRes) {
        return this.setMessage(str, messageColorRes, 0, true);
    }

    public MessageDialogBuilder setMessageIcon(int iconResId, int drawablePadding, int drawableOrientation) {
        this.mIconResId = iconResId;
        this.mDrawablePadding = drawablePadding;
        this.mDrawableOrientation = drawableOrientation;
        return this;
    }

    public MessageDialogBuilder setMessageIconTitle(int iconResId, int drawablePadding, int drawableOrientation) {
        this.setMTitleIcon(iconResId);
        this.setMTitleIconPadding(drawablePadding);
        this.setMTitleDrawableOrientation(drawableOrientation);
        return this;
    }

    public MessageDialogBuilder setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.mPaddingLeft = paddingLeft;
        this.mPaddingTop = paddingTop;
        this.mPaddingRight = paddingRight;
        this.mPaddingBottom = paddingBottom;
        return this;
    }


    public MessageDialogBuilder setContentMaxHeight(int maxHeight) {
        this.mContentMaxHeight = maxHeight;
        return this;
    }


    public MessageDialogBuilder setContentMimHeight(int maxHeight) {
        this.mContentMimHeight = maxHeight;
        return this;
    }

    @Override

    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        if (TextUtils.isEmpty((CharSequence) this.mMessage)) {
            return null;
        }
        TextView tv = new TextView(context);
        if (this.mMessage instanceof SpannableString) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
        tv.setId(R.id.xui_dialog_content);
        tv.setText(this.mMessage);
        XUiResHelper.assignTextViewWithAttr(tv, R.attr.xui_dialog_content_message_style);
        if (this.mDrawablePadding > 0) {
            tv.setCompoundDrawablePadding(this.mDrawablePadding);
        }
        switch (this.mDrawableOrientation) {
            case 2: {
                tv.setCompoundDrawablesWithIntrinsicBounds(this.mIconResId, 0, 0, 0);
                break;
            }
            case 1: {
                tv.setCompoundDrawablesWithIntrinsicBounds(0, this.mIconResId, 0, 0);
                break;
            }
            case 3: {
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, this.mIconResId, 0);
                break;
            }
            case 0: {
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, this.mIconResId);
                break;
            }
        }
        try {
            if (this.mMessageColorRes > 0) {
                tv.setTextColor(tv.getResources().getColorStateList(this.mMessageColorRes));
            }
            if (this.mMessageSizeRes > 0) {
                tv.setTextSize(0, tv.getResources().getDimension(this.mMessageSizeRes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mMessageIsCenter) {
            tv.setGravity(1);
        } else {
            tv.setGravity(0x800003);
        }
        tv.setPadding(this.mPaddingLeft > 0 ? this.mPaddingLeft : tv.getPaddingLeft(), this.mPaddingTop > 0 ? this.mPaddingTop : tv.getPaddingTop(),
                this.mPaddingRight > 0 ? this.mPaddingRight : tv.getPaddingRight(), this.mPaddingBottom > 0 ? this.mPaddingBottom : tv.getPaddingBottom());
        if (this.mContentMimHeight > 0) {
            tv.setMinHeight(this.mContentMimHeight);
        }
        return wrapWithScroll(tv, this.mContentMaxHeight);
    }

    public MessageDialogBuilder(Context context) {
        super(context);
        this.mContentMaxHeight = (int) ((float) XUiDisplayHelper.getScreenHeight(context) * 0.5f);
    }
}
