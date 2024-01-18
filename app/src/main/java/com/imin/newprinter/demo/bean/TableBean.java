package com.imin.newprinter.demo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mark
 * @date: 2024/1/3 Time：15:59
 * @description:
 */
public class TableBean {

    private List<TableInnerBean> innerBeanList;

    private int itemCount;

    public TableBean() {

    }

    public TableBean(int itemCount) {
        this.itemCount = itemCount;
        innerBeanList = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            innerBeanList.add(new TableInnerBean());
        }
    }

    public List<TableInnerBean> getInnerBeanList() {
        return innerBeanList;
    }

    public void setInnerBeanList(List<TableInnerBean> innerBeanList) {
        this.innerBeanList = innerBeanList;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        innerBeanList = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            innerBeanList.add(new TableInnerBean());
        }
    }


    @Override
    public String toString() {
        return "TableBean{" +
                "itemCount='" + itemCount + '\'' +
//                ", weight=" + weight +

                '}';
    }
}
