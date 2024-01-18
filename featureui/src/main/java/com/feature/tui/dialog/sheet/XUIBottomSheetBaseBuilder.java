package com.feature.tui.dialog.sheet;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R;
import com.feature.tui.layout.QXUIPriorityLinearLayout;

public abstract class XUIBottomSheetBaseBuilder<T extends XUIBottomSheetBaseBuilder> {
    private Context mContext;
    protected XUIBottomSheet mDialog;
    private CharSequence mTitle;
    private DialogInterface.OnDismissListener mOnBottomDialogDismissListener;
    private int mRadius = -1;
    private boolean mAllowDrag = false;
    private QXUIBottomSheetBehavior.DownDragDecisionMaker mDownDragDecisionMaker = null;

    private LayoutInflater inflater;
    private boolean hasTitleButton = false;
    // 右功能按钮文字
    private String rightBtnText;
    private OnButtonClickListener mCallback;

    public XUIBottomSheetBaseBuilder(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @SuppressWarnings("unchecked")
    public T setButtonClickListener(OnButtonClickListener listener) {
        mCallback = listener;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setTitle(CharSequence title) {
        mTitle = title;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setHasTitleButton(boolean has) {
        hasTitleButton = has;
        return (T) this;
    }

    protected boolean hasTitle() {
        return mTitle != null && mTitle.length() != 0;
    }

    @SuppressWarnings("unchecked")
    public T setAllowDrag(boolean allowDrag) {
        mAllowDrag = allowDrag;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDownDragDecisionMaker(QXUIBottomSheetBehavior.DownDragDecisionMaker downDragDecisionMaker) {
        mDownDragDecisionMaker = downDragDecisionMaker;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setRadius(int radius) {
        mRadius = radius;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setOnBottomDialogDismissListener(DialogInterface.OnDismissListener listener) {
        mOnBottomDialogDismissListener = listener;
        return (T) this;
    }

    public XUIBottomSheet build() {
        return build(R.style.XUI_BottomSheet);
    }

    public XUIBottomSheet build(int style) {
        mDialog = new XUIBottomSheet(mContext, style);
        Context dialogContext = mDialog.getContext();
        QXUIBottomSheetRootLayout rootLayout = mDialog.getRootView();
        rootLayout.removeAllViews();
        View titleView = onCreateTitleView(mDialog, rootLayout, dialogContext);
        initTitleView(titleView);
        mDialog.addContentView(titleView);
        onAddCustomViewBetweenTitleAndContent(mDialog, rootLayout, dialogContext);
        View contentView = onCreateContentView(mDialog, rootLayout, dialogContext);
        if (contentView != null) {
            QXUIPriorityLinearLayout.LayoutParams lp = new QXUIPriorityLinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setPriority(QXUIPriorityLinearLayout.LayoutParams.PRIORITY_DISPOSABLE);
            mDialog.addContentView(contentView, lp);
        }
        onAddCustomViewAfterContent(mDialog, rootLayout, dialogContext);
        if (mOnBottomDialogDismissListener != null) {
            mDialog.setOnDismissListener(mOnBottomDialogDismissListener);
        }
        if (mRadius != -1) {
            mDialog.setRadius(mRadius);
        }
        QXUIBottomSheetBehavior behavior = mDialog.getBehavior();
        behavior.setAllowDrag(mAllowDrag);
        behavior.setDownDragDecisionMaker(mDownDragDecisionMaker);
        return mDialog;
    }


    @NonNull
    protected View onCreateTitleView(@NonNull XUIBottomSheet bottomSheet,
                                     @NonNull QXUIBottomSheetRootLayout rootLayout,
                                     @NonNull Context context) {
        if (hasTitleButton) {
            return inflater.inflate(R.layout.bottom_sheet_title_layout, rootLayout, false);
        } else {
            return inflater.inflate(R.layout.bottom_sheet_title_no_buttons_layout, rootLayout, false);
        }
    }

    private void initTitleView(@NonNull View titleView) {
        if (hasTitleButton) {
            TextView tv = titleView.findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(mTitle)) tv.setText(mTitle);
            tv = titleView.findViewById(R.id.tv_save);
            if (!TextUtils.isEmpty(rightBtnText)) {
                tv.setText(rightBtnText);
            }
            tv.setOnClickListener(v -> {
                if (mCallback != null) mCallback.onRightButtonClick(v);
            });
            tv = titleView.findViewById(R.id.tv_cancel);
            tv.setOnClickListener(v -> mDialog.cancel());
        }
    }

    protected void onAddCustomViewBetweenTitleAndContent(@NonNull XUIBottomSheet bottomSheet,
                                                         @NonNull QXUIBottomSheetRootLayout rootLayout,
                                                         @NonNull Context context) {
    }

    @Nullable
    protected abstract View onCreateContentView(@NonNull XUIBottomSheet bottomSheet,
                                                @NonNull QXUIBottomSheetRootLayout rootLayout,
                                                @NonNull Context context);

    protected void onAddCustomViewAfterContent(@NonNull XUIBottomSheet bottomSheet,
                                               @NonNull QXUIBottomSheetRootLayout rootLayout,
                                               @NonNull Context context) {
    }

    interface OnButtonClickListener {
        void onRightButtonClick(View view);
    }
}
