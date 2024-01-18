package com.feature.tui.dialog.builder;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feature.tui.R;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.pickerview.listener.OnTimeSelectChangeListener;
import com.feature.tui.widget.pickerview.view.TimePickerView;

import java.util.Calendar;

public class TimePickerBuilder
        extends BasePickerBuilder<TimePickerBuilder> {

    private TimePickerView timePickerView;

    private TextView titleSon;

    public TimePickerView getTimePickerView() {
        return this.timePickerView;
    }

    public void setTimePickerView(TimePickerView timePickerView) {
        this.timePickerView = timePickerView;
    }

    public TimePickerBuilder isCyclic(boolean cyclic) {
        this.getMPickerOptions().cyclic = cyclic;
        return this;
    }

    public TimePickerBuilder setTextXOffset(int xOffsetYear, int xOffsetMonth, int xOffsetDay, int xOffsetHours, int xOffsetMinutes, int xOffsetSeconds) {
        this.getMPickerOptions().xOffsetYear = xOffsetYear;
        this.getMPickerOptions().xOffsetMonth = xOffsetMonth;
        this.getMPickerOptions().xOffsetDay = xOffsetDay;
        this.getMPickerOptions().xOffsetHours = xOffsetHours;
        this.getMPickerOptions().xOffsetMinutes = xOffsetMinutes;
        this.getMPickerOptions().xOffsetSeconds = xOffsetSeconds;
        return this;
    }

    public TimePickerBuilder setLabel(String labelYear, String labelMonth, String labelDay, String labelHours, String labelMinutes, String labelSeconds) {
        this.getMPickerOptions().labelYear = labelYear;
        this.getMPickerOptions().labelMonth = labelMonth;
        this.getMPickerOptions().labelDay = labelDay;
        this.getMPickerOptions().labelHours = labelHours;
        this.getMPickerOptions().labelMinutes = labelMinutes;
        this.getMPickerOptions().labelSeconds = labelSeconds;
        return this;
    }

    public TimePickerBuilder setTimeSelectChangeListener(OnTimeSelectChangeListener listener) {
        this.getMPickerOptions().timeSelectChangeListener = listener;
        return this;
    }

    public TimePickerBuilder setLunarCalendar(boolean lunarCalendar) {
        this.getMPickerOptions().isLunarCalendar = lunarCalendar;
        return this;
    }

    public TimePickerBuilder setType(boolean[] type) {
        this.getMPickerOptions().type = type;
        return this;
    }

    public TimePickerBuilder setDate(Calendar date) {
        this.getMPickerOptions().date = date;
        return this;
    }

    public TimePickerBuilder setRangDate(Calendar startDate, Calendar endDate) {
        this.getMPickerOptions().startDate = startDate;
        this.getMPickerOptions().endDate = endDate;
        return this;
    }

    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        titleSon = new TextView(context);
        titleSon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleSon.setGravity(Gravity.CENTER_HORIZONTAL);
        titleSon.setTextColor(context.getResources().getColor(R.color.xui_color_666666));
        titleSon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int padding = XUiDisplayHelper.dp2px(context, 10);
        titleSon.setPadding(padding, padding, padding, 0);
        titleSon.setVisibility(View.GONE);

        linearLayout.addView(titleSon);

        TimePickerView timePickerView = this.timePickerView = new TimePickerView(this.getMPickerOptions());
        linearLayout.addView(timePickerView.getContentView());

        setSonTitle(getMPickerOptions().titleSon, getMPickerOptions().titleSonColor);

        return linearLayout;
    }

    public TimePickerBuilder setCalendarToggleVisible(boolean visible) {
        this.getMPickerOptions().calendarToggleVisible = visible;
        return this;
    }

    public TimePickerBuilder setCalendarToggleChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.getMPickerOptions().calendarToggleChangeListener = listener;
        return this;
    }

    public TimePickerBuilder setCalendarToggleText(String calendarToggleTextOff, String calendarToggleTextOn) {
        this.getMPickerOptions().calendarToggleTextOff = calendarToggleTextOff;
        this.getMPickerOptions().calendarToggleTextOn = calendarToggleTextOn;
        return this;
    }

    public TimePickerBuilder isAutoUpdateYears(boolean visible) {
        if (!this.getMPickerOptions().type[0] && this.getMPickerOptions().cyclic) {
            this.getMPickerOptions().isAutoUpdateYears = visible;
        }
        return this;
    }

    public TimePickerBuilder isWeeksMode(boolean isWeeksMode) {
        this.getMPickerOptions().isWeeksMode = isWeeksMode;
        return this;
    }

    public TimePickerBuilder isTwelve(boolean isTwelve) {
        this.getMPickerOptions().isTwelve = isTwelve;
        return this;
    }

    public TimePickerBuilder setSonTitle(String str, int color) {
        return this.setSonTitle(str, color, null);
    }

    /**
     * 设置子标题
     *
     * @param str
     * @param color =0 为默认666666
     */
    public TimePickerBuilder setSonTitle(String str, int color, View.OnClickListener listener) {
        this.getMPickerOptions().titleSon = str;
        this.getMPickerOptions().titleSonColor = color;
        if (titleSon == null)
            return this;
        if (TextUtils.isEmpty(str)) {
            titleSon.setVisibility(View.GONE);
        } else {
            titleSon.setText(str);
            titleSon.setVisibility(View.VISIBLE);
            if (color != 0)
                titleSon.setTextColor(color);
            if (listener != null)
                titleSon.setOnClickListener(listener);
            else
                titleSon.setOnClickListener(v -> getMPickerOptions().timeSelectChangeListener.onHeadClick(timePickerView.returnData()));
        }
        return this;
    }

    public TimePickerBuilder(Context context) {
        super(context);
    }

}
