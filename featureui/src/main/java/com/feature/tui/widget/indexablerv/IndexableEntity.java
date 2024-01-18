package com.feature.tui.widget.indexablerv;

/**
 * Created by YoKey on 16/10/9.
 */
public interface IndexableEntity {

    String getFieldIndexBy();

    void setFieldIndexBy(String indexField);

    void setFieldPinyinIndexBy(String pinyin);

    /**
     * 给adapter设置数据，排序完成后调用
     *
     * @param isFirstInGroup 是否为字母组中的第一个元素
     * @param isLastInGroup  是否为字母组中的最后一个元素
     */
    default void onSortFinished(boolean isFirstInGroup, boolean isLastInGroup) {
    }
}
