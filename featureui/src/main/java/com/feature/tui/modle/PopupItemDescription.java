package com.feature.tui.modle;


/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/28 9:14
 */
public class PopupItemDescription {
    private String title;
    private int resId;
    private boolean isEnable = true;

    public String getTitle() {
        return this.title;
    }

    public int getResId() {
        return this.resId;
    }

    public void setResId(int var1) {
        this.resId = var1;
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    public void setEnable(boolean var1) {
        this.isEnable = var1;
    }

    public PopupItemDescription(String title, int resId, boolean isEnable) {
        this.title = title;
        this.resId = resId;
        this.isEnable = isEnable;
    }

    public PopupItemDescription(String title) {
        this.title = title;
    }

    public PopupItemDescription(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public PopupItemDescription(String title, boolean isEnable) {
        this.title = title;
        this.isEnable = isEnable;
    }

    @Override
    public String toString() {
        return "PopupItemDescription{" +
                "title='" + title + '\'' +
                ", resId=" + resId +
                ", isEnable=" + isEnable +
                '}';
    }
}
