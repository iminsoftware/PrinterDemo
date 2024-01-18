package com.feature.tui.dialog.sheet;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class QXUIBottomSheetBehavior<V extends ViewGroup> extends BottomSheetBehavior<V> {
    private boolean mAllowDrag = true;
    private boolean mMotionEventCanDrag = true;
    private DownDragDecisionMaker mDownDragDecisionMaker;

    public void setAllowDrag(boolean allowDrag) {
        mAllowDrag = allowDrag;
    }

    public void setDownDragDecisionMaker(DownDragDecisionMaker downDragDecisionMaker) {
        mDownDragDecisionMaker = downDragDecisionMaker;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent,
                                @NonNull V child,
                                @NonNull MotionEvent event) {
        if(!mAllowDrag){
            return false;
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mMotionEventCanDrag = mDownDragDecisionMaker == null ||
                    mDownDragDecisionMaker.canDrag(parent, child, event);
        }

        if(!mMotionEventCanDrag){
            return false;
        }

        return super.onTouchEvent(parent, child, event);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent,
                                         @NonNull V child,
                                         @NonNull MotionEvent event) {
        if(!mAllowDrag){
            return false;
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            mMotionEventCanDrag = mDownDragDecisionMaker == null ||
                    mDownDragDecisionMaker.canDrag(parent, child, event);
        }
        if(!mMotionEventCanDrag){
            return false;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull V child,
                                       @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        if(!mAllowDrag){
            return false;
        }
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }


    public interface DownDragDecisionMaker {
        boolean canDrag(@NonNull CoordinatorLayout parent,
                        @NonNull View child,
                        @NonNull MotionEvent event);
    }
}
