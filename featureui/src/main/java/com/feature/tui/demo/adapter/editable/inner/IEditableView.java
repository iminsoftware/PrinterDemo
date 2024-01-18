package com.feature.tui.demo.adapter.editable.inner;

import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

public interface IEditableView<VH extends RecyclerView.ViewHolder> {
    View getHideView( VH vh);

    CheckBox getCheckBox( VH vh);

}
