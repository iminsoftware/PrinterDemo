package com.feature.tui.widget.pickerview.configure;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.CompoundButton;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.listener.OnOptionsSelectChangeListener;
import com.feature.tui.widget.pickerview.listener.OnTimeSelectChangeListener;

import java.util.Calendar;
import java.util.List;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/27 14:34
 */
public class PickerOptions {

    public OnTimeSelectChangeListener timeSelectChangeListener;

    public OnOptionsSelectChangeListener optionsSelectChangeListener;

    public List optionsItems;

    public String label1;

    public String label2;

    public String label3;

    public int option1;

    public int option2;

    public int option3;

    public int xOffsetOne;

    public int xOffsetTwo;

    public int xOffsetThree;

    public boolean cyclic1;

    public boolean cyclic2;

    public boolean cyclic3;
    /**
     * 切换时，还原第一项
     */
    public boolean isRestoreItem = false;

    /**
     * 显示类型，默认显示： 年月日时分秒
     */
    public boolean[] type = {true, true, true, false, false, false};

    /**
     * 当前选中时间
     */
    public Calendar date;

    /**
     * 开始时间
     */
    public Calendar startDate;

    /**
     * 终止时间
     */
    public Calendar endDate;

    /**
     * 开始年份
     */
    public int startYear;

    /**
     * 结尾年份
     */
    public int endYear;

    /**
     * 是否循环
     */
    public boolean cyclic;

    /**
     * 是否显示农历
     */
    public boolean isLunarCalendar;

    public String labelYear;

    public String labelMonth;

    public String labelDay;

    public String labelHours;

    public String labelMinutes;


    public String labelSeconds;

    public int xOffsetYear;

    public int xOffsetMonth;

    public int xOffsetDay;

    public int xOffsetHours;

    public int xOffsetMinutes;

    public int xOffsetSeconds;

    public int layoutRes;

    public int textGravity = Gravity.CENTER;

    public int bgColorWheel;

    public int textSizeOut;

    public int textSizeCenter;

    public int textColorOut;

    public int textColorCenter;

    public int dividerColor;

    public int dividerWidth;

    public boolean hasDivider;

    public float itemHeight;

    public int itemsVisibleCount = 5;

    /**
     * 是否只显示中间的label,默认每个item都显示
     */
    public boolean isCenterLabel;

    public int labelPadding;

    public int textSizeLabel;

    public float slidingCoefficient = 10f;

    public Typeface font = Typeface.MONOSPACE;

    public boolean calendarToggleVisible;

    public CompoundButton.OnCheckedChangeListener calendarToggleChangeListener;

    public String calendarToggleTextOff;

    public String calendarToggleTextOn;

    public boolean isAutoUpdateYears;

    //是否周模式
    public boolean isWeeksMode;

    //是否12小时制
    public boolean isTwelve;

    public String titleSon;

    public int titleSonColor;

    private Context context;

    public Context getContext() {
        return context;
    }

    public PickerOptions(Context context) {
        this.context = context;
        textSizeOut = context.getResources().getDimensionPixelOffset(R.dimen.sp_16);

        textSizeCenter = context.getResources().getDimensionPixelOffset(R.dimen.sp_20);

        textColorOut = context.getResources().getColor(R.color.xui_config_color_gray);

        textColorCenter = context.getResources().getColor(R.color.xui_config_color_main);

        dividerColor = context.getResources().getColor(R.color.xui_config_module_divider_color_deep);

        dividerWidth = context.getResources().getDimensionPixelOffset(R.dimen.xui_module_divider);
        itemHeight = context.getResources().getDimension(R.dimen.dp_40);

        labelPadding = context.getResources().getDimensionPixelOffset(R.dimen.dp_2);

        textSizeLabel = context.getResources().getDimensionPixelOffset(R.dimen.sp_18);
    }
}
