package com.feature.tui.widget.drag;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.feature.tui.R;

import java.util.List;

public class DragLay extends LinearLayout {

    private DragContainer qsCustomizer;

    private DragMoveView moveView;

    private View view;

    private int vecPaddingLeft, vecPaddingTop, vecPaddingRight, vecPaddingBottom;

    public DragLay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.drag_container, null);
        qsCustomizer = view.findViewById(R.id.qs_customizer);
        moveView = view.findViewById(R.id.view_move);
        qsCustomizer.setMoveView(moveView);
        addView(view);
        vecPaddingLeft = view.getPaddingLeft();
        vecPaddingTop = view.getPaddingTop();
        vecPaddingRight = view.getPaddingRight();
        vecPaddingBottom = view.getPaddingBottom();
        setPaddingForCfgChange();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setPaddingForCfgChange();
    }

    private void setPaddingForCfgChange() {
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view.setPadding(120, 0, 120, 0);
        } else {
            view.setPadding(vecPaddingLeft, vecPaddingTop, vecPaddingRight, vecPaddingBottom);
        }
    }

    public void setCallBack(DragMoveView.DataChangeCallback dataChangeCallback) {
        moveView.setDataChangeCallBack(dataChangeCallback);
    }

    public void initData(List<DragBean> dataSourceTop, List<DragBean> dataSourceBottom) {
        qsCustomizer.initData(dataSourceTop, dataSourceBottom);
    }

}
