package com.feature.tui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.feature.tui.R;

import java.util.ArrayList;

public class QXUIPriorityLinearLayout extends QXUILinearLayout {
    private ArrayList<View> mTempMiniWidthChildList = new ArrayList<>();
    private ArrayList<View> mTempDisposableChildList = new ArrayList<>();

    public QXUIPriorityLinearLayout(Context context) {
        super(context);
    }

    public QXUIPriorityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            handleHorizontal(widthMeasureSpec, heightMeasureSpec);
        } else {
            handleVertical(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void handleHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int visibleChildCount = getVisibleChildCount();
        if (widthMode == View.MeasureSpec.UNSPECIFIED || visibleChildCount == 0 || widthSize <= 0) {
            return;
        }
        int usedWidth = handlePriorityIncompressible(widthMeasureSpec, heightMeasureSpec);
        if (usedWidth >= widthSize) {
            for (View view : mTempMiniWidthChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                view.measure(View.MeasureSpec.makeMeasureSpec(lp.miniContentProtectionSize, View.MeasureSpec.AT_MOST), heightMeasureSpec);
                lp.width = view.getMeasuredWidth();
            }
            for (View view : mTempDisposableChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                lp.width = 0;
                lp.leftMargin = 0;
                lp.rightMargin = 0;
            }
        } else {
            int usefulWidth = widthSize - usedWidth;
            int miniNeedWidth = 0, miniWidthChildTotalWidth = 0, marginHor;
            for (View view : mTempMiniWidthChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                view.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.AT_MOST), heightMeasureSpec);
                marginHor = lp.leftMargin + lp.rightMargin;
                miniWidthChildTotalWidth += view.getMeasuredWidth() + marginHor;
                miniNeedWidth += Math.min(view.getMeasuredWidth(), lp.miniContentProtectionSize) + marginHor;
            }
            if (miniNeedWidth >= usefulWidth) {
                for (View view : mTempMiniWidthChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.width = Math.min(view.getMeasuredWidth(), lp.miniContentProtectionSize);
                }
                for (View view : mTempDisposableChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.width = 0;
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                }
            } else if (miniWidthChildTotalWidth < usefulWidth) {
                // there is a space for disposableChildList
                if (!mTempDisposableChildList.isEmpty()) {
                    dispatchSpaceToDisposableChildList(mTempDisposableChildList, widthMeasureSpec, heightMeasureSpec,
                            usefulWidth - miniWidthChildTotalWidth);
                }
            } else {
                // no space for disposableChild
                for (View view : mTempDisposableChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.width = 0;
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                }
                if (usefulWidth < miniWidthChildTotalWidth && !mTempMiniWidthChildList.isEmpty()) {
                    dispatchSpaceToMiniWidthChildList(mTempMiniWidthChildList, usefulWidth, miniWidthChildTotalWidth);
                }
            }
        }
    }

    private void handleVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int visibleChildCount = getVisibleChildCount();
        if (heightMode == View.MeasureSpec.UNSPECIFIED || visibleChildCount == 0 || heightSize <= 0) {
            return;
        }
        int usedHeight = handlePriorityIncompressible(widthMeasureSpec, heightMeasureSpec);
        if (usedHeight >= heightSize) {
            for (View view : mTempMiniWidthChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                view.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(lp.miniContentProtectionSize, View.MeasureSpec.AT_MOST));
                lp.height = view.getMeasuredHeight();
            }
            for (View view : mTempDisposableChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                lp.height = 0;
                lp.topMargin = 0;
                lp.bottomMargin = 0;
            }
        } else {
            int usefulSpace = heightSize - usedHeight;
            int miniNeedSpace = 0, miniSizeChildTotalLength = 0, marginVer;
            for (View view : mTempMiniWidthChildList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                view.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST));
                marginVer = lp.topMargin + lp.bottomMargin;
                miniSizeChildTotalLength += view.getMeasuredHeight() + marginVer;
                miniNeedSpace += Math.min(view.getMeasuredHeight(), lp.miniContentProtectionSize) + marginVer;
            }
            if (miniNeedSpace >= usefulSpace) {
                for (View view : mTempMiniWidthChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.height = Math.min(view.getMeasuredHeight(), lp.miniContentProtectionSize);
                }
                for (View view : mTempDisposableChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.height = 0;
                    lp.topMargin = 0;
                    lp.bottomMargin = 0;
                }
            } else if (miniSizeChildTotalLength < usefulSpace) {
                // there is a space for disposableChildList
                if (!mTempDisposableChildList.isEmpty()) {
                    dispatchSpaceToDisposableChildList(mTempDisposableChildList, widthMeasureSpec, heightMeasureSpec,
                            usefulSpace - miniSizeChildTotalLength);
                }
            } else {
                // no space for disposableChild
                for (View view : mTempDisposableChildList) {
                    LayoutParams lp = (LayoutParams) view.getLayoutParams();
                    lp.height = 0;
                    lp.topMargin = 0;
                    lp.bottomMargin = 0;
                }
                if (usefulSpace < miniSizeChildTotalLength && !mTempMiniWidthChildList.isEmpty()) {
                    dispatchSpaceToMiniWidthChildList(mTempMiniWidthChildList, usefulSpace, miniSizeChildTotalLength);
                }
            }
        }
    }

    private int handlePriorityIncompressible(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int usedSize = 0;
        mTempMiniWidthChildList.clear();
        mTempDisposableChildList.clear();
        int orientation = getOrientation();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.backupOrRestore();
            int priority = lp.getPriority(orientation);
            int margin = orientation == HORIZONTAL ? lp.leftMargin + lp.rightMargin :
                    lp.topMargin + lp.bottomMargin;
            if (priority == LayoutParams.PRIORITY_INCOMPRESSIBLE) {
                if (orientation == HORIZONTAL) {
                    if (lp.width >= 0) {
                        usedSize += lp.width + margin;
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.AT_MOST), heightMeasureSpec);
                        usedSize += child.getMeasuredWidth() + margin;
                    }
                } else {
                    if (lp.height >= 0) {
                        usedSize += lp.height + margin;
                    } else {
                        child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.AT_MOST));
                        usedSize += child.getMeasuredHeight() + margin;
                    }
                }
            } else if (priority == LayoutParams.PRIORITY_MINI_CONTENT_PROTECTION) {
                mTempMiniWidthChildList.add(child);
            } else {
                if (lp.weight == 0) {
                    mTempDisposableChildList.add(child);
                }
            }
        }
        return usedSize;
    }

    protected void dispatchSpaceToDisposableChildList(ArrayList<View> childList, int widthMeasureSpec, int heightMeasureSpec, int usefulSpace) {

        for (View view : childList) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            if (getOrientation() == HORIZONTAL) {
                if (usefulSpace <= 0) {
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                    lp.width = 0;
                }
                usefulSpace -= lp.leftMargin - lp.rightMargin;
                if (usefulSpace > 0) {
                    view.measure(
                            View.MeasureSpec.makeMeasureSpec(usefulSpace, View.MeasureSpec.AT_MOST),
                            getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), lp.height));
                    if (view.getMeasuredWidth() >= usefulSpace) {
                        lp.width = usefulSpace;
                        usefulSpace = 0;
                    } else {
                        usefulSpace -= view.getMeasuredWidth();
                    }
                } else {
                    lp.leftMargin = 0;
                    lp.rightMargin = 0;
                    lp.width = 0;
                }
            } else {
                if (usefulSpace <= 0) {
                    lp.topMargin = 0;
                    lp.bottomMargin = 0;
                    lp.height = 0;
                }
                usefulSpace -= lp.topMargin - lp.bottomMargin;
                if (usefulSpace > 0) {
                    view.measure(
                            getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width),
                            View.MeasureSpec.makeMeasureSpec(usefulSpace, View.MeasureSpec.AT_MOST));
                    if (view.getMeasuredHeight() >= usefulSpace) {
                        lp.height = usefulSpace;
                        usefulSpace = 0;
                    } else {
                        usefulSpace -= view.getMeasuredHeight();
                    }
                } else {
                    lp.topMargin = 0;
                    lp.bottomMargin = 0;
                    lp.height = 0;
                }

            }
        }
    }

    protected void dispatchSpaceToMiniWidthChildList(ArrayList<View> childList, int usefulSpace,
                                                     int calculateTotalLength) {
        int extra = calculateTotalLength - usefulSpace;
        if (extra > 0) {
            for (View view : childList) {
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (getOrientation() == HORIZONTAL) {
                    float radio = (view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin)
                            * 1f / calculateTotalLength;
                    int width = (int) (view.getMeasuredWidth() - extra * radio);
                    lp.width = Math.max(0, width);
                } else {
                    float radio = (view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin)
                            * 1f / calculateTotalLength;
                    int height = (int) (view.getMeasuredHeight() - extra * radio);
                    lp.height = Math.max(0, height);
                }
            }
        }
    }

    private int getVisibleChildCount() {
        int childCount = getChildCount();
        int visibleChildCount = 0;
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).getVisibility() == VISIBLE) {
                visibleChildCount++;
            }
        }
        return visibleChildCount;
    }

    @Override
    protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public static final int PRIORITY_DISPOSABLE = 1;
        public static final int PRIORITY_MINI_CONTENT_PROTECTION = 2;
        public static final int PRIORITY_INCOMPRESSIBLE = 3;

        private int priority = PRIORITY_MINI_CONTENT_PROTECTION;
        private int miniContentProtectionSize = 0;

        private int backupWidth = Integer.MIN_VALUE;
        private int backupHeight = Integer.MIN_VALUE;
        private int backupLeftMargin = 0;
        private int backupRightMargin = 0;
        private int backupTopMargin = 0;
        private int backupBottomMargin = 0;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.QXUIPriorityLinearLayout_Layout);
            priority = a.getInteger(R.styleable.QXUIPriorityLinearLayout_Layout_qxui_layout_priority,
                    PRIORITY_MINI_CONTENT_PROTECTION);
            miniContentProtectionSize = a.getDimensionPixelSize(
                    R.styleable.QXUIPriorityLinearLayout_Layout_qxui_layout_miniContentProtectionSize,
                    0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void setMiniContentProtectionSize(int miniContentProtectionSize) {
            this.miniContentProtectionSize = miniContentProtectionSize;
        }

        public int getPriority(int orientation) {
            if (weight > 0) {
                return PRIORITY_DISPOSABLE;
            }
            if (orientation == LinearLayout.HORIZONTAL) {
                if (width >= 0) {
                    return PRIORITY_INCOMPRESSIBLE;
                }
            } else {
                if (height >= 0) {
                    return PRIORITY_INCOMPRESSIBLE;
                }
            }
            return priority;
        }

        void backupOrRestore() {
            if (backupWidth == Integer.MIN_VALUE) {
                backupWidth = width;
                backupLeftMargin = leftMargin;
                backupRightMargin = rightMargin;
            } else {
                width = backupWidth;
                leftMargin = backupLeftMargin;
                rightMargin = backupRightMargin;
            }
            if (backupHeight == Integer.MIN_VALUE) {
                backupHeight = height;
                backupTopMargin = topMargin;
                backupBottomMargin = bottomMargin;
            } else {
                height = backupHeight;
                topMargin = backupTopMargin;
                bottomMargin = backupBottomMargin;
            }
        }
    }
}
