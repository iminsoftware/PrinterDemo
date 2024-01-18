package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.widget.pickerview.listener.OnOptionsSelectChangeListener;
import com.feature.tui.widget.pickerview.view.OptionsPickerView;

import java.util.List;


public class OptionsPickerBuilder
        extends BasePickerBuilder<OptionsPickerBuilder> {

    private OptionsPickerView optionsPickerView;


    public OptionsPickerView getOptionsPickerView() {
        return this.optionsPickerView;
    }

    public void setOptionsPickerView(OptionsPickerView optionsPickerView) {
        this.optionsPickerView = optionsPickerView;
    }


    public OptionsPickerBuilder setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        getMPickerOptions().cyclic1 = cyclic1;
        getMPickerOptions().cyclic2 = cyclic2;
        getMPickerOptions().cyclic3 = cyclic3;
        return this;
    }


    public OptionsPickerBuilder setTextXOffset(int xOffsetOne, int xOffsetTwo, int xOffsetThree) {
        getMPickerOptions().xOffsetOne = xOffsetOne;
        getMPickerOptions().xOffsetTwo = xOffsetTwo;
        getMPickerOptions().xOffsetThree = xOffsetThree;
        return this;
    }


    public OptionsPickerBuilder setLabels(String label1, String label2, String label3) {
        getMPickerOptions().label1 = label1;
        getMPickerOptions().label2 = label2;
        getMPickerOptions().label3 = label3;
        return this;
    }


    public OptionsPickerBuilder setOptionsSelectChangeListener(OnOptionsSelectChangeListener listener) {
        getMPickerOptions().optionsSelectChangeListener = listener;
        return this;
    }


    public OptionsPickerBuilder isRestoreItem(boolean isRestoreItem) {
        getMPickerOptions().isRestoreItem = isRestoreItem;
        return this;
    }


    public OptionsPickerBuilder setSelectOptions(int option1, int option2, int option3) {
        getMPickerOptions().option1 = option1;
        getMPickerOptions().option2 = option2;
        getMPickerOptions().option3 = option3;
        return this;
    }


    public <T> OptionsPickerBuilder setPicker(List<? extends T> optionsItems) {
        getMPickerOptions().optionsItems = optionsItems;
        return this;
    }


    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        OptionsPickerView optionsPickerView = this.optionsPickerView = new OptionsPickerView(getMPickerOptions());
        return optionsPickerView != null ? optionsPickerView.getContentView() : null;
    }

    public OptionsPickerBuilder(Context context) {
        super(context);
    }
}
