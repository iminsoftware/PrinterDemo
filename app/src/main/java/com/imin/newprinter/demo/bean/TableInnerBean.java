package com.imin.newprinter.demo.bean;

/**
 * @Author: Mark
 * @date: 2024/1/3 Time：15:59
 * @description:
 */
public class TableInnerBean {

    private String content = "test";
    private int weight = 1;
    private int align = 0;
    private int size = 24;


    public TableInnerBean() {

    }

    public TableInnerBean(int itemCount) {

    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getAlign() {
        return align;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }


    @Override
    public String toString() {
        return "TableBean{" +
                "content='" + content + '\'' +
                ", weight=" + weight +
                ", align=" + align +
                ", size=" + size +
                '}';
    }
}
