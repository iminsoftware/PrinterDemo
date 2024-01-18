package com.feature.tui.dialog.builder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.feature.tui.R;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;

import java.util.ArrayList;
import java.util.List;


public class AgreementDialogBuilder
        extends BaseDialogBuilder<AgreementDialogBuilder> {
    private CharSequence mMessage;
    private int mMessageColor;
    private int mMessageSize;
    private boolean mMessageIsCenter;
    private int mMessagePaddingLeft;
    private int mMessagePaddingTop;
    private int mMessagePaddingRight;
    private int mMessagePaddingBottom;
    private int mMessageLineSpacingExtra;
    private int mMessageMinHeight;
    private CharSequence mSubMessage;
    private int mSubMessageColor;
    private int mSubMessageSize;
    private boolean mSubMessageIsCenter;
    private int mSubMessagePaddingLeft;
    private int mSubMessagePaddingTop;
    private int mSubMessagePaddingRight;
    private int mSubMessagePaddingBottom;
    private List<ClickableAction> mClickableActions;
    private  Context context;

    /**
     * 添加可点击的文字
     *
     * @param clickableStr      文字
     * @param clickableColor    颜色
     * @param clickableListener 监听
     */
    public  AgreementDialogBuilder addClickableSpan(CharSequence clickableStr, int clickableColor, Functions.Fun clickableListener) {
        mClickableActions.add(new ClickableAction(this.context, clickableStr, clickableListener, clickableColor));
        return this;
    }

    /**
     * 添加可点击的文字
     */
    public  AgreementDialogBuilder addClickableSpan(CharSequence clickableStr, Functions.Fun clickableListener) {
        return this.addClickableSpan(clickableStr, context.getResources().getColor(R.color.xui_config_color_main), clickableListener);
    }

    /**
     * 设置对话框的消息文本
     *
     * @param messageColor    为0时表示用默认的
     * @param messageSize     为0时表示用默认的
     * @param messageIsCenter 默认左边对齐
     */
    public  AgreementDialogBuilder setMessage(CharSequence message, int messageColor, int messageSize, boolean messageIsCenter) {
        mMessage = message;
        if (messageColor != 0) {
            mMessageColor = messageColor;
        }
        if (messageSize > 0) {
            mMessageSize = messageSize;
        }
        mMessageIsCenter = messageIsCenter;
        return this;
    }

    /**
     * 设置对话框的消息文本
     *
     * @param messageColor    为0时表示用默认的
     * @param messageSize     为0时表示用默认的
     * @param messageIsCenter 默认左边对齐
     */
    public  AgreementDialogBuilder setMessage(int resId, int messageColor, int messageSize, boolean messageIsCenter) {
        Context context = getBaseContext();
        return this.setMessage(context.getString(resId), messageColor, messageSize, messageIsCenter);
    }

    /**
     * 设置对话框的消息文本
     */
    public  AgreementDialogBuilder setMessage(CharSequence message) {
        return this.setMessage(message, 0, 0, false);
    }

    /**
     * 设置内容的上下左右距离
     * 如果不设置或者小于0，则用之前的默认值
     */
    public  AgreementDialogBuilder setMessagePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        if (paddingLeft > 0) {
            mMessagePaddingLeft = paddingLeft;
        }
        if (paddingTop > 0) {
            mMessagePaddingTop = paddingTop;
        }
        if (paddingRight > 0) {
            mMessagePaddingRight = paddingRight;
        }
        if (paddingBottom > 0) {
            mMessagePaddingBottom = paddingBottom;
        }
        return this;
    }

    /**
     * 设置内容的最小高度，默认是60dp
     */
    public  AgreementDialogBuilder setMessageMinHeight(int minHeight) {
        mMessageMinHeight = minHeight;
        return this;
    }

    /**
     * 设置行间距，默认3dp
     */
    public  AgreementDialogBuilder setMessageLineSpacingExtra(int messageLineSpacingExtra) {
        mMessageLineSpacingExtra = messageLineSpacingExtra;
        return this;
    }

    /**
     * 设置对话框的子消息
     *
     * @param subMessageColor    为0时表示用默认的
     * @param subMessageSize     为0时表示用默认的
     * @param subMessageIsCenter 默认左边对齐
     */
    public  AgreementDialogBuilder setSubMessage(CharSequence subMessage, int subMessageColor, int subMessageSize, boolean subMessageIsCenter) {
        mSubMessage = subMessage;
        if (subMessageColor != 0) {
            mSubMessageColor = subMessageColor;
        }
        if (subMessageSize > 0) {
            mSubMessageSize = subMessageSize;
        }
        mSubMessageIsCenter = subMessageIsCenter;
        return this;
    }

    /**
     * 设置对话框的子消息
     */
    public  AgreementDialogBuilder setSubMessage(CharSequence subMessage) {
        return this.setSubMessage(subMessage, 0, 0, false);
    }

    /**
     * 设置子消息的上下左右距离
     * 如果不设置或者小于0，则用之前的默认值
     */
    public  AgreementDialogBuilder setSubMessagePadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        if (paddingLeft > 0) {
            mSubMessagePaddingLeft = paddingLeft;
        }
        if (paddingTop > 0) {
            mSubMessagePaddingTop = paddingTop;
        }
        if (paddingRight > 0) {
            mSubMessagePaddingRight = paddingRight;
        }
        if (paddingBottom > 0) {
            mSubMessagePaddingBottom = paddingBottom;
        }
        return this;
    }

    private  CharSequence setClickableClickableSpans() {
        if (mMessage == null) {
            return null;
        }
        SpannableString spannableString = new SpannableString(mMessage);
        for (int i = 0; i < mClickableActions.size(); i++) {
            ClickableAction action = mClickableActions.get(i);
            if (action.getMClickableStr() != null) {
                String str = action.getMClickableStr().toString();
                int startIndex = mMessage.toString().indexOf(str);
                if (startIndex > 0) {
                    int endIndex = startIndex + str.length();
                    spannableString.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            action.getMClickableListener().invoke(widget);
                        }
                    }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置部分文字颜色
                    spannableString.setSpan(new ForegroundColorSpan(action.getMClickableColor()),
                            startIndex,
                            endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        if (!TextUtils.isEmpty(mMessage)) {
            TextView tv = new TextView(context);
            tv.setId(R.id.xui_dialog_content);
            if (mMessage instanceof SpannableString) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
            tv.setText(setClickableClickableSpans());
            tv.setLineSpacing(mMessageLineSpacingExtra, 1.0f);
            tv.setTextColor(mMessageColor);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMessageSize);
            if (mMessageIsCenter) {
                tv.setGravity(Gravity.CENTER);
            } else {
                tv.setGravity(Gravity.START);
            }
            tv.setPadding(mMessagePaddingLeft, mMessagePaddingTop, mMessagePaddingRight, mMessagePaddingBottom);
            tv.setMinHeight(mMessageMinHeight);
            ViewGroup.LayoutParams tvLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.addView(tv, tvLayoutParams);
        }
        if (!TextUtils.isEmpty(mSubMessage)) {
            TextView subTv = new TextView(context);
            if (mSubMessage instanceof SpannableString) {
                subTv.setMovementMethod(LinkMovementMethod.getInstance());
            }
            subTv.setText(mSubMessage);
            subTv.setLineSpacing(mMessageLineSpacingExtra, 1.0f);
            subTv.setTextColor(mSubMessageColor);
            subTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.mSubMessageSize);
            if (mSubMessageIsCenter) {
                subTv.setGravity(Gravity.CENTER);
            } else {
                subTv.setGravity(Gravity.START);
            }
            subTv.setPadding(mSubMessagePaddingLeft, mSubMessagePaddingTop, mSubMessagePaddingRight, mSubMessagePaddingBottom);
            ViewGroup.LayoutParams subTvLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.addView(subTv, subTvLayoutParams);
        }
        return wrapWithScroll(layout, (int) (XUiDisplayHelper.getScreenHeight(context) * 0.7f));
    }

    public AgreementDialogBuilder(Context context) {
        super(context);
        this.context = context;
        this.mMessageColor = context.getResources().getColor(R.color.xui_config_color_title);
        this.mMessageSize = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.sp_14);
        this.mMessagePaddingLeft = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        this.mMessagePaddingTop = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_8);
        this.mMessagePaddingRight = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_12);
        this.mMessagePaddingBottom = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        this.mMessageLineSpacingExtra = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_3);
        this.mMessageMinHeight = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_60);
        this.mSubMessageColor = context.getResources().getColor(R.color.xui_config_color_title);
        this.mSubMessageSize = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.sp_14);
        this.mSubMessagePaddingLeft = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        this.mSubMessagePaddingRight = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_8);
        this.mSubMessagePaddingBottom = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_20);
        this.mClickableActions = new ArrayList();
    }

    public  class ClickableAction {

        private  CharSequence mClickableStr;

        private Functions.Fun mClickableListener;
        private  int mClickableColor;


        public  CharSequence getMClickableStr() {
            return this.mClickableStr;
        }


        public  Functions.Fun getMClickableListener() {
            return this.mClickableListener;
        }

        public  void setMClickableListener(Functions.Fun function1) {
            this.mClickableListener = function1;
        }

        public  int getMClickableColor() {
            return this.mClickableColor;
        }

        public ClickableAction(Context context, CharSequence mClickableStr, Functions.Fun mClickableListener, int mClickableColor) {
            this.mClickableStr = mClickableStr;
            this.mClickableListener = mClickableListener;
            this.mClickableColor = mClickableColor;
        }
    }
}
