package com.feature.tui.popup;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R.dimen;
import com.feature.tui.R.drawable;
import com.feature.tui.R.style;
import com.feature.tui.util.XUiDisplayHelper;

import java.lang.annotation.RetentionPolicy;

public class NormalPopup extends BasePopup {

    private static final String TAG = "NormalPopup";
    private FrameLayout decorRootView;
    private int mPaddingTop = 10;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingBottom;
    private int mOffsetX = 25;
    private int mOffsetY;
    private int mPreferredDirection;
    private int mAnimDirection;
    private View mContentView;
    private int mInitWidth;
    private int mInitHeight;
    public static final int AUTO = 0;
    public static final int FROM_LEFT = 1;
    public static final int FROM_RIGHT = 2;
    public static final int FROM_CENTER = 3;
    public static final int DIRECTION_TOP = 0;
    public static final int DIRECTION_BOTTOM = 1;
    public static final int DIRECTION_CENTER_IN_SCREEN = 2;

    public NormalPopup(Context context, int initWidth, int initHeight) {
        super(context);
        mInitWidth = initWidth;
        mInitHeight = initHeight;
        mPreferredDirection = 1;
    }

    public NormalPopup setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
        return this;
    }

    public NormalPopup offsetX(int offsetX) {
        mOffsetX = offsetX;
        return this;
    }

    public NormalPopup offsetY(int y) {
        mOffsetY = y;
        return this;
    }

    public NormalPopup preferredDirection(@DirectionVertical int preferredDirection) {
        mPreferredDirection = preferredDirection;
        return this;
    }

    public NormalPopup animDirection(@DirectionHorizontal int animDirection) {
        mAnimDirection = animDirection;
        return this;
    }

    public NormalPopup view(@Nullable View contentView) {
        mContentView = contentView;
        return this;
    }

    public NormalPopup view(@LayoutRes int contentViewResId) {
        return view(LayoutInflater.from(mContext).inflate(contentViewResId, (ViewGroup) null));
    }

    public NormalPopup show(@NonNull View anchor) {
        if (mContentView == null) {
            throw new RuntimeException("you should call view() to set your content view");
        } else {
            NormalPopup.ShowInfo showInfo = new NormalPopup.ShowInfo(anchor);
            decorateContentView();
            calculateWindowSize(showInfo);
            calculateXY(showInfo);
            setAnimationStyle(showInfo.anchorProportion(), showInfo.getDirection());
            if (mWindow != null) {
//                mWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                mWindow.setWidth(mInitWidth);
                mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            }
            showAtLocation(anchor, showInfo.getWindowX(), showInfo.getWindowY());
            return this;
        }
    }

    private void decorateContentView() {
        if (decorRootView == null)
            decorRootView = new FrameLayout(mContext);
//        decorRootView.setBackgroundResource(drawable.shadow_8dp_bg);
        decorRootView.setBackgroundResource(drawable.tui_bg_white_shape);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mInitHeight);
        decorRootView.removeView(mContentView);
        decorRootView.addView(mContentView, layoutParams);
        if (mWindow != null) {
            mWindow.setContentView(decorRootView);
        }
    }

    private void calculateXY(NormalPopup.ShowInfo showInfo) {
        if (mAnimDirection == FROM_LEFT) {
            showInfo.setX(showInfo.getAnchorLocation()[0] - XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16) + mOffsetX);
        } else if (mAnimDirection == FROM_RIGHT) {
            showInfo.setX(showInfo.getAnchorLocation()[0] + showInfo.getAnchor().getWidth() - showInfo.getWidth() - XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16) + mOffsetX);
        } else if (mAnimDirection == FROM_CENTER) {
            showInfo.setX(showInfo.getAnchorLocation()[0] + (showInfo.getAnchor().getWidth() - showInfo.getWidth()
                    - 2 * XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16)) / 2 + mOffsetX);
        } else if (showInfo.getAnchorCenter() < showInfo.getVisibleWindowFrame().left + showInfo.getVisibleWidth() / 2) {
            showInfo.setX(Math.max(mPaddingLeft + showInfo.getVisibleWindowFrame().left, showInfo.getAnchorCenter() - showInfo.getWidth() / 2 + mOffsetX));
        } else {
            showInfo.setX(Math.min(showInfo.getVisibleWindowFrame().right - mPaddingRight - showInfo.getWidth(), showInfo.getAnchorCenter() - showInfo.getWidth() / 2 + mOffsetX));
        }

        int nextDirection = DIRECTION_CENTER_IN_SCREEN;
        if (mPreferredDirection == DIRECTION_BOTTOM) {
            nextDirection = DIRECTION_TOP;
        } else if (mPreferredDirection == DIRECTION_TOP) {
            nextDirection = DIRECTION_BOTTOM;
        }

        handleDirection(showInfo, mPreferredDirection, nextDirection);
    }

    private void handleDirection(NormalPopup.ShowInfo showInfo, int currentDirection, int nextDirection) {
        if (currentDirection == DIRECTION_CENTER_IN_SCREEN) {
            showInfo.setX(showInfo.getVisibleWindowFrame().left + (showInfo.getVisibleWidth() - showInfo.getWidth()) / 2 - XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16) + mOffsetX);
            showInfo.setY(showInfo.getVisibleWindowFrame().top + (showInfo.getVisibleHeight() - showInfo.getHeight()) / 2 - XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16) + mOffsetY);
            showInfo.setDirection(DIRECTION_CENTER_IN_SCREEN);
        } else if (currentDirection == DIRECTION_TOP) {
            showInfo.setY(showInfo.getAnchorLocation()[1] - showInfo.getHeight() - 2 * XUiDisplayHelper.getDimensionPixelToId(mContext, dimen.dp_16) + mOffsetY);
            if (showInfo.getY() < mPaddingTop + showInfo.getVisibleWindowFrame().top) {
                handleDirection(showInfo, nextDirection, DIRECTION_CENTER_IN_SCREEN);
            } else {
                showInfo.setDirection(DIRECTION_TOP);
            }
        } else if (currentDirection == DIRECTION_BOTTOM) {
            showInfo.setY(showInfo.getAnchorLocation()[1] + showInfo.getAnchor().getHeight() + mOffsetY);
            if (showInfo.getY() > showInfo.getVisibleWindowFrame().bottom - mPaddingBottom - showInfo.getHeight()) {
                handleDirection(showInfo, nextDirection, DIRECTION_CENTER_IN_SCREEN);
            } else {
                showInfo.setDirection(DIRECTION_BOTTOM);
            }
        }
    }

    private int proxyWidth(int width) {
        return width;
    }

    private int proxyHeight(int height) {
        return height;
    }

    private void calculateWindowSize(NormalPopup.ShowInfo showInfo) {
        boolean needMeasureForWidth = false;
        boolean needMeasureForHeight = false;
        int maxHeight;
        if (mInitWidth > 0) {
            showInfo.setWidth(proxyWidth(mInitWidth));
            showInfo.setContentWidthMeasureSpec(MeasureSpec.makeMeasureSpec(showInfo.getWidth(), View.MeasureSpec.EXACTLY));
        } else {
            maxHeight = showInfo.getVisibleWidth() - mPaddingLeft - mPaddingRight;
            if (mInitWidth == -1) {
                showInfo.setWidth(proxyWidth(maxHeight));
                showInfo.setContentWidthMeasureSpec(MeasureSpec.makeMeasureSpec(showInfo.getWidth(), View.MeasureSpec.EXACTLY));
            } else {
                needMeasureForWidth = true;
                showInfo.setContentWidthMeasureSpec(MeasureSpec.makeMeasureSpec(proxyWidth(maxHeight), View.MeasureSpec.AT_MOST));
            }
        }

        if (mInitHeight > 0) {
            showInfo.setHeight(proxyHeight(mInitHeight));
            showInfo.setContentHeightMeasureSpec(MeasureSpec.makeMeasureSpec(showInfo.getHeight(), View.MeasureSpec.EXACTLY));
        } else {
            maxHeight = showInfo.getVisibleHeight() - mPaddingTop - mPaddingBottom;
            if (mInitHeight == -1) {
                showInfo.setHeight(proxyHeight(maxHeight));
                showInfo.setContentHeightMeasureSpec(MeasureSpec.makeMeasureSpec(showInfo.getHeight(), View.MeasureSpec.EXACTLY));
            } else {
                needMeasureForHeight = true;
                showInfo.setContentHeightMeasureSpec(MeasureSpec.makeMeasureSpec(proxyHeight(maxHeight), View.MeasureSpec.AT_MOST));
            }
        }

        if (needMeasureForWidth || needMeasureForHeight) {
            if (mContentView != null) {
                mContentView.measure(showInfo.getContentWidthMeasureSpec(), showInfo.getContentHeightMeasureSpec());
                MarginLayoutParams layoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
                if (needMeasureForWidth) {
                    showInfo.setWidth(proxyWidth(mContentView.getMeasuredWidth() + layoutParams.getMarginStart() + layoutParams.getMarginEnd()));
                }
                if (needMeasureForHeight) {
                    showInfo.setHeight(proxyHeight(mContentView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin));
                }
            }
        }

    }

    private void setAnimationStyle(float anchorProportion, int direction) {
        boolean onTop = direction == 0;
        switch (mAnimDirection) {
            case AUTO:
                if (anchorProportion <= 0.25F) {
                    if (mWindow != null) {
                        mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Left : style.Animation_PopDownMenu_Left);
                    }
                } else if (anchorProportion > 0.25F && anchorProportion < 0.75F) {
                    if (mWindow != null) {
                        mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Center : style.Animation_PopDownMenu_Center);
                    }
                } else {
                    if (mWindow != null) {
                        mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Right : style.Animation_PopDownMenu_Right);
                    }
                }
                break;
            case FROM_LEFT:
                mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Left : style.Animation_PopDownMenu_Left);
                break;
            case FROM_RIGHT:
                mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Right : style.Animation_PopDownMenu_Right);
                break;
            case FROM_CENTER:
                mWindow.setAnimationStyle(onTop ? style.Animation_PopUpMenu_Center : style.Animation_PopDownMenu_Center);
        }
    }


    @java.lang.annotation.Retention(RetentionPolicy.SOURCE)
    public @interface DirectionHorizontal {
    }

    @java.lang.annotation.Retention(RetentionPolicy.SOURCE)
    public @interface DirectionVertical {
    }

    public class ShowInfo {
        private int[] anchorRootLocation;
        private int[] anchorLocation;
        private Rect visibleWindowFrame;
        private int width;
        private int height;
        private int x;
        private int y;
        private int anchorCenter;
        private int direction;
        private int contentWidthMeasureSpec;
        private int contentHeightMeasureSpec;
        private View anchor;

        public int[] getAnchorLocation() {
            return anchorLocation;
        }

        public Rect getVisibleWindowFrame() {
            return visibleWindowFrame;
        }

        public void setVisibleWindowFrame(@NonNull Rect var1) {
            visibleWindowFrame = var1;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int var1) {
            width = var1;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int var1) {
            height = var1;
        }

        public int getX() {
            return x;
        }

        public void setX(int var1) {
            x = var1;
        }

        public int getY() {
            return y;
        }

        public void setY(int var1) {
            y = var1;
        }

        public int getAnchorCenter() {
            return anchorCenter;
        }

        public void setAnchorCenter(int var1) {
            anchorCenter = var1;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int var1) {
            direction = var1;
        }

        public int getContentWidthMeasureSpec() {
            return contentWidthMeasureSpec;
        }

        public void setContentWidthMeasureSpec(int var1) {
            contentWidthMeasureSpec = var1;
        }

        public int getContentHeightMeasureSpec() {
            return contentHeightMeasureSpec;
        }

        public void setContentHeightMeasureSpec(int var1) {
            contentHeightMeasureSpec = var1;
        }

        public float anchorProportion() {
            return (float) (anchorCenter - x) / (float) width;
        }

        public int getVisibleWidth() {
            return visibleWindowFrame.width();
        }

        public int getVisibleHeight() {
            return visibleWindowFrame.height();
        }

        public int getWindowX() {
            return x - anchorRootLocation[0];
        }

        public int getWindowY() {
            return y - anchorRootLocation[1];
        }

        public View getAnchor() {
            return anchor;
        }

        public void setAnchor(View var1) {
            anchor = var1;
        }

        public ShowInfo(View anchor) {
            super();
            this.anchor = anchor;
            anchorRootLocation = new int[2];
            anchorLocation = new int[2];
            visibleWindowFrame = new Rect();
            direction = mPreferredDirection;
            this.anchor.getRootView().getLocationOnScreen(anchorRootLocation);
            this.anchor.getLocationOnScreen(anchorLocation);
            anchorCenter = anchorLocation[0] + anchor.getWidth() / 2;
            this.anchor.getWindowVisibleDisplayFrame(visibleWindowFrame);
        }
    }
}
