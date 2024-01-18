package com.feature.tui.widget.pickerview.adapter;

import java.util.List;

/**
 * @author mark
 * Time: 2020/12/23 16:35
 * Description:数组WheelAdapter
 */
public class ArrayWheelAdapter<T> implements WheelAdapter<T> {

    /**
     * items
     */
    private List<T> items;

    /**
     * @param items the items
     */
    public ArrayWheelAdapter(List<T> items) {
        this.items = items;
    }

    @Override
    public T getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    public static class BeanForWeeksMode {
        public boolean isLunar;
        public String text;
        public String textLunar;
        public int year;
        public int month;
        public int day;

        public BeanForWeeksMode(boolean isLunar, String text, String textLunar, int year, int month, int day) {
            this.isLunar = isLunar;
            this.textLunar = textLunar;
            this.text = text;
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public String toString() {
            return isLunar ? textLunar : text;
        }
    }

    public List<T> getItems() {
        return items;
    }

}
