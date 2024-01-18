package com.feature.tui.modle;

import java.util.List;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/28 9:07
 */
public class OptionsBean {

    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */
    public String name = null;
    public List<ListBean> list = null;

    @Override
    public String toString() {
        return name;
    }

    public List<ListBean> getList(){
        return list;
    }

    public static class ListBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */
        public String name = null;
        public List<String> list = null;

        @Override
        public String toString() {
            return name;
        }

        public List<String> getList(){
            return list;
        }
    }
}
