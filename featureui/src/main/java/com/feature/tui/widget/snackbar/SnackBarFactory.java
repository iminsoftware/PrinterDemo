package com.feature.tui.widget.snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.feature.tui.R;
import com.google.android.material.snackbar.Snackbar;
/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/26 17:21
 */
public class SnackBarFactory {
    public static Snackbar make( View view,String txt, String actionText, View.OnClickListener listener, int d, boolean bottomMode) {
        ViewGroup parent = findSuitableParent(view);
        if (parent != null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            int layoutID = R.layout.snackbar_n_layout;
            View content = inflater.inflate(layoutID, parent, false);

            Snackbar snackbar = Snackbar.make(view, "", d);
            View snackbarView = snackbar.getView();
            if (snackbarView != null) {
                Snackbar.SnackbarLayout snackLayout = (Snackbar.SnackbarLayout) snackbarView;
                snackLayout.setPadding(0, 0, 0, 0);
                snackLayout.setBackgroundResource(0);
                snackLayout.addView(content, 0);
            }

            snackbar.setDuration(d);
            snackbar.setText(txt);

            if (bottomMode) {
                TextView snackbar_bottom_action = content.findViewById(R.id.snackbar_bottom_action);
                snackbar_bottom_action.setText(actionText);
                snackbar_bottom_action.setVisibility(View.VISIBLE);
                content.findViewById(R.id.snackbar_action).setVisibility(View.GONE);
            } else {
                content.findViewById(R.id.snackbar_bottom_action).setVisibility(View.GONE);
                snackbar.setAction(actionText, listener);
            }

            content.findViewById(R.id.snackbar_text).setPadding(0, 0, 0, 0);
            return snackbar;
        } else {
            try {
                throw (new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view."));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return null;
    }
    public static Snackbar make(View view, String txt) {
        return make(view, txt, null, null, 4000, false);
    }

    public static Snackbar make(View view, String txt, String actionText, View.OnClickListener listener) {
        return make(view, txt, actionText, listener, 4000, false);
    }

    public static Snackbar make(View view, String txt, String actionText, View.OnClickListener listener, int d) {
        return make(view, txt, actionText, listener, d, false);
    }

    public static Snackbar make(View view, String txt, String actionText, View.OnClickListener listener, boolean bottomMode) {
        return make(view, txt, actionText, listener, 4000, bottomMode);
    }

    private static ViewGroup findSuitableParent(View param){
        View view = param;
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (CoordinatorLayout)view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (FrameLayout)view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (FrameLayout)view;
                }
            }
            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View)parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }
}
