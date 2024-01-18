// HorizontalTabItemKt.java
package com.feature.tui.widget.horizontaltablist;

import androidx.annotation.NonNull;

import com.feature.tui.demo.adapter.editable.inner.Selectable;

/**
 * Author:LiangJiaXin
 * Time:2021/1/6 13:45
 * Description:类或接口功能描述
 **/

public class HorizontalTabItem extends Selectable {

    public static final int SECONDARY_ITEM0 = 0xf0;
    public static final int SECONDARY_ITEM1 = 0xf1;
    public static final int SECONDARY_ITEM2 = 0xf2;
    public static final int SECONDARY_ITEM3 = 0xf3;

    private final int type;
    private final int icon;
    private final String name;
    private final String name2;

    public final int getType() {
        return this.type;
    }

    public final int getIcon() {
        return this.icon;
    }

    public final String getName() {
        return this.name;
    }

    public final String getName2() {
        return this.name2;
    }

    public HorizontalTabItem(int type, int icon, @NonNull String name, @NonNull String name2) {
        this.type = type;
        this.icon = icon;
        this.name = name;
        this.name2 = name2;
    }

    @NonNull
    public String toString() {
        return "HorizontalTabItem(type=" + this.type + ", icon=" + this.icon + ", name=" + this.name + ", name2=" + this.name2 + ")";
    }

}
