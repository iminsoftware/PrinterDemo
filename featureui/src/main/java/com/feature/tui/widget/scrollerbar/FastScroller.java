package com.feature.tui.widget.scrollerbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.util.XUiDisplayHelper;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FastScroller extends RecyclerView.ItemDecoration implements RecyclerView.OnItemTouchListener {

    private final FastScrollPopup mPopup;

    @IntDef({STATE_HIDDEN, STATE_VISIBLE, STATE_DRAGGING})
    @Retention(SOURCE)
    private @interface State {
    }

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_VISIBLE = 1;
    private static final int STATE_DRAGGING = 2;

    @IntDef({DRAG_Y, DRAG_NONE})
    @Retention(SOURCE)
    private @interface DragState {
    }

    private static final int DRAG_NONE = 0;

    private static final int DRAG_Y = 2;

    @IntDef({ANIMATION_STATE_OUT, ANIMATION_STATE_FADING_IN, ANIMATION_STATE_IN,
            ANIMATION_STATE_FADING_OUT})
    @Retention(SOURCE)
    private @interface AnimationState {
    }

    private static final int ANIMATION_STATE_OUT = 0;
    private static final int ANIMATION_STATE_FADING_IN = 1;
    private static final int ANIMATION_STATE_IN = 2;
    private static final int ANIMATION_STATE_FADING_OUT = 4;

    private static final int SHOW_DURATION_MS = 500;
    private static final int HIDE_DELAY_AFTER_VISIBLE_MS = 1500;
    private static final int HIDE_DELAY_AFTER_DRAGGING_MS = 1200;
    private static final int HIDE_DURATION_MS = 500;
    private static final int SCROLLBAR_FULL_OPAQUE = 255;

    private static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed};
    private static final int[] EMPTY_STATE_SET = new int[]{};

    private final int mScrollbarMinimumRange;
    private final int mMargin;

    final Drawable mVerticalThumbDrawable;

    final Drawable mVerticalTrackDrawable;
    private final int mVerticalThumbWidth;

    int mVerticalThumbHeight;

    int mVerticalThumbCenterY;

    float mVerticalDragY;

    private int mRecyclerViewWidth = 0;
    private int mRecyclerViewHeight = 0;
    private int mMarginRight = 0;

    private RecyclerView mRecyclerView;
    private boolean mNeedVerticalScrollbar = false;

    @State
    private int mState = STATE_HIDDEN;
    @DragState
    private int mDragState = DRAG_NONE;

    private final int[] mVerticalRange = new int[2];
    private final SectionedFastScrollImp mSectionedFastScrollImp;
    final ValueAnimator mShowHideAnimator = ValueAnimator.ofFloat(0, 1);

    private int downX;

    @AnimationState
    int mAnimationState = ANIMATION_STATE_OUT;
    private final Runnable mHideRunnable = () -> hide();
    public int mScrollY = 0;
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mScrollY += dy;
            updateScrollPosition(mScrollY);
        }
    };

    public FastScroller(RecyclerView recyclerView, SectionedFastScrollImp sectionedFastScrollImp, Drawable verticalThumbDrawable,
                        Drawable verticalTrackDrawable,
                        int defaultWidth, int scrollbarMinimumRange,
                        int margin, int marginRight) {
        mVerticalThumbDrawable = verticalThumbDrawable;
        mVerticalTrackDrawable = verticalTrackDrawable;
        mSectionedFastScrollImp = sectionedFastScrollImp;
        mVerticalThumbWidth = Math.max(defaultWidth, verticalThumbDrawable.getIntrinsicWidth());
        mVerticalThumbHeight = verticalThumbDrawable.getIntrinsicHeight();
        mScrollbarMinimumRange = scrollbarMinimumRange;
        mMarginRight = marginRight;
        mMargin = margin;
        mVerticalThumbDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);
        mVerticalTrackDrawable.setAlpha(SCROLLBAR_FULL_OPAQUE);

        mShowHideAnimator.addListener(new AnimatorListener());
        mShowHideAnimator.addUpdateListener(new AnimatorUpdater());
        mPopup = new FastScrollPopup(recyclerView.getContext());
        attachToRecyclerView(recyclerView);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
        }
    }

    private void setupCallbacks() {
        mRecyclerView.addItemDecoration(this);
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void destroyCallbacks() {
        mRecyclerView.removeItemDecoration(this);
        mRecyclerView.removeOnItemTouchListener(this);
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        cancelHide();
    }

    void requestRedraw() {
        mRecyclerView.invalidate();
    }

    void setState(@State int state) {
        if (state == STATE_DRAGGING && mState != STATE_DRAGGING) {
            mVerticalThumbDrawable.setState(PRESSED_STATE_SET);
            cancelHide();
        }

        if (state == STATE_HIDDEN) {
            requestRedraw();
        } else {
            show();
        }

        if (mState == STATE_DRAGGING && state != STATE_DRAGGING) {
            mVerticalThumbDrawable.setState(EMPTY_STATE_SET);
            resetHideDelay(HIDE_DELAY_AFTER_DRAGGING_MS);
        } else if (state == STATE_VISIBLE) {
            resetHideDelay(HIDE_DELAY_AFTER_VISIBLE_MS);
        }
        mState = state;
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(mRecyclerView) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public boolean isDragging() {
        return mState == STATE_DRAGGING;
    }

    boolean isVisible() {
        return mState == STATE_VISIBLE;
    }

    public void show() {
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_OUT:
                mShowHideAnimator.cancel();
                // fall through
            case ANIMATION_STATE_OUT:
                mAnimationState = ANIMATION_STATE_FADING_IN;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 1);
                mShowHideAnimator.setDuration(SHOW_DURATION_MS);
                mShowHideAnimator.setStartDelay(0);
                mShowHideAnimator.start();
                break;
        }
    }


    void hide() {
        switch (mAnimationState) {
            case ANIMATION_STATE_FADING_IN:
                mShowHideAnimator.cancel();
                // fall through
            case ANIMATION_STATE_IN:
                mAnimationState = ANIMATION_STATE_FADING_OUT;
                mShowHideAnimator.setFloatValues((float) mShowHideAnimator.getAnimatedValue(), 0);
                mShowHideAnimator.setDuration(FastScroller.HIDE_DURATION_MS);
                mShowHideAnimator.start();
                break;
        }
    }

    private void cancelHide() {
        mRecyclerView.removeCallbacks(mHideRunnable);
    }

    private void resetHideDelay(int delay) {
        cancelHide();
        mRecyclerView.postDelayed(mHideRunnable, delay);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (mRecyclerViewWidth != mRecyclerView.getWidth()
                || mRecyclerViewHeight != mRecyclerView.getHeight()) {
            mRecyclerViewWidth = mRecyclerView.getWidth();
            mRecyclerViewHeight = mRecyclerView.getHeight();
            // This is due to the different events ordering when keyboard is opened or
            // retracted vs rotate. Hence to avoid corner cases we just disable the
            // scroller when size changed, and wait until the scroll position is recomputed
            // before showing it back.
            setState(STATE_HIDDEN);
            return;
        }

        if (mAnimationState != ANIMATION_STATE_OUT) {
            if (mNeedVerticalScrollbar) {
                drawVerticalScrollbar(canvas);
            }
        }
    }

    private void drawVerticalScrollbar(Canvas canvas) {
        int left = getThumbLeft();
        int top = mVerticalThumbCenterY - mVerticalThumbHeight / 2;
        mVerticalThumbDrawable.setBounds(0, 0, mVerticalThumbWidth, mVerticalThumbHeight);
        if (top < 0) {
            top = 0;
        }

        if (isLayoutRTL()) {
//            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(mVerticalThumbWidth, top);
            canvas.scale(-1, 1);
            mVerticalThumbDrawable.draw(canvas);
            canvas.scale(1, 1);
            canvas.translate(-mVerticalThumbWidth, -top);
        } else {
            canvas.translate(left, 0);
//            mVerticalTrackDrawable.draw(canvas);
            canvas.translate(0, top);
            mVerticalThumbDrawable.draw(canvas);
            canvas.translate(-left, -top);
        }
        mPopup.draw(canvas, top + XUiDisplayHelper.dp2px(mRecyclerView.getContext(), 3), left);
    }


    /**
     * Notify the scroller of external change of the scroll, e.g. through dragging or flinging on
     * the view itself.
     *
     * @param offsetY The new scroll Y offset.
     */
    void updateScrollPosition(int offsetY) {
        int verticalContentLength = mSectionedFastScrollImp.computeVerticalScrollRange();
        int verticalVisibleLength = mRecyclerViewHeight;
        mNeedVerticalScrollbar = verticalContentLength - verticalVisibleLength > 0 && mRecyclerViewHeight >= mScrollbarMinimumRange;

        if (!mNeedVerticalScrollbar) {
            if (mState != STATE_HIDDEN) {
                setState(STATE_HIDDEN);
            }
            return;
        }

        if (mNeedVerticalScrollbar) {
            int tackHeight = (int) (verticalVisibleLength + mVerticalThumbHeight * 0.7);
            mVerticalThumbCenterY = tackHeight * offsetY / (verticalContentLength - verticalVisibleLength);

        }

        if (mState == STATE_HIDDEN || mState == STATE_VISIBLE) {
            setState(STATE_VISIBLE);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView,
                                         @NonNull MotionEvent ev) {
        final boolean handled;
        if (mState == STATE_VISIBLE) {
            boolean insideVerticalThumb = isPointInsideVerticalThumb(ev.getX(), ev.getY());
            if (ev.getAction() == MotionEvent.ACTION_DOWN
                    && (insideVerticalThumb)) {
                if (insideVerticalThumb) {
                    mDragState = DRAG_Y;
                    mVerticalDragY = (int) ev.getY();
                }

                setState(STATE_DRAGGING);
                handled = true;
            } else {
                handled = false;
            }
        } else if (mState == STATE_DRAGGING) {
            handled = true;
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent me) {
        if (mState == STATE_HIDDEN) {
            return;
        }
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) me.getY();
            makeOnShoot();
            boolean insideVerticalThumb = isPointInsideVerticalThumb(me.getX(), me.getY());
            if (insideVerticalThumb) {
                if (insideVerticalThumb) {
                    mDragState = DRAG_Y;
                    mVerticalDragY = (int) me.getY();
                }
                setState(STATE_DRAGGING);
            }
            mPopup.animateVisibility(false);
        } else if (me.getAction() == MotionEvent.ACTION_UP && mState == STATE_DRAGGING) {
            mVerticalDragY = 0;
            setState(STATE_VISIBLE);
            mDragState = DRAG_NONE;
            mPopup.animateVisibility(false);
        } else if (me.getAction() == MotionEvent.ACTION_MOVE && mState == STATE_DRAGGING) {
            show();
            mPopup.animateVisibility(true);
            if (mDragState == DRAG_Y && Math.abs(me.getY() - downX) > 5) {
                verticalScrollTo(me.getY());
            }
            int offsetY = mScrollY + mVerticalThumbCenterY - mVerticalThumbHeight / 2;
            String sectionName = mSectionedFastScrollImp.getSectionName(offsetY);
//            sectionName = sectionName.substring(0, sectionName.indexOf("月") + 1);
            mPopup.setSectionName(sectionName);
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public void verticalScrollTo(float y) {
        final int[] scrollbarRange = getVerticalRange();
        y = Math.max(scrollbarRange[0], Math.min(scrollbarRange[1], y));
        if (Math.abs(mVerticalThumbCenterY - y) < 2) {
            return;
        }
        int conHeight = mSectionedFastScrollImp.computeVerticalScrollRange() - mRecyclerViewHeight;
        int diff = (int) (y * conHeight / mRecyclerViewHeight);
        int scrollingBy = diff - mScrollY;
        if (scrollingBy != 0) {
            mRecyclerView.scrollBy(0, scrollingBy);
        }
        mVerticalDragY = y;
    }

    boolean isPointInsideVerticalThumb(float x, float y) {
        return (isLayoutRTL() ? x <= mVerticalThumbWidth / 2
                : x >= getThumbLeft())
                && y >= mVerticalThumbCenterY - mVerticalThumbHeight / 2
                && y <= mVerticalThumbCenterY + mVerticalThumbHeight / 2;
    }

    private int getThumbLeft() {
        return mRecyclerViewWidth - mVerticalThumbWidth - mMarginRight;
    }

    /**
     * Gets the (min, max) vertical positions of the vertical scroll bar.
     */
    private int[] getVerticalRange() {
        mVerticalRange[0] = mMargin;
        mVerticalRange[1] = mRecyclerViewHeight - mMargin;
        return mVerticalRange;
    }

    private class AnimatorListener extends AnimatorListenerAdapter {

        private boolean mCanceled = false;

        AnimatorListener() {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            // Cancel is always followed by a new directive, so don't update state.
            if (mCanceled) {
                mCanceled = false;
                return;
            }
            if ((float) mShowHideAnimator.getAnimatedValue() == 0) {
                mAnimationState = ANIMATION_STATE_OUT;
                setState(STATE_HIDDEN);
            } else {
                mAnimationState = ANIMATION_STATE_IN;
                requestRedraw();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceled = true;
        }
    }

    private class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
        AnimatorUpdater() {

        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int alpha = (int) (SCROLLBAR_FULL_OPAQUE * ((float) valueAnimator.getAnimatedValue()));
            mVerticalThumbDrawable.setAlpha(alpha);
            mVerticalTrackDrawable.setAlpha(alpha);
            requestRedraw();
        }
    }

    public interface SectionedFastScrollImp {
        public String getSectionName(int offsetY);

        public int computeVerticalScrollRange();
    }

    public void makeOnShoot() {
        Vibrator vibrator = (Vibrator) mRecyclerView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(50, 200));
    }

}
