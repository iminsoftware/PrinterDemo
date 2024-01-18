package com.feature.tui.widget.pickerview.utils;

import android.content.Context;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.adapter.ArrayWheelAdapter;
import com.feature.tui.widget.pickerview.view.WheelTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:
 */
public class ChinaDate {

    public static final int TYPE_COMMON = 1;

    public static final int TYPE_CHINA = 2;

    public static final int TYPE_CHINA_LUNAR = 3;

    public static final int TYPE_CHINA_LUNAR_TWELVE = 4;

    public static final int TYPE_WEEKS = 5;

    /**
     * <lunarInfo 数组值的计算原理>
     * <p>
     * 0x代表十六进制，后面的五位数是十六进制数。
     * 举个例子: 1980年的数据是 0x095b0
     * 二进制:  0000 1001 0101 1011 0000
     * 1-4:   表示当年是否为闰年，是的话为1，否则为0。
     * 5-16: 为除了闰月外的正常月份是大月还是小月，1为30天，0为29天。
     * 注意:  从1月到12月对应的是第16位到第5位。
     * 17-20: 非闰年为0，大于0表示闰月月份，仅当存在闰月的情况下有意义。
     */
    private final static long[] lunarInfo = new long[]{
            //1900-1909
            0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            //1910-1919
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
            //1920-1929
            0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            //1930-1939
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
            //1940-1949
            0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
            //1950-1959
            0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
            //1960-1969
            0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
            //1970-1979
            0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
            //1980-1989
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
            //1990-1999
            0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            //2000-2009
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
            //2010-2019
            0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
            //2020-2029
            0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
            //2030-2039
            0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
            //2040-2049
            0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
            //2050-2059
            0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06b20, 0x1a6c4, 0x0aae0,
            //2060-2069
            0x0a2e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
            //2070-2079
            0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
            //2080-2089
            0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
            //2090-2099
            0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a2d0, 0x0d150, 0x0f252,
            //2100
            0x0d520};

    /**
     * 传回农历
     *
     * @param y 年的总天数
     * @return 农历
     */
    public static int lYearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0) {
                sum += 1;
            }
        }
        return (sum + leapDays(y));
    }

    /**
     * 传回农历
     *
     * @param y 年闰月的天数
     * @return 农历
     */
    public static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0) {
                return 30;
            } else {
                return 29;
            }
        } else {
            return 0;
        }
    }

    /**
     * 传回农历
     *
     * @param y 年闰哪个月 1-12 , 没闰传回 0
     * @return 农历
     */
    public static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    /**
     * 传回农历 y
     *
     * @param y y年m月的总天数
     * @param m y年m月的总天数
     * @return 农历
     */
    public static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0) {
            return 29;
        } else {
            return 30;
        }
    }

    /**
     * 传出y年m月d日对应的农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
     *
     * @param y 年
     * @param m 月
     * @param d 日
     * @return y年m月d日对应的农历
     */
    public static long[] calElement(int y, int m, int d) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(0 + 1900, 0, 31).getTime();
        Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        nongDate[5] = offset + 40;
        nongDate[4] = 14;
        for (i = 1900; i < 2100 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        // 闰哪个月
        leap = leapMonth(i);
        nongDate[6] = 0;
        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }
            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1)) {
                nongDate[6] = 0;
            }
            offset -= temp;
            if (nongDate[6] == 0) {
                nongDate[4]++;
            }
        }
        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }

    public static String getChinaDate(Context context, int day) {
        String a = "";
        if (day == 10) {
            return context.getString(R.string.number_c10);
        }
        if (day == 20) {
            return context.getString(R.string.number_20);
        }
        if (day == 30) {
            return context.getString(R.string.number_30);
        }
        int two = (int) ((day) / 10);
        if (two == 0) {
            a = context.getString(R.string.number_chu);
        }
        if (two == 1) {
            a = context.getString(R.string.number_10);
        }
        if (two == 2) {
            a = context.getString(R.string.number_er);
        }
        if (two == 3) {
            a = context.getString(R.string.number_3);
        }
        int one = (int) (day % 10);
        switch (one) {
            case 1:
                a += context.getString(R.string.number_1);
                break;
            case 2:
                a += context.getString(R.string.number_2);
                break;
            case 3:
                a += context.getString(R.string.number_3);
                break;
            case 4:
                a += context.getString(R.string.number_4);
                break;
            case 5:
                a += context.getString(R.string.number_5);
                break;
            case 6:
                a += context.getString(R.string.number_6);
                break;
            case 7:
                a += context.getString(R.string.number_7);
                break;
            case 8:
                a += context.getString(R.string.number_8);
                break;
            case 9:
                a += context.getString(R.string.number_9);
                break;
            default:
                break;
        }
        return a;
    }

    public static int dayIndexOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String oneDay(Context context, Date date, boolean getYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return oneDay(context, calendar, getYear);
    }

    public static String oneDay(Context context, Calendar calendar, boolean getYear) {
        int year, month, day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(context.getString(R.string.pickerview_lunar_calendar));
            if (getYear) {
                sToday.append(lunar[0]);
                sToday.append(context.getString(R.string.pickerview_year));
            }
            if (lunar[3] == 1) {
                sToday.append(context.getString(R.string.pickerview_run));
            }
            String[] nStr1 = context.getResources().getStringArray(R.array.lunar_month);
            sToday.append(nStr1[lunar[1]]);
            sToday.append(context.getString(R.string.pickerview_month));
            sToday.append(getChinaDate(context, lunar[2]));
            return sToday.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getYears(Context context, int startYear, int endYear) {
        ArrayList<String> years = new ArrayList<>();
        for (int i = startYear; i < endYear; i++) {
            //years.add(String.format("%s(%d)", getLunarYearText(i), i));
            years.add(String.format("%d%s", i, context.getString(R.string.pickerview_year)));
        }
        return years;
    }

    /**
     * 获取year年的所有月份
     *
     * @param year 年
     * @return 月份列表
     */
    public static ArrayList<String> getMonths(Context context, int year) {
        ArrayList<String> baseMonths = new ArrayList<>();
        String[] nStr1 = context.getResources().getStringArray(R.array.lunar_month);
        for (int i = 1; i < nStr1.length; i++) {
            baseMonths.add(nStr1[i] + context.getString(R.string.pickerview_month));
        }
        if (leapMonth(year) != 0) {
            baseMonths.add(leapMonth(year), context.getString(R.string.pickerview_run) + nStr1[leapMonth(year)] + context.getString(R.string.pickerview_month));
        }
        return baseMonths;
    }

    /**
     * 获取每月农历显示名称
     *
     * @param maxDay 天
     * @return 名称列表
     */
    public static ArrayList<String> getLunarDays(Context context, int maxDay) {
        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= maxDay; i++) {
            days.add(getChinaDate(context, i));
        }
        return days;
    }

    /**
     * 获取全年的天数
     *
     * @return
     */
    public static List<ArrayWheelAdapter.BeanForWeeksMode> getDaysByYearForWeeksMode(Context context, int y, boolean isLunar) {
        ArrayList<ArrayWheelAdapter.BeanForWeeksMode> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int i = 0;
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        while (true) {
            i++;
            calendar.set(Calendar.DAY_OF_YEAR, i);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (y != calendar.get(Calendar.YEAR))
                break;
            String str = month + context.getString(R.string.pickerview_month) + day + context.getString(R.string.pickerview_day) + " " + getWeek(context, calendar);
            if (now.get(Calendar.YEAR) == y && now.get(Calendar.MONTH) + 1 == month && now.get(Calendar.DAY_OF_MONTH) == day) {
                str = context.getString(R.string.pickerview_today) + " " + getWeek(context, calendar);
            }
            String strLunar = oneDay(context, calendar, false);
            strLunar = strLunar.replace(context.getString(R.string.pickerview_lunar_calendar), "");
            days.add(new ArrayWheelAdapter.BeanForWeeksMode(isLunar, str, strLunar, y, month, day));
        }
        return days;
    }

    public static String getWeek(Context context, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return getWeek(context, c);
    }

    public static String getWeek(Context context, Calendar c) {
        String week = "";
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        String[] weeks = context.getResources().getStringArray(R.array.weeks);
        if (weekday == 1) {
            week = weeks[0];
        } else if (weekday == 2) {
            week = weeks[1];
        } else if (weekday == 3) {
            week = weeks[2];
        } else if (weekday == 4) {
            week = weeks[3];
        } else if (weekday == 5) {
            week = weeks[4];
        } else if (weekday == 6) {
            week = weeks[5];
        } else if (weekday == 7) {
            week = weeks[6];
        }
        return week;
    }

    /**
     * @param hours24
     * @return
     */
    public static String getTimeByHours(Context context, int hours24) {
        String[] datetime = context.getResources().getStringArray(R.array.datetime);
        if (hours24 < 1) {
            return datetime[0];
        } else if (hours24 >= 1 && hours24 < 6) {
            return datetime[1];
        } else if (hours24 >= 6 && hours24 < 12) {
            return datetime[2];
        } else if (hours24 >= 12 && hours24 < 13) {
            return datetime[3];
        } else if (hours24 >= 13 && hours24 < 18) {
            return datetime[4];
        } else if (hours24 >= 18 && hours24 < 19) {
            return datetime[5];
        } else if (hours24 >= 19 && hours24 < 23) {
            return datetime[6];
        }
        return null;
    }

    /**
     * 日期格式显示
     *
     * @param date
     * @param type TYPE_COMMON：1996-11-23 14:58
     *             TYPE_CHINA：1996年11月23日 14时58分
     *             TYPE_CHINA_LUNAR：1996年十月十二 14时58分
     *             TYPE_CHINA_LUNAR_TWELVE：1996年十月十二 半夜 12时58分
     *             TYPE_WEEKS：1996年11月23日 周四
     * @return
     */
    public static String parseDate(Context context, Date date, int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String year = context.getString(R.string.pickerview_year);
        String month = context.getString(R.string.pickerview_month);
        String day = context.getString(R.string.pickerview_day);
        String hours = context.getString(R.string.pickerview_hours);
        String minutes = context.getString(R.string.pickerview_minutes);
        if (type == TYPE_COMMON) {
            return sdf.format(date);
        } else if (type == TYPE_CHINA) {
            sdf = new SimpleDateFormat("yyyy" + year + "MM" + month + "dd" + day + " HH" + hours + "mm" + minutes);
            return sdf.format(date);
        } else if (type == TYPE_CHINA_LUNAR) {
            return oneDay(context, date, true) + " " + new SimpleDateFormat("HH" + hours + "mm" + minutes).format(date);
        } else if (type == TYPE_CHINA_LUNAR_TWELVE) {
            return oneDay(context, date, true) + " " + getTimeByHours(context, date.getHours()) + " " + new SimpleDateFormat("hh" + hours + "mm" + minutes).format(date);
        } else if (type == TYPE_WEEKS) {
            sdf = new SimpleDateFormat("yyyy" + year + "MM" + month + "dd" + day);
            return sdf.format(date) + " " + getWeek(context, date);
        }
        return date.toString();
    }

    public static String parse24Hours(String time12) {
        SimpleDateFormat inSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.ENGLISH);
        try {
            Date date = inSdf.parse(time12);
            String result = WheelTime.dateFormat.format(date);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
