package com.feature.tui.widget.pickerview.view;

import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.adapter.ArrayWheelAdapter;
import com.feature.tui.widget.pickerview.adapter.NumericWheelAdapter;
import com.feature.tui.widget.pickerview.configure.PickerOptions;
import com.feature.tui.widget.pickerview.listener.OnTimeSelectChangeListener;
import com.feature.tui.widget.pickerview.utils.ChinaDate;
import com.feature.tui.widget.pickerview.utils.LunarCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description: time的管理类
 */
public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_twelve;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    private WheelView wv_seconds;

    private boolean[] type;
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_END_DAY = 31;

    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private int startMonth = DEFAULT_START_MONTH;
    private int endMonth = DEFAULT_END_MONTH;
    private int startDay = DEFAULT_START_DAY;
    private int endDay = DEFAULT_END_DAY;
    private int currentYear;

    private boolean isLunarCalendar = false;
    private PickerOptions mPickerOptions;
    private OnTimeSelectChangeListener mTimeSelectChangeListener;
    //当前显示的周模式年份
    private List<Integer> showYearsForWeeksMode = new ArrayList<>();

    public WheelTime(View view, PickerOptions pickerOptions) {
        super();
        this.view = view;
        this.mPickerOptions = pickerOptions;
        wv_year =  view.findViewById(R.id.year);
        wv_month =  view.findViewById(R.id.month);
        wv_day =  view.findViewById(R.id.day);
        wv_hours =  view.findViewById(R.id.hour);
        wv_minutes =  view.findViewById(R.id.min);
        wv_seconds =  view.findViewById(R.id.second);
        wv_twelve = view.findViewById(R.id.twelve);
        if (mPickerOptions.isTwelve) {
            ArrayList<String> twelveData = new ArrayList<>();
            twelveData.add(view.getContext().getString(R.string.pickerview_morning));
            twelveData.add(view.getContext().getString(R.string.pickerview_afternoon));
            wv_twelve.setVisibility(View.VISIBLE);
            wv_twelve.setAdapter(new ArrayWheelAdapter(twelveData));
        }

        type = mPickerOptions.type;
        setTimeSelectChangeListener(mPickerOptions.timeSelectChangeListener);
        setLunarMode(mPickerOptions.isLunarCalendar);

        if (mPickerOptions.startYear != 0 && mPickerOptions.endYear != 0
                && mPickerOptions.startYear <= mPickerOptions.endYear) {
            setStartYear(mPickerOptions.startYear);
            setEndYear(mPickerOptions.endYear);
        }
        //若手动设置了时间范围限制
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            if (mPickerOptions.startDate.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                throw new IllegalArgumentException("startDate can't be later than endDate");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.startDate != null) {
            if (mPickerOptions.startDate.get(Calendar.YEAR) < 1900) {
                throw new IllegalArgumentException("The startDate can not as early as 1900");
            } else {
                setRangDate();
            }
        } else if (mPickerOptions.endDate != null) {
            if (mPickerOptions.endDate.get(Calendar.YEAR) > 2100) {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            } else {
                setRangDate();
            }
        } else {//没有设置时间范围限制，则会使用默认范围。
            setRangDate();
        }

        setTime();

        setGravity(mPickerOptions.textGravity);
        setLabels(mPickerOptions.labelYear, mPickerOptions.labelMonth, mPickerOptions.labelDay
                , mPickerOptions.labelHours, mPickerOptions.labelMinutes, mPickerOptions.labelSeconds);
        setTextXOffset(mPickerOptions.xOffsetYear, mPickerOptions.xOffsetMonth, mPickerOptions.xOffsetDay,
                mPickerOptions.xOffsetHours, mPickerOptions.xOffsetMinutes, mPickerOptions.xOffsetSeconds);
        setItemsVisible(mPickerOptions.itemsVisibleCount);
        setCyclic(mPickerOptions.cyclic);
        setDividerColor(mPickerOptions.dividerColor);
        setDividerWidth(mPickerOptions.dividerWidth);
        hasDivider(mPickerOptions.hasDivider);
        setItemHeight(mPickerOptions.itemHeight);
        setTextSize(mPickerOptions.textSizeOut, mPickerOptions.textSizeCenter);
        setTextColor(mPickerOptions.textColorOut, mPickerOptions.textColorCenter);
        isCenterLabel(mPickerOptions.isCenterLabel);
        setLabelPadding(mPickerOptions.labelPadding);
        setLabelTextSize(mPickerOptions.textSizeLabel);
        setSlidingCoefficient(mPickerOptions.slidingCoefficient);
        setTypeface(mPickerOptions.font);
        if (mPickerOptions.isAutoUpdateYears)
            startAutoUpdateYears();
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void setRangDate() {
        setRangDate(mPickerOptions.startDate, mPickerOptions.endDate);
        initDefaultSelectedDate();
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    private void setTime() {
        int year, month, day, hours, minute, seconds;
        Calendar calendar = Calendar.getInstance();

        if (mPickerOptions.date == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        } else {
            year = mPickerOptions.date.get(Calendar.YEAR);
            month = mPickerOptions.date.get(Calendar.MONTH);
            day = mPickerOptions.date.get(Calendar.DAY_OF_MONTH);
            hours = mPickerOptions.date.get(Calendar.HOUR_OF_DAY);
            minute = mPickerOptions.date.get(Calendar.MINUTE);
            seconds = mPickerOptions.date.get(Calendar.SECOND);
        }

        setPicker(year, month, day, hours, minute, seconds);
    }

    private void initDefaultSelectedDate() {
        //如果手动设置了时间范围
        if (mPickerOptions.startDate != null && mPickerOptions.endDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (mPickerOptions.date == null || mPickerOptions.date.getTimeInMillis() < mPickerOptions.startDate.getTimeInMillis()
                    || mPickerOptions.date.getTimeInMillis() > mPickerOptions.endDate.getTimeInMillis()) {
                mPickerOptions.date = mPickerOptions.startDate;
            }
        } else if (mPickerOptions.startDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            mPickerOptions.date = mPickerOptions.startDate;
        } else if (mPickerOptions.endDate != null) {
            mPickerOptions.date = mPickerOptions.endDate;
        }
    }

    /**
     * 设置是否是农历
     *
     * @param isLunarCalendar
     */
    public void setLunarMode(boolean isLunarCalendar) {
        this.isLunarCalendar = isLunarCalendar;
    }

    /**
     * 获取是否是农历
     */
    public boolean isLunarMode() {
        return isLunarCalendar;
    }

    /**
     * 设置选择器的时间
     */
    public void setPicker(int year, final int month, int day, int h, int m, int s) {
        if (isLunarCalendar) {
            int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
            setLunar(lunar[0], lunar[1] - 1, lunar[2], lunar[3] == 1, h, m, s);
        } else {
            setSolar(year, month, day, h, m, s);
        }
    }

    /**
     * 设置农历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setLunar(int year, final int month, int day, boolean isLeap, int h, int m, int s) {

        wv_year.setAdapter(new ArrayWheelAdapter(ChinaDate.getYears(view.getContext(), startYear, endYear)));// 设置"年"的显示数据
        wv_year.setLabel("");// 添加文字
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据

        wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(view.getContext(), year)));
        wv_month.setLabel("");
        int leapMonth = ChinaDate.leapMonth(year);
        if (leapMonth != 0 && (month > leapMonth - 1 || isLeap)) { //选中月是闰月或大于闰月
            wv_month.setCurrentItem(month + 1);
        } else {
            wv_month.setCurrentItem(month);
        }

        if (mPickerOptions.isWeeksMode) {
            setDayAdapterForWeeksMode(year, true, false);
            setOnScrollForWeeksMode();
        } else {
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (ChinaDate.leapMonth(year) == 0) {
                wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.monthDays(year, month))));
            } else {
                wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.leapDays(year))));
            }
            wv_day.setCurrentItem(day - 1);
        }
        wv_day.setLabel("");

        if (mPickerOptions.isTwelve) {
            wv_hours.setAdapter(new NumericWheelAdapter(1, 12));
            if (h > 12) {
                h = h - 12;
                wv_twelve.setCurrentItem(1);
            }
            h--;
        } else {
            wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        }

        wv_hours.setCurrentItem(h);

        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        wv_minutes.setCurrentItem(m);

        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
        wv_seconds.setCurrentItem(m);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(index -> {
            int year_num = index + startYear;
            if (mPickerOptions.isWeeksMode) {
                setDayAdapterForWeeksMode(year_num, true, true);
            } else {
                // 判断是不是闰年,来确定月和日的选择
                wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(view.getContext(), year_num)));
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    wv_month.setCurrentItem(wv_month.getCurrentItem() + 1);
                } else {
                    wv_month.setCurrentItem(wv_month.getCurrentItem());
                }

                int currentIndex = wv_day.getCurrentItem();
                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.monthDays(year_num, wv_month.getCurrentItem()))));
                        maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem());
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1))));
                    maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1);
                }

                if (currentIndex > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }
            }
            onTimeSelect();
        });

        // 添加"月"监听
        wv_month.setOnItemSelectedListener(index -> {
            int month_num = index;
            int year_num = wv_year.getCurrentItem() + startYear;
            int currentIndex = wv_day.getCurrentItem();
            int maxItem = 29;
            if (ChinaDate.leapMonth(year_num) != 0 && month_num > ChinaDate.leapMonth(year_num) - 1) {
                if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.leapDays(year_num))));
                    maxItem = ChinaDate.leapDays(year_num);
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.monthDays(year_num, month_num))));
                    maxItem = ChinaDate.monthDays(year_num, month_num);
                }
            } else {
                wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(view.getContext(), ChinaDate.monthDays(year_num, month_num + 1))));
                maxItem = ChinaDate.monthDays(year_num, month_num + 1);
            }

            if (currentIndex > maxItem - 1) {
                currentIndex = maxItem - 1;
            }
            wv_day.setCurrentItem(currentIndex);

            onTimeSelect();
        });

        setChangedListener(wv_day);
        setChangedListener(wv_twelve);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new RuntimeException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置公历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setSolar(int year, final int month, int day, int h, int m, int s) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        currentYear = year;

        // 设置"年"的显示数据
        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));
        // 初始化时显示的数据
        wv_year.setCurrentItem(year - startYear);

        //开始年等于终止年
        if (startYear == endYear) {
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
            wv_month.setCurrentItem(month + 1 - startMonth);
        } else if (year == startYear) {
            //起始日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));
            wv_month.setCurrentItem(month + 1 - startMonth);
        } else if (year == endYear) {
            //终止日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
            wv_month.setCurrentItem(month);
        } else {
            wv_month.setAdapter(new NumericWheelAdapter(1, 12));
            wv_month.setCurrentItem(month);
        }

        if (mPickerOptions.isWeeksMode) {
            setDayAdapterForWeeksMode(year, false, false);
            setOnScrollForWeeksMode();
        } else {
            boolean leapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
            if (startYear == endYear && startMonth == endMonth) {
                if (list_big.contains(String.valueOf(month + 1))) {
                    if (endDay > 31) {
                        endDay = 31;
                    }
                    wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
                } else if (list_little.contains(String.valueOf(month + 1))) {
                    if (endDay > 30) {
                        endDay = 30;
                    }
                    wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
                } else {
                    // 闰年
                    if (leapYear) {
                        if (endDay > 29) {
                            endDay = 29;
                        }
                        wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
                    } else {
                        if (endDay > 28) {
                            endDay = 28;
                        }
                        wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
                    }
                }
                wv_day.setCurrentItem(day - startDay);
            } else if (year == startYear && month + 1 == startMonth) {
                // 起始日期的天数控制
                if (list_big.contains(String.valueOf(month + 1))) {

                    wv_day.setAdapter(new NumericWheelAdapter(startDay, 31));
                } else if (list_little.contains(String.valueOf(month + 1))) {

                    wv_day.setAdapter(new NumericWheelAdapter(startDay, 30));
                } else {
                    // 闰年 29，平年 28
                    wv_day.setAdapter(new NumericWheelAdapter(startDay, leapYear ? 29 : 28));
                }
                wv_day.setCurrentItem(day - startDay);
            } else if (year == endYear && month + 1 == endMonth) {
                // 终止日期的天数控制
                if (list_big.contains(String.valueOf(month + 1))) {
                    if (endDay > 31) {
                        endDay = 31;
                    }
                    wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
                } else if (list_little.contains(String.valueOf(month + 1))) {
                    if (endDay > 30) {
                        endDay = 30;
                    }
                    wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
                } else {
                    // 闰年
                    if (leapYear) {
                        if (endDay > 29) {
                            endDay = 29;
                        }
                        wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
                    } else {
                        if (endDay > 28) {
                            endDay = 28;
                        }
                        wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
                    }
                }
                wv_day.setCurrentItem(day - 1);
            } else {
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    // 闰年 29，平年 28
                    wv_day.setAdapter(new NumericWheelAdapter(startDay, leapYear ? 29 : 28));
                }
                wv_day.setCurrentItem(day - 1);
            }
        }

        if (mPickerOptions.isTwelve) {
            wv_hours.setAdapter(new NumericWheelAdapter(1, 12));
            if (h > 12) {
                h = h - 12;
                wv_twelve.setCurrentItem(1);
            }
            h--;
        } else {
            wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        }
        wv_hours.setCurrentItem(h);

        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        wv_minutes.setCurrentItem(m);

        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
        wv_seconds.setCurrentItem(s);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(index -> {
            int year_num = index + startYear;
            currentYear = year_num;
            if (mPickerOptions.isWeeksMode) {
                setDayAdapterForWeeksMode(currentYear, false, true);
            } else {
                //记录上一次的item位置
                int currentMonthItem = wv_month.getCurrentItem();
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (startYear == endYear) {
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));

                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                        wv_month.setCurrentItem(currentMonthItem);
                    }

                    int monthNum = currentMonthItem + startMonth;

                    if (startMonth == endMonth) {
                        //重新设置日
                        setReDay(year_num, monthNum, startDay, endDay, list_big, list_little);
                    } else if (monthNum == startMonth) {
                        //重新设置日
                        setReDay(year_num, monthNum, startDay, 31, list_big, list_little);
                    } else if (monthNum == endMonth) {
                        setReDay(year_num, monthNum, 1, endDay, list_big, list_little);
                    } else {//重新设置日
                        setReDay(year_num, monthNum, 1, 31, list_big, list_little);
                    }
                } else if (year_num == startYear) {//等于开始的年
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));

                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                        wv_month.setCurrentItem(currentMonthItem);
                    }

                    int month1 = currentMonthItem + startMonth;
                    if (month1 == startMonth) {
                        //重新设置日
                        setReDay(year_num, month1, startDay, 31, list_big, list_little);
                    } else {
                        //重新设置日
                        setReDay(year_num, month1, 1, 31, list_big, list_little);
                    }

                } else if (year_num == endYear) {
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                        wv_month.setCurrentItem(currentMonthItem);
                    }
                    int monthNum = currentMonthItem + 1;

                    if (monthNum == endMonth) {
                        //重新设置日
                        setReDay(year_num, monthNum, 1, endDay, list_big, list_little);
                    } else {
                        //重新设置日
                        setReDay(year_num, monthNum, 1, 31, list_big, list_little);
                    }

                } else {
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(1, 12));
                    wv_month.setCurrentItem(wv_month.getCurrentItem());
                    //重新设置日
                    setReDay(year_num, wv_month.getCurrentItem() + 1, 1, 31, list_big, list_little);
                }
            }

            onTimeSelect();
        });

        // 添加"月"监听
        wv_month.setOnItemSelectedListener(index -> {
            int month_num = index + 1;

            if (mPickerOptions.isWeeksMode)
                return;

            if (startYear == endYear) {
                month_num = month_num + startMonth - 1;
                if (startMonth == endMonth) {
                    //重新设置日
                    setReDay(currentYear, month_num, startDay, endDay, list_big, list_little);
                } else if (startMonth == month_num) {

                    //重新设置日
                    setReDay(currentYear, month_num, startDay, 31, list_big, list_little);
                } else if (endMonth == month_num) {
                    setReDay(currentYear, month_num, 1, endDay, list_big, list_little);
                } else {
                    setReDay(currentYear, month_num, 1, 31, list_big, list_little);
                }
            } else if (currentYear == startYear) {
                month_num = month_num + startMonth - 1;
                if (month_num == startMonth) {
                    //重新设置日
                    setReDay(currentYear, month_num, startDay, 31, list_big, list_little);
                } else {
                    //重新设置日
                    setReDay(currentYear, month_num, 1, 31, list_big, list_little);
                }

            } else if (currentYear == endYear) {
                if (month_num == endMonth) {
                    //重新设置日
                    setReDay(currentYear, wv_month.getCurrentItem() + 1, 1, endDay, list_big, list_little);
                } else {
                    setReDay(currentYear, wv_month.getCurrentItem() + 1, 1, 31, list_big, list_little);
                }

            } else {
                //重新设置日
                setReDay(currentYear, month_num, 1, 31, list_big, list_little);
            }

            onTimeSelect();
        });

        setChangedListener(wv_day);
        setChangedListener(wv_twelve);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new IllegalArgumentException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
    }

    private void setOnScrollForWeeksMode() {
        wv_day.setCallBack(selectedItem -> {
            if (!mPickerOptions.type[0] && !mPickerOptions.type[1] && mPickerOptions.cyclic && mPickerOptions.isAutoUpdateYears) {//周模式不显示年份可自动更新年份
                int currentIndex = wv_day.getCurrentItem();
                ArrayWheelAdapter.BeanForWeeksMode selectBean = (ArrayWheelAdapter.BeanForWeeksMode) wv_day.getAdapter().getItem(currentIndex);
                if (selectBean.month == 1) {//周模式滑动到1月份需要判断是否加入上年数据
                    ArrayWheelAdapter.BeanForWeeksMode hopePreBean = (ArrayWheelAdapter.BeanForWeeksMode) wv_day.getAdapter().getItem(0);
                    if (hopePreBean.year == selectBean.year) {
                        addOneYearDateForWeeksMode(selectBean.year - 1, currentIndex, true);
                    }
                } else if (selectBean.month == 12) {//周模式滑动到12月份需要判断是否加入下年数据
                    ArrayWheelAdapter.BeanForWeeksMode hopeNextBean = (ArrayWheelAdapter.BeanForWeeksMode) wv_day.getAdapter().getItem(wv_day.getAdapter().getItemsCount() - 1);
                    if (hopeNextBean.year == selectBean.year) {
                        addOneYearDateForWeeksMode(selectBean.year + 1, currentIndex, false);
                    }
                }
            }
        });
    }

    private void setDayAdapterForWeeksMode(int year, boolean isLunarCalendar, boolean isOnYearSelect) {
        if (isOnYearSelect) {
            showYearsForWeeksMode.clear();
        }
        if (!showYearsForWeeksMode.contains(year))
            showYearsForWeeksMode.add(year);
        List<ArrayWheelAdapter.BeanForWeeksMode> list = new ArrayList<>();
        for (int i = 0; i < showYearsForWeeksMode.size(); i++) {
            List<ArrayWheelAdapter.BeanForWeeksMode> listOneYear = ChinaDate.getDaysByYearForWeeksMode(view.getContext(), showYearsForWeeksMode.get(i), isLunarCalendar);
            list.addAll(listOneYear);
        }
        int dayIndex = ChinaDate.dayIndexOfYear() - 1;
        if (wv_day.getAdapter() != null)
            dayIndex = wv_day.getCurrentItem();
        wv_day.setAdapter(new ArrayWheelAdapter(list));
        if (dayIndex > list.size() - 1)
            dayIndex = list.size() - 1;
        wv_day.setCurrentItem(dayIndex);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) wv_day.getLayoutParams();
        lp.weight = 5.2f;
        wv_day.setLayoutParams(lp);
    }

    private void onTimeSelect() {
        if (mTimeSelectChangeListener == null)
            return;
        Date date = null;
        try {
            String time = getTime();
            date = WheelTime.dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            mTimeSelectChangeListener.onTimeSelectChanged(date);
        }
    }

    private void addOneYearDateForWeeksMode(int year, int currentIndex, boolean isPre) {
        List<ArrayWheelAdapter.BeanForWeeksMode> oldList = ((ArrayWheelAdapter<ArrayWheelAdapter.BeanForWeeksMode>) wv_day.getAdapter()).getItems();
        List<ArrayWheelAdapter.BeanForWeeksMode> newList = ChinaDate.getDaysByYearForWeeksMode(view.getContext(), year, isLunarCalendar);
        int addSize = newList.size();
        if (isPre) {
            if (!showYearsForWeeksMode.contains(year))
                showYearsForWeeksMode.add(0, year);
            newList.addAll(oldList);
            wv_day.setAdapter(new ArrayWheelAdapter(newList));
            wv_day.setCurrentItem(addSize + currentIndex);
        } else {
            if (!showYearsForWeeksMode.contains(year))
                showYearsForWeeksMode.add(year);
            oldList.addAll(newList);
            wv_day.setAdapter(new ArrayWheelAdapter(oldList));
        }
    }

    private void setChangedListener(WheelView wheelView) {
        if (wheelView == null)
            return;
        wheelView.setOnItemSelectedListener(index -> onTimeSelect());
    }

    private void setReDay(int year_num, int monthNum, int startD, int endD, List<String> list_big, List<String> list_little) {
        int currentItem = wv_day.getCurrentItem();

        //int maxItem;
        if (list_big.contains(String.valueOf(monthNum))) {
            if (endD > 31) {
                endD = 31;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
            //maxItem = endD;
        } else if (list_little.contains(String.valueOf(monthNum))) {
            if (endD > 30) {
                endD = 30;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
            //maxItem = endD;
        } else {
            if ((year_num % 4 == 0 && year_num % 100 != 0)
                    || year_num % 400 == 0) {
                if (endD > 29) {
                    endD = 29;
                }
                //maxItem = endD;
            } else {
                if (endD > 28) {
                    endD = 28;
                }
                //maxItem = endD;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        }

        if (currentItem > wv_day.getAdapter().getItemsCount() - 1) {
            currentItem = wv_day.getAdapter().getItemsCount() - 1;
        }
        wv_day.setCurrentItem(currentItem);
    }

    /**
     * @param textSizeOut    设置分割线以外文字的大小，为dimension,不是资源id
     * @param textSizeCenter 设置分割线之间的文字的大小，为dimension,不是资源id
     */
    public void setTextSize(int textSizeOut, int textSizeCenter) {
        wv_day.setTextSize(textSizeOut, textSizeCenter);
        wv_month.setTextSize(textSizeOut, textSizeCenter);
        wv_year.setTextSize(textSizeOut, textSizeCenter);
        wv_hours.setTextSize(textSizeOut, textSizeCenter);
        wv_minutes.setTextSize(textSizeOut, textSizeCenter);
        wv_seconds.setTextSize(textSizeOut, textSizeCenter);
        if (wv_twelve != null)
            wv_twelve.setTextSize(textSizeOut, textSizeCenter);
    }

    /**
     * @param gravity 设置内容显示的位置 Gravity.CENTER、Gravity.LEFT、Gravity.RIGHT
     */
    public void setGravity(int gravity) {
        wv_year.setGravity(gravity);
        wv_month.setGravity(gravity);
        wv_day.setGravity(gravity);
        wv_hours.setGravity(gravity);
        wv_minutes.setGravity(gravity);
        wv_seconds.setGravity(gravity);
        if (wv_twelve != null)
            wv_twelve.setGravity(gravity);
    }

    /**
     * 设置附加单位
     *
     * @param labelYear
     * @param labelMonth
     * @param labelDay
     * @param labelHours
     * @param labelMins
     * @param labelSeconds
     */
    public void setLabels(String labelYear, String labelMonth, String labelDay, String labelHours, String labelMins, String labelSeconds) {
        if (isLunarCalendar) {
            return;
        }
        if (labelYear != null) {
            wv_year.setLabel(labelYear);
        } else {
            wv_year.setLabel(view.getContext().getString(R.string.pickerview_year));
        }
        if (labelMonth != null) {
            wv_month.setLabel(labelMonth);
        } else {
            wv_month.setLabel(view.getContext().getString(R.string.pickerview_month));
        }
        if (labelDay != null) {
            wv_day.setLabel(labelDay);
        } else {
            wv_day.setLabel(view.getContext().getString(R.string.pickerview_day));
        }
        if (mPickerOptions.isWeeksMode) {
            wv_day.setLabel("");
        }
        if (labelHours != null) {
            wv_hours.setLabel(labelHours);
        } else {
            wv_hours.setLabel(view.getContext().getString(R.string.pickerview_hours));
        }
        if (labelMins != null) {
            wv_minutes.setLabel(labelMins);
        } else {
            wv_minutes.setLabel(view.getContext().getString(R.string.pickerview_minutes));
        }
        if (labelSeconds != null) {
            wv_seconds.setLabel(labelSeconds);
        } else {
            wv_seconds.setLabel(view.getContext().getString(R.string.pickerview_seconds));
        }
    }

    /**
     * 设置文字X轴偏移的位置
     */
    public void setTextXOffset(int xOffsetYear, int xOffsetMonth, int xOffsetDay,
                               int xOffsetHours, int xOffsetMinutes, int xOffsetSeconds) {
        wv_year.setTextXOffset(xOffsetYear);
        wv_month.setTextXOffset(xOffsetMonth);
        wv_day.setTextXOffset(xOffsetDay);
        wv_hours.setTextXOffset(xOffsetHours);
        wv_minutes.setTextXOffset(xOffsetMinutes);
        wv_seconds.setTextXOffset(xOffsetSeconds);
        if (wv_twelve != null)
            wv_twelve.setTextXOffset(xOffsetSeconds);
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    public void setTypeface(Typeface font) {
        wv_year.setTypeface(font);
        wv_month.setTypeface(font);
        wv_day.setTypeface(font);
        wv_hours.setTypeface(font);
        wv_minutes.setTypeface(font);
        wv_seconds.setTypeface(font);
        if (wv_twelve != null)
            wv_twelve.setTypeface(font);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_minutes.setCyclic(cyclic);
        wv_seconds.setCyclic(cyclic);
    }

    /**
     * 得到当前选中的时间
     *
     * @return
     */
    public String getTime() {
        StringBuilder sb = new StringBuilder();
        if (mPickerOptions.isWeeksMode) {
            ArrayWheelAdapter.BeanForWeeksMode bean = (ArrayWheelAdapter.BeanForWeeksMode) wv_day.getAdapter().getItem(wv_day.getCurrentItem());
            sb.append(bean.year).append("-")
                    .append(bean.month).append("-")
                    .append(bean.day).append(" ")
                    .append(wv_hours.getCurrentItem()).append(":")
                    .append(wv_minutes.getCurrentItem()).append(":")
                    .append(wv_seconds.getCurrentItem());
            return sb.toString();
        }
        if (isLunarCalendar) {
            //如果是农历 返回对应的公历时间
            return getLunarTime();
        }

        if (mPickerOptions.isTwelve) {
            sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1)).append(" ")
                    .append(wv_hours.getCurrentItem() + 1).append(":")
                    .append(wv_minutes.getCurrentItem()).append(":")
                    .append(wv_seconds.getCurrentItem());
            if (wv_twelve.getCurrentItem() == 0) {
                sb.append(" am");
            } else
                sb.append(" pm");

            return ChinaDate.parse24Hours(sb.toString());
        }

        if (currentYear == startYear) {
            if ((wv_month.getCurrentItem() + startMonth) == startMonth) {
                sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                        .append((wv_month.getCurrentItem() + startMonth)).append("-")
                        .append((wv_day.getCurrentItem() + startDay)).append(" ")
                        .append(wv_hours.getCurrentItem()).append(":")
                        .append(wv_minutes.getCurrentItem()).append(":")
                        .append(wv_seconds.getCurrentItem());
            } else {
                sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                        .append((wv_month.getCurrentItem() + startMonth)).append("-")
                        .append((wv_day.getCurrentItem() + 1)).append(" ")
                        .append(wv_hours.getCurrentItem()).append(":")
                        .append(wv_minutes.getCurrentItem()).append(":")
                        .append(wv_seconds.getCurrentItem());
            }

        } else {
            sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1)).append(" ")
                    .append(wv_hours.getCurrentItem()).append(":")
                    .append(wv_minutes.getCurrentItem()).append(":")
                    .append(wv_seconds.getCurrentItem());
        }

        String result = sb.toString();
        if (wv_month.isAutoUpdateYears()) {
            String year = sb.toString().split("-")[0];
            result = result.replaceFirst(year, wv_month.getAutoUpdateYears() + "");
        }
        return result;
    }

    /**
     * 农历返回对应的公历时间
     *
     * @return
     */
    private String getLunarTime() {
        StringBuilder sb = new StringBuilder();
        int year = wv_year.getCurrentItem() + startYear;
        int month = 1;
        boolean isLeapMonth = false;
        if (ChinaDate.leapMonth(year) == 0) {
            month = wv_month.getCurrentItem() + 1;
        } else {
            if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) <= 0) {
                month = wv_month.getCurrentItem() + 1;
            } else if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) == 1) {
                month = wv_month.getCurrentItem();
                isLeapMonth = true;
            } else {
                month = wv_month.getCurrentItem();
            }
        }
        int day = wv_day.getCurrentItem() + 1;
        int[] solar = LunarCalendar.lunarToSolar(year, month, day, isLeapMonth);

        sb.append(solar[0]).append("-")
                .append(solar[1]).append("-")
                .append(solar[2]).append(" ")
                .append(wv_hours.getCurrentItem()).append(":")
                .append(wv_minutes.getCurrentItem()).append(":")
                .append(wv_seconds.getCurrentItem());
        String result = sb.toString();
        if (wv_month.isAutoUpdateYears()) {
            String yea = sb.toString().split("-")[0];
            result = result.replaceFirst(yea, wv_month.getAutoUpdateYears() + "");
        }
        return result;
    }

    /**
     * 得到根视图
     *
     * @return
     */
    public View getView() {
        return view;
    }

    /**
     * 得到开始的年份
     *
     * @return
     */
    public int getStartYear() {
        return startYear;
    }

    /**
     * 设置开始的年份
     *
     * @return
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    /**
     * 得到结束的年份
     *
     * @return
     */
    public int getEndYear() {
        return endYear;
    }

    /**
     * 设置结束的年份
     *
     * @return
     */
    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    public void setRangDate(Calendar startDate, Calendar endDate) {

        if (startDate == null && endDate != null) {
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH) + 1;
            int day = endDate.get(Calendar.DAY_OF_MONTH);
            if (year > startYear) {
                this.endYear = year;
                this.endMonth = month;
                this.endDay = day;
            } else if (year == startYear) {
                if (month > startMonth) {
                    this.endYear = year;
                    this.endMonth = month;
                    this.endDay = day;
                } else if (month == startMonth) {
                    if (day > startDay) {
                        this.endYear = year;
                        this.endMonth = month;
                        this.endDay = day;
                    }
                }
            }

        } else if (startDate != null && endDate == null) {
            int year = startDate.get(Calendar.YEAR);
            int month = startDate.get(Calendar.MONTH) + 1;
            int day = startDate.get(Calendar.DAY_OF_MONTH);
            if (year < endYear) {
                this.startMonth = month;
                this.startDay = day;
                this.startYear = year;
            } else if (year == endYear) {
                if (month < endMonth) {
                    this.startMonth = month;
                    this.startDay = day;
                    this.startYear = year;
                } else if (month == endMonth) {
                    if (day < endDay) {
                        this.startMonth = month;
                        this.startDay = day;
                        this.startYear = year;
                    }
                }
            }

        } else if (startDate != null && endDate != null) {
            this.startYear = startDate.get(Calendar.YEAR);
            this.endYear = endDate.get(Calendar.YEAR);
            this.startMonth = startDate.get(Calendar.MONTH) + 1;
            this.endMonth = endDate.get(Calendar.MONTH) + 1;
            this.startDay = startDate.get(Calendar.DAY_OF_MONTH);
            this.endDay = endDate.get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * @param itemHeight 设置一行的高度
     */
    public void setItemHeight(float itemHeight) {
        wv_day.setItemHeight(itemHeight);
        wv_month.setItemHeight(itemHeight);
        wv_year.setItemHeight(itemHeight);
        wv_hours.setItemHeight(itemHeight);
        wv_minutes.setItemHeight(itemHeight);
        wv_seconds.setItemHeight(itemHeight);
        if (wv_twelve != null)
            wv_twelve.setItemHeight(itemHeight);
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        wv_day.setDividerColor(dividerColor);
        wv_month.setDividerColor(dividerColor);
        wv_year.setDividerColor(dividerColor);
        wv_hours.setDividerColor(dividerColor);
        wv_minutes.setDividerColor(dividerColor);
        wv_seconds.setDividerColor(dividerColor);
        if (wv_twelve != null)
            wv_twelve.setDividerColor(dividerColor);
    }

    /**
     * 设置分割线的宽度
     *
     * @param dividerWidth
     */
    public void setDividerWidth(int dividerWidth) {
        wv_day.setDividerWidth(dividerWidth);
        wv_month.setDividerWidth(dividerWidth);
        wv_year.setDividerWidth(dividerWidth);
        wv_hours.setDividerWidth(dividerWidth);
        wv_minutes.setDividerWidth(dividerWidth);
        wv_seconds.setDividerWidth(dividerWidth);
        if (wv_twelve != null)
            wv_twelve.setDividerWidth(dividerWidth);
    }

    /**
     * 是否有中心区域的分割线，默认false
     */
    public void hasDivider(boolean hasDivider) {
        wv_day.hasDivider(hasDivider);
        wv_month.hasDivider(hasDivider);
        wv_year.hasDivider(hasDivider);
        wv_hours.hasDivider(hasDivider);
        wv_minutes.hasDivider(hasDivider);
        wv_seconds.hasDivider(hasDivider);
        wv_twelve.hasDivider(hasDivider);
    }

    /**
     * 设置分割线之间的文字的颜色
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     * @param textColorCenter
     */
    public void setTextColor(int textColorOut, int textColorCenter) {
        wv_day.setTextColor(textColorOut, textColorCenter);
        wv_month.setTextColor(textColorOut, textColorCenter);
        wv_year.setTextColor(textColorOut, textColorCenter);
        wv_hours.setTextColor(textColorOut, textColorCenter);
        wv_minutes.setTextColor(textColorOut, textColorCenter);
        wv_seconds.setTextColor(textColorOut, textColorCenter);
        if (wv_twelve != null)
            wv_twelve.setTextColor(textColorOut, textColorCenter);
    }

    /**
     * @param isCenterLabel 是否只显示中间选中项的
     */
    public void isCenterLabel(boolean isCenterLabel) {
        wv_day.isCenterLabel(isCenterLabel);
        wv_month.isCenterLabel(isCenterLabel);
        wv_year.isCenterLabel(isCenterLabel);
        wv_hours.isCenterLabel(isCenterLabel);
        wv_minutes.isCenterLabel(isCenterLabel);
        wv_seconds.isCenterLabel(isCenterLabel);
        wv_twelve.isCenterLabel(isCenterLabel);
    }

    /**
     * 当 isCenterLabel=true时，此值作为文案和单位的间距
     *
     * @param labelPadding
     */
    public void setLabelPadding(int labelPadding) {
        wv_day.setLabelPadding(labelPadding);
        wv_month.setLabelPadding(labelPadding);
        wv_year.setLabelPadding(labelPadding);
        wv_hours.setLabelPadding(labelPadding);
        wv_minutes.setLabelPadding(labelPadding);
        wv_seconds.setLabelPadding(labelPadding);
        if (wv_twelve != null)
            wv_twelve.setLabelPadding(labelPadding);
    }

    /**
     * 当 isCenterLabel=true时，设置单位的字体的大小
     */
    public void setLabelTextSize(int textSizeLabel) {
        wv_day.setLabelTextSize(textSizeLabel);
        wv_month.setLabelTextSize(textSizeLabel);
        wv_year.setLabelTextSize(textSizeLabel);
        wv_hours.setLabelTextSize(textSizeLabel);
        wv_minutes.setLabelTextSize(textSizeLabel);
        wv_seconds.setLabelTextSize(textSizeLabel);
        if (wv_twelve != null)
            wv_twelve.setLabelTextSize(textSizeLabel);
    }

    /**
     * 滑动系数，值越大 ，滑动越慢
     */
    public void setSlidingCoefficient(float slidingCoefficient) {
        wv_day.setSlidingCoefficient(slidingCoefficient);
        wv_month.setSlidingCoefficient(slidingCoefficient);
        wv_year.setSlidingCoefficient(slidingCoefficient);
        wv_hours.setSlidingCoefficient(slidingCoefficient);
        wv_minutes.setSlidingCoefficient(slidingCoefficient);
        wv_seconds.setSlidingCoefficient(slidingCoefficient);
        if (wv_twelve != null)
            wv_twelve.setSlidingCoefficient(slidingCoefficient);
    }

    /**
     * 设置时间选择器的监听
     *
     * @param timeSelectChangeListener
     */
    public void setTimeSelectChangeListener(OnTimeSelectChangeListener timeSelectChangeListener) {
        this.mTimeSelectChangeListener = timeSelectChangeListener;
    }

    /**
     * 设置滚轮的最大可见数目
     */
    public void setItemsVisible(int itemsVisibleCount) {
        wv_day.setItemsVisibleCount(itemsVisibleCount);
        wv_month.setItemsVisibleCount(itemsVisibleCount);
        wv_year.setItemsVisibleCount(itemsVisibleCount);
        wv_hours.setItemsVisibleCount(itemsVisibleCount);
        wv_minutes.setItemsVisibleCount(itemsVisibleCount);
        wv_seconds.setItemsVisibleCount(itemsVisibleCount);
        if (wv_twelve != null)
            wv_twelve.setItemsVisibleCount(itemsVisibleCount);
    }

    /**
     * 启动自动更新年份
     */
    public void startAutoUpdateYears() {
        if (mPickerOptions.isWeeksMode)
            wv_day.startAutoUpdateYears(startYear, endYear, currentYear);
        else
            wv_month.startAutoUpdateYears(startYear, endYear, currentYear);
    }

}
