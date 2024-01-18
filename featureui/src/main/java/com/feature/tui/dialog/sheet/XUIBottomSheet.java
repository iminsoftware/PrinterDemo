package com.feature.tui.dialog.sheet;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;
import com.feature.tui.layout.IQXUILayout;
import com.feature.tui.layout.QXUIPriorityLinearLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;


/**
 * QXUIBottomSheet 在 [Dialog] 的基础上重新定制了 [.show] 和 [.hide] 时的动画效果, 使 [Dialog] 在界面底部升起和降下。
 * <p>
 * <p>
 * 提供了以下两种面板样式:
 * <p>
 * * 列表样式, 使用 [BottomListSheetBuilder] 生成。
 */
public class XUIBottomSheet
        extends BaseDialog {
    private QXUIBottomSheetRootLayout rootView;
    private OnBottomSheetShowListener mOnBottomSheetShowListener;

    private QXUIBottomSheetBehavior<QXUIBottomSheetRootLayout> behavior;
    private boolean mAnimateToCancel = false;
    private boolean mAnimateToDismiss = false;
    private boolean cancelable = true;

    public QXUIBottomSheetRootLayout getRootView() {
        return rootView;
    }

    public QXUIBottomSheetBehavior<QXUIBottomSheetRootLayout> getBehavior() {
        return behavior;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ViewCompat.requestApplyInsets(rootView);
    }

    protected void onStart() {
        super.onStart();
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void cancel() {
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToCancel = false;
            super.cancel();
        } else {
            this.mAnimateToCancel = true;
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void dismiss() {
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToDismiss = false;
            super.dismiss();
        } else {
            mAnimateToDismiss = true;
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void setOnBottomSheetShowListener(OnBottomSheetShowListener onBottomSheetShowListener) {
        this.mOnBottomSheetShowListener = onBottomSheetShowListener;
    }

    public void setRadius(int radius) {
        rootView.setRadius(radius, IQXUILayout.HIDE_RADIUS_SIDE_BOTTOM);
    }

    public void show() {
        super.show();
        if (mOnBottomSheetShowListener != null) {
            mOnBottomSheetShowListener.onShow();
        }
        if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            rootView.postOnAnimation(() -> {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
        }
        mAnimateToCancel = false;
        mAnimateToDismiss = false;
    }

    public void setContentView(View view) {
        throw new IllegalStateException("Use addContentView(View, ConstraintLayout.LayoutParams) for replacement");
    }

    public void setContentView(int layoutResId) {
        throw new IllegalStateException("Use addContentView(int) for replacement");
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new IllegalStateException("Use addContentView(View, QXUIPriorityLinearLayout.LayoutParams) for replacement");
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        throw new IllegalStateException("Use addContentView(View, QXUIPriorityLinearLayout.LayoutParams) for replacement");
    }

    public void addContentView(View view, QXUIPriorityLinearLayout.LayoutParams layoutParams) {
        rootView.addView(view, layoutParams);
    }

    public void addContentView(View view) {
        QXUIPriorityLinearLayout.LayoutParams lp = new QXUIPriorityLinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setPriority(QXUIPriorityLinearLayout.LayoutParams.PRIORITY_DISPOSABLE);
        rootView.addView(view, lp);
    }

    public void addContentView(int layoutResId) {
        LayoutInflater.from(rootView.getContext()).inflate(layoutResId, rootView, true);
    }

    public XUIBottomSheet(Context context, int style2) {
        super(context, style2);
        View container = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        rootView = container.findViewById(R.id.bottom_sheet);
        behavior = new QXUIBottomSheetBehavior();
        behavior.setHideable(cancelable);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {//1.1.0  1.2.0 addBottomSheetCallback

            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mAnimateToCancel) {
                        cancel();
                    } else if (mAnimateToDismiss) {
                        dismiss();
                    } else {
                        cancel();
                    }
                }
            }

            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
        behavior.setPeekHeight(0);
        behavior.setAllowDrag(false);
        behavior.setSkipCollapsed(true);
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        CoordinatorLayout.LayoutParams rootViewLp = (CoordinatorLayout.LayoutParams) layoutParams;
        rootViewLp.setBehavior(behavior);
        container.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_SETTLING) {
                    return;
                }
                if (cancelable && isShowing()) {
                    cancel();
                }
            }
        });
        super.setContentView((View) container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public XUIBottomSheet(Context context) {
        this(context, R.style.XUI_BottomSheet);
    }

    public static interface OnBottomSheetShowListener {
        public void onShow();
    }

    /**
     * 生成列表类型的 [XUIBottomSheet] 对话框。
     */
    public static class BottomListSheetBuilder
            extends XUIBottomSheetBaseBuilder<BottomListSheetBuilder> {
        private List<View> mContentHeaderViews;
        private List<View> mContentFooterViews;
        private XUIBottomSheetListAdapter<?> mListAdapter;
        private RecyclerView.ItemDecoration mItemDecoration;

        public BottomListSheetBuilder addHeaderView(View view) {
            return this.addContentHeaderView(view);
        }

        public BottomListSheetBuilder setContentAdapter(XUIBottomSheetListAdapter<?> adapter) {
            this.mListAdapter = adapter;
            return this;
        }

        public BottomListSheetBuilder addContentItemDecoration(RecyclerView.ItemDecoration decoration) {
            this.mItemDecoration = decoration;
            return this;
        }

        public BottomListSheetBuilder addContentHeaderView(View view) {
            if (this.mContentHeaderViews == null) {
                this.mContentHeaderViews = new ArrayList();
            }
            List<View> list = this.mContentHeaderViews;
            list.add(view);
            return this;
        }

        public BottomListSheetBuilder addContentFooterView(View view) {
            if (this.mContentFooterViews == null) {
                this.mContentFooterViews = new ArrayList();
            }
            List<View> list = this.mContentFooterViews;
            list.add(view);
            return this;
        }

        protected View onCreateContentView(XUIBottomSheet bottomSheet, QXUIBottomSheetRootLayout rootLayout, Context context) {
            RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            recyclerView.setLayoutManager(new LinearLayoutManager(context) {
                @Override
                public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                    return new RecyclerView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }
            });
            if (mItemDecoration != null) {
                recyclerView.addItemDecoration(mItemDecoration);
            }
            LinearLayout headerView = null;
            if (mContentHeaderViews != null && mContentHeaderViews.size() > 0) {
                headerView = new LinearLayout(context);
                headerView.setOrientation(LinearLayout.VERTICAL);
                for (View view : mContentHeaderViews) {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    headerView.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
            LinearLayout footerView = null;
            if (mContentFooterViews != null && mContentFooterViews.size() > 0) {
                footerView = new LinearLayout(context);
                footerView.setOrientation(LinearLayout.VERTICAL);
                for (View view : mContentFooterViews) {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    footerView.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
            recyclerView.setAdapter(mListAdapter);
            return recyclerView;
        }

        public BottomListSheetBuilder(Context context) {
            super(context);
        }
    }

}
