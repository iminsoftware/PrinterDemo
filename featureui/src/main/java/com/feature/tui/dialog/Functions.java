package com.feature.tui.dialog;

import android.app.Dialog;
import android.view.View;

import com.feature.tui.demo.adapter.editable.BaseSwipeActionAdapter;

public class Functions {

    public interface Fun {
        void invoke(View view);
    }

    public interface Fun0 {
        void invoke();
    }

    public interface Fun1 {
        void invoke(Dialog dialog, int i);
    }

    public interface Fun2<M extends BaseSwipeActionAdapter.BaseModel> {
        void invoke(int position, M m);
    }

    public interface Fun3 {
        void invoke(boolean isChecked);
    }

    public interface Fun4<T> {
        void invoke(T item, int position);
    }
    
}
