package com.imin.newprinter.demo.bean;

import android.graphics.Bitmap;

/**
 * @Author: hy
 * @date: 2024/9/24
 * @description:
 */
public class LabelTitleBean {
    int id;
    String title;
    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public LabelTitleBean(int id, String title, int width, int height, Bitmap iMage) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
        this.iMage = iMage;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public LabelTitleBean() {
    }

    public LabelTitleBean(int id, String title, Bitmap iMage) {
        this.id = id;
        this.title = title;
        this.iMage = iMage;
    }

    Bitmap iMage;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    boolean select = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getiMage() {
        return iMage;
    }

    public void setiMage(Bitmap iMage) {
        this.iMage = iMage;
    }
}
