package com.imin.newprinter.demo.bean;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：18:11
 * @description:
 */
public class FunctionTestBean {

    private String title;
    private String value;
    private int imageResource;

    private int itemCount;
    private Class fragment;

    public FunctionTestBean(String title) {

        this.title = title;
        this.value = value;
    }

    public FunctionTestBean(String title,int resource) {

        this.title = title;
        this.imageResource = resource;
    }

    public FunctionTestBean(int itemCount, String title) {

        this.title = title;
        this.itemCount = itemCount;
    }

    public FunctionTestBean(String title, String value) {

        this.title = title;
        this.value = value;
    }

    public FunctionTestBean(Class fragment, String title,int resource) {

        this.fragment = fragment;
        this.title = title;
        this.imageResource = resource;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "FunctionTestBean{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                ", imageResource=" + imageResource +
                ", itemCount=" + itemCount +
                ", fragment=" + fragment +
                '}';
    }
}
