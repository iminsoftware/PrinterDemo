package com.feature.tui.dialog.builder;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.ColorInt;

import com.feature.tui.widget.pickerview.configure.PickerOptions;

public abstract class BasePickerBuilder<T>
        extends BaseDialogBuilder<T> {

    private  PickerOptions mPickerOptions;


    protected  PickerOptions getMPickerOptions() {
        return this.mPickerOptions;
    }

    public  T setGravity(int gravity) {
        this.mPickerOptions.textGravity = gravity;
        return (T) this;
    }

    public  T setLayoutRes(int res) {
        this.mPickerOptions.layoutRes = res;
        return (T) this;
    }

    public  T setBgColor(int bgColorWheel) {
        this.mPickerOptions.bgColorWheel = bgColorWheel;
        return (T) this;
    }

    public  T setTextSize(int textSizeOut, int textSizeCenter) {
        this.mPickerOptions.textSizeOut = textSizeOut;
        this.mPickerOptions.textSizeCenter = textSizeCenter;
        return (T) this;
    }

    public  T setTextColor(@ColorInt int textColorOut, @ColorInt int textColorCenter) {
        this.mPickerOptions.textColorOut = textColorOut;
        this.mPickerOptions.textColorCenter = textColorCenter;
        return (T) this;
    }

    public  T setItemVisibleCount(int count) {
        this.mPickerOptions.itemsVisibleCount = count;
        return (T) this;
    }

    public  T setItemHeight(float itemHeight) {
        this.mPickerOptions.itemHeight = itemHeight;
        return (T) this;
    }

    public  T setDividerColor(@ColorInt int dividerColor) {
        this.mPickerOptions.dividerColor = dividerColor;
        return (T) this;
    }

    public  T setDividerWidth(int dividerWidth) {
        this.mPickerOptions.dividerWidth = dividerWidth;
        return (T) this;
    }

    public  T setDividerWidth(boolean hasDivider) {
        this.mPickerOptions.hasDivider = hasDivider;
        return (T) this;
    }

    public  T setTypeface(Typeface font) {
        this.mPickerOptions.font = font;
        return (T) this;
    }

    public  T isCenterLabel(boolean isCenterLabel) {
        this.mPickerOptions.isCenterLabel = isCenterLabel;
        return (T) this;
    }

    public  T setLabelPadding(int labelPadding) {
        this.mPickerOptions.labelPadding = labelPadding;
        return (T) this;
    }

    public  T setTextSizeLabel(int textSizeLabel) {
        this.mPickerOptions.textSizeLabel = textSizeLabel;
        return (T) this;
    }

    public  T setSlidingCoefficient(float slidingCoefficient) {
        this.mPickerOptions.slidingCoefficient = slidingCoefficient;
        return (T) this;
    }

    public BasePickerBuilder(Context context) {
        super(context);
        this.mPickerOptions = new PickerOptions(context);
    }
}
