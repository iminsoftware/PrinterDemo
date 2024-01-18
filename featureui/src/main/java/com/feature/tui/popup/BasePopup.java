package com.feature.tui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.feature.tui.dialog.Functions;

import java.lang.ref.WeakReference;


public abstract class BasePopup<T extends BasePopup> {
    public PopupWindow mWindow;
    private WeakReference mAttachedViewRf;
    private Functions.Fun0 mDismissListener;
    private boolean mDismissIfOutsideTouch;
    private OnAttachStateChangeListener mOnAttachStateChangeListener;
    @SuppressLint({"ClickableViewAccessibility"})
    private OnTouchListener mOutsideTouchDismissListener;
    public Context mContext;

    public BasePopup(@NonNull Context context) {
        super();
        mContext = context;
        mDismissIfOutsideTouch = true;
        mOnAttachStateChangeListener = new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(@NonNull View v) {
            }

            public void onViewDetachedFromWindow(@NonNull View v) {
                dismiss();
            }
        };
        mOutsideTouchDismissListener = new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 4) {
                    if (mWindow != null) {
                        mWindow.dismiss();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        mWindow = new PopupWindow(context);
        initWindow();

    }

    private void initWindow() {
        assert mWindow != null;
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setFocusable(true);
        mWindow.setTouchable(true);

        mWindow.setOnDismissListener(() -> {
            if (mDismissListener != null)
                mDismissListener.invoke();
        });
        dismissIfOutsideTouch(mDismissIfOutsideTouch);
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }

    public void dismissIfOutsideTouch(boolean dismissIfOutsideTouch) {
        mWindow.setOutsideTouchable(dismissIfOutsideTouch);
        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mWindow.isOutsideTouchable()) {
                    View mView = mWindow.getContentView();
                    if (null != mView)
                        mView.dispatchTouchEvent(event);
                }
                return mWindow.isFocusable() && !mWindow.isOutsideTouchable();
            }
        });
    }

    public BasePopup<T> setOnDismissListener(@NonNull Functions.Fun0 listener) {
        mDismissListener = listener;
        return this;
    }

    private void removeOldAttachStateChangeListener() {
        if (mAttachedViewRf != null) {
            View oldAttachedView = (View) mAttachedViewRf.get();
            if (oldAttachedView != null) {
                oldAttachedView.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
            }
        }
    }

    public View getDecorView() {
        View decorView = null;
        try {
            assert mWindow != null;
            if (mWindow.getBackground() == null) {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView = (View) mWindow.getContentView().getParent();
                } else {
                    decorView = mWindow.getContentView();
                }
            } else {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView = (View) mWindow.getContentView().getParent().getParent();
                } else {
                    decorView = (View) mWindow.getContentView().getParent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decorView;
    }

    protected void showAtLocation(View parent, int x, int y) {
        if (ViewCompat.isAttachedToWindow(parent)) {
            removeOldAttachStateChangeListener();
            parent.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
            mAttachedViewRf = new WeakReference(parent);
            if (mWindow != null) {
                mWindow.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
            }
        }
    }

    public void dismiss() {
        removeOldAttachStateChangeListener();
        mAttachedViewRf = null;
        if (mWindow != null) {
            mWindow.dismiss();
        }
    }
}
