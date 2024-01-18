package com.feature.tui.demo.adapter.editable.inner;

public class Selectable {
    private boolean isSelected = true;

    public final boolean isSelected() {
        return this.isSelected;
    }

    public final void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Selectable() {

    }

    public Selectable(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
