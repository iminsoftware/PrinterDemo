package com.feature.tui.widget.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.configure.PickerOptions;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:
 */
public class TimePickerView {

    /**
     * 自定义控件
     */
    private WheelTime wheelTime;
    private PickerOptions mPickerOptions;
    private LinearLayout mTimePickerView;
    private ToggleButton mCalendarToggleBtn;

    public TimePickerView(PickerOptions pickerOptions) {
        mPickerOptions = pickerOptions;
        initView(pickerOptions.getContext());
    }

    private void initView(Context context) {
        if (mPickerOptions.layoutRes == 0) {
            mPickerOptions.layoutRes = R.layout.pickerview_time;
        }
        mTimePickerView = (LinearLayout) LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, null);
        if (mPickerOptions.bgColorWheel != 0) {
            mTimePickerView.setBackgroundColor(mPickerOptions.bgColorWheel);
        }
        initWheelTime(mTimePickerView);
    }

    private void setSwitchCalendarVisible(boolean visible) {
        if (visible) {
            mCalendarToggleBtn.setVisibility(View.VISIBLE);
        } else {
            mCalendarToggleBtn.setVisibility(View.GONE);
        }
    }

    private void setSwitchCalendarCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mCalendarToggleBtn.setOnCheckedChangeListener(listener);
    }

    private void setCalendarToggleText(String textOff, String textOn) {
        mCalendarToggleBtn.setTextOff(textOff);
        mCalendarToggleBtn.setTextOn(textOn);
    }

    public boolean getLunarOpen() {
        return mCalendarToggleBtn.isChecked();
    }

    private void initWheelTime(LinearLayout timePickerView) {
        mCalendarToggleBtn = timePickerView.findViewById(R.id.calendar_toggle_btn);
        setSwitchCalendarVisible(mPickerOptions.calendarToggleVisible);
        setSwitchCalendarCheckedChangeListener(mPickerOptions.calendarToggleChangeListener);
        if (!TextUtils.isEmpty(mPickerOptions.calendarToggleTextOff) && !TextUtils.isEmpty(mPickerOptions.calendarToggleTextOn)) {
            setCalendarToggleText(mPickerOptions.calendarToggleTextOff, mPickerOptions.calendarToggleTextOn);
        }
        wheelTime = new WheelTime(timePickerView, mPickerOptions);
    }

    /**
     * 得到选中的item下标，为 int[3]
     *
     * @return Date
     */
    public Date returnData() {
        try {
            return WheelTime.dateFormat.parse(wheelTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 目前暂时只支持设置1900 - 2100年
     *
     * @param lunar 农历的开关
     */
    public void setLunarCalendar(boolean lunar) {
        try {
            int year, month, day, hours, minute, seconds;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(WheelTime.dateFormat.parse(wheelTime.getTime()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

            wheelTime.setLunarMode(lunar);
            wheelTime.setLabels(mPickerOptions.labelYear, mPickerOptions.labelMonth, mPickerOptions.labelDay,
                    mPickerOptions.labelHours, mPickerOptions.labelMinutes, mPickerOptions.labelSeconds);
            wheelTime.setPicker(year, month, day, hours, minute, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否是农历
     *
     * @return
     */
    public boolean isLunarCalendar() {
        return wheelTime.isLunarMode();
    }

    /**
     * 得到时间选择器View
     *
     * @return
     */
    public View getContentView() {
        return mTimePickerView;
    }

}
