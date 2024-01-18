package com.feature.tui.modle;

/**
 * Author: mark
 * Time: 2020/12/17 16:37
 * Description: 类或接口功能描述
 */
public class DialogItemDescription {
    private String title;
    private String description;
    private int resId = 0;
    private boolean isChecked = false;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResId() {
        return this.resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public DialogItemDescription(String title, String description, int resId, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.resId = resId;
        this.isChecked = isChecked;
    }

    public DialogItemDescription(String title, String description, boolean isChecked) {
        this(title, description, 0, isChecked);
    }

    public DialogItemDescription(String title, String description) {
        this(title, description, 0, false);
    }

    public DialogItemDescription(String title, boolean isChecked) {
        this(title, "", 0, isChecked);
    }

    public DialogItemDescription(String title, int resId) {
        this(title, "", resId, false);
    }

    public DialogItemDescription(String title) {
        this(title, "", 0, false);
    }

    @Override
    public String toString() {
        return "DialogItemDescription{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", resId=" + resId +
                ", isChecked=" + isChecked +
                '}';
    }
}

