package com.feature.tui.widget.pickerview.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.configure.PickerOptions;

import java.util.List;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:
 */
public class OptionsPickerView {

    private WheelOptions wheelOptions;
    private PickerOptions mPickerOptions;
    private LinearLayout mOptionsPickerView;


    public OptionsPickerView(PickerOptions pickerOptions) {
        mPickerOptions = pickerOptions;
        initView(pickerOptions.getContext());
    }

    private void initView(Context context) {
        if (mPickerOptions.layoutRes == 0) {
            mPickerOptions.layoutRes = R.layout.pickerview_options;
        }
        mOptionsPickerView = (LinearLayout) LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, null);
        if (mPickerOptions.bgColorWheel != 0) {
            mOptionsPickerView.setBackgroundColor(mPickerOptions.bgColorWheel);
        }
        wheelOptions = new WheelOptions(mOptionsPickerView, mPickerOptions.isRestoreItem);
        if (mPickerOptions.optionsSelectChangeListener != null) {
            wheelOptions.setOptionsSelectChangeListener(mPickerOptions.optionsSelectChangeListener);
        }


        wheelOptions.setGravity(mPickerOptions.textGravity);
        wheelOptions.setLabels(mPickerOptions.label1, mPickerOptions.label2, mPickerOptions.label3);
        wheelOptions.setTextXOffset(mPickerOptions.xOffsetOne, mPickerOptions.xOffsetTwo, mPickerOptions.xOffsetThree);
        wheelOptions.setItemsVisible(mPickerOptions.itemsVisibleCount);
        wheelOptions.setCyclic(mPickerOptions.cyclic1, mPickerOptions.cyclic2, mPickerOptions.cyclic3);
        wheelOptions.setDividerColor(mPickerOptions.dividerColor);
        wheelOptions.setDividerWidth(mPickerOptions.dividerWidth);
        wheelOptions.hasDivider(mPickerOptions.hasDivider);
        wheelOptions.setItemHeight(mPickerOptions.itemHeight);
        wheelOptions.setTextSize(mPickerOptions.textSizeOut, mPickerOptions.textSizeCenter);
        wheelOptions.setTextColor(mPickerOptions.textColorOut, mPickerOptions.textColorCenter);
        wheelOptions.isCenterLabel(mPickerOptions.isCenterLabel);
        wheelOptions.setLabelPadding(mPickerOptions.labelPadding);
        wheelOptions.setLabelTextSize(mPickerOptions.textSizeLabel);
        wheelOptions.setSlidingCoefficient(mPickerOptions.slidingCoefficient);
        wheelOptions.setTypeface(mPickerOptions.font);

        setPicker(mPickerOptions.optionsItems);
    }


    /**
     * 联动情况下调用
     *
     * @param optionsItems 设置数据源
     */
    private <T> void setPicker(List<T> optionsItems) {

        wheelOptions.setPicker(optionsItems);
        reSetCurrentItems();
    }


    /**
     * 不联动情况下调用
     *
     * @param options1Items
     * @param options2Items
     * @param options3Items
     */
    private <T> void setNPicker(List<T> options1Items,
                                List<T> options2Items,
                                List<T> options3Items) {

        wheelOptions.setLinkage(false);
        wheelOptions.setNPicker(options1Items, options2Items, options3Items);
        reSetCurrentItems();
    }

    private void reSetCurrentItems() {
        if (wheelOptions != null) {
            wheelOptions.setCurrentItems(mPickerOptions.option1, mPickerOptions.option2, mPickerOptions.option3);
        }
    }

    /**
     * 得到选中的item下标
     *
     * @return int[3]
     */
    public int[] returnData() {
        return wheelOptions.getCurrentItems();
    }

    /**
     * 得到选择器View
     *
     * @return
     */
    public View getContentView() {
        return mOptionsPickerView;
    }

}
