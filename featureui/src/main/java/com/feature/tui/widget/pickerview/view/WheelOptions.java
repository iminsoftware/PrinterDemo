package com.feature.tui.widget.pickerview.view;

import android.graphics.Typeface;
import android.view.View;

import com.feature.tui.R;
import com.feature.tui.widget.pickerview.adapter.ArrayWheelAdapter;
import com.feature.tui.widget.pickerview.listener.OnItemSelectedListener;
import com.feature.tui.widget.pickerview.listener.OnOptionsSelectChangeListener;

import java.util.List;

/**
 * @author mark
 * Time: 2020/12/24 10:35
 * Description:WheelOptions的管理类
 */
public class WheelOptions {
    private View view;
    private WheelView mWvOption1;
    private WheelView mWvOption2;
    private WheelView mWvOption3;

    private List mOptionsItems;

    /**
     * 默认联动
     */
    private boolean linkage = true;
    /**
     * 切换时，还原第一项
     */
    private boolean isRestoreItem;
    private OnItemSelectedListener mWheelListenerOption2;

    private OnOptionsSelectChangeListener optionsSelectChangeListener;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelOptions(View view, boolean isRestoreItem) {
        super();
        this.isRestoreItem = isRestoreItem;
        this.view = view;
        // 初始化时显示的数据
        mWvOption1 = (WheelView) view.findViewById(R.id.options1);
        mWvOption2 = (WheelView) view.findViewById(R.id.options2);
        mWvOption3 = (WheelView) view.findViewById(R.id.options3);

    }


    /**
     * 联动情况下调用
     *
     * @param optionsItems 设置数据源
     */
    public <T> void setPicker(List<T> optionsItems) {
        this.mOptionsItems = optionsItems;
        if (mOptionsItems == null || mOptionsItems.size() == 0) {
            mWvOption1.setVisibility(View.GONE);
            mWvOption2.setVisibility(View.GONE);
            mWvOption3.setVisibility(View.GONE);
            return;
        }

        // 选项1
        // 设置显示数据
        mWvOption1.setAdapter(new ArrayWheelAdapter<T>(mOptionsItems));
        // 初始化时显示的数据
        mWvOption1.setCurrentItem(0);

        // 选项2
        List<Object> listBean = null;
        try {
            Object object = mOptionsItems.get(0);
            listBean = (List<Object>) object.getClass().getMethod("getList").invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listBean != null) {
            mWvOption2.setVisibility(View.VISIBLE);
            // 设置显示数据
            mWvOption2.setAdapter(new ArrayWheelAdapter<Object>(listBean));
            // 初始化时显示的数据
            mWvOption2.setCurrentItem(mWvOption2.getCurrentItem());
            // 选项3
            if (listBean.size() > 0) {
                List<Object> listString = null;
                try {
                    Object object = listBean.get(0);
                    listString = (List<Object>) object.getClass().getMethod("getList").invoke(object);
                } catch (Exception e) {
                }
                if (listString != null) {
                    mWvOption3.setVisibility(View.VISIBLE);
                    // 设置显示数据
                    mWvOption3.setAdapter(new ArrayWheelAdapter<Object>(listString));
                    mWvOption3.setCurrentItem(mWvOption3.getCurrentItem());
                } else {
                    mWvOption3.setVisibility(View.GONE);
                }
            }

        } else {
            mWvOption2.setVisibility(View.GONE);
            mWvOption3.setVisibility(View.GONE);
        }

        // 联动监听器
        //只有1级联动数据
        //上一个opt2的选中位置
        //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
        //只有2级联动数据，滑动第1项回调
        OnItemSelectedListener wheelListenerOption1 = index -> {
            int opt2Select = 0;
            List<Object> beanList = null;
            try {
                Object object = mOptionsItems.get(index);
                beanList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (beanList == null || beanList.size() == 0) {//只有1级联动数据
                if (optionsSelectChangeListener != null) {
                    optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), 0, 0);
                }
            } else {
                if (!isRestoreItem) {
                    opt2Select = mWvOption2.getCurrentItem();//上一个opt2的选中位置
                    //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                    opt2Select = Math.min(opt2Select, beanList.size() - 1);
                }
                mWvOption2.setAdapter(new ArrayWheelAdapter<Object>(beanList));
                mWvOption2.setCurrentItem(opt2Select);
                List<Object> stringList = null;
                try {
                    Object object = beanList.get(opt2Select);
                    stringList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (stringList != null && stringList.size() > 0) {
                    mWheelListenerOption2.onItemSelected(opt2Select);
                } else {//只有2级联动数据，滑动第1项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(index, opt2Select, 0);
                    }
                }
            }
        };

        mWheelListenerOption2 = index -> {
            int opt1Select = mWvOption1.getCurrentItem();
            opt1Select = Math.min(opt1Select, mOptionsItems.size() - 1);
            List<Object> beanList = null;
            try {
                Object object = mOptionsItems.get(opt1Select);
                beanList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (beanList != null) {
                index = Math.min(index, beanList.size() - 1);
                List<Object> stringList = null;
                try {
                    Object object = beanList.get(index);
                    stringList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (stringList != null) {

                    int opt3 = 0;
                    if (!isRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt3 = Math.min(mWvOption3.getCurrentItem(), stringList.size() - 1);
                    }
                    mWvOption3.setAdapter(new ArrayWheelAdapter<Object>(stringList));
                    mWvOption3.setCurrentItem(opt3);

                    //3级联动数据实时回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), index, opt3);
                    }
                } else {
                    //只有2级联动数据，滑动第2项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), index, 0);
                    }
                }
            }
        };

        // 添加联动监听
        if (linkage) {
            mWvOption1.setOnItemSelectedListener(wheelListenerOption1);
        }
        if (linkage) {
            mWvOption2.setOnItemSelectedListener(mWheelListenerOption2);
        }
        if (linkage && optionsSelectChangeListener != null) {
            mWvOption3.setOnItemSelectedListener(index -> optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), mWvOption2.getCurrentItem(), index));
        }
    }


    /**
     * 不联动情况下调用
     */
    public <T> void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items) {

        // 选项1
        // 设置显示数据
        mWvOption1.setAdapter(new ArrayWheelAdapter<>(options1Items));
        // 初始化时显示的数据
        mWvOption1.setCurrentItem(0);
        // 选项2
        if (options2Items != null) {
            // 设置显示数据
            mWvOption2.setAdapter(new ArrayWheelAdapter<>(options2Items));
        }
        // 初始化时显示的数据
        mWvOption2.setCurrentItem(mWvOption2.getCurrentItem());
        // 选项3
        if (options3Items != null) {
            // 设置显示数据
            mWvOption3.setAdapter(new ArrayWheelAdapter<>(options3Items));
        }
        mWvOption3.setCurrentItem(mWvOption3.getCurrentItem());

        if (optionsSelectChangeListener != null) {
            mWvOption1.setOnItemSelectedListener(index -> optionsSelectChangeListener.onOptionsSelectChanged(index, mWvOption2.getCurrentItem(), mWvOption3.getCurrentItem()));
        }

        if (options2Items == null) {
            mWvOption2.setVisibility(View.GONE);
        } else {
            mWvOption2.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                mWvOption2.setOnItemSelectedListener(index -> optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), index, mWvOption3.getCurrentItem()));
            }
        }
        if (options3Items == null) {
            mWvOption3.setVisibility(View.GONE);
        } else {
            mWvOption3.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                mWvOption3.setOnItemSelectedListener(index -> optionsSelectChangeListener.onOptionsSelectChanged(mWvOption1.getCurrentItem(), mWvOption2.getCurrentItem(), index));
            }
        }
    }

    /**
     * @param textSizeOut    设置分割线以外文字的大小，为dimension,不是资源id
     * @param textSizeCenter 设置分割线之间的文字的大小，为dimension,不是资源id
     */
    public void setTextSize(int textSizeOut, int textSizeCenter) {
        mWvOption1.setTextSize(textSizeOut, textSizeCenter);
        mWvOption2.setTextSize(textSizeOut, textSizeCenter);
        mWvOption3.setTextSize(textSizeOut, textSizeCenter);

    }

    /**
     * @param gravity 设置内容显示的位置 Gravity.CENTER、Gravity.LEFT、Gravity.RIGHT
     */
    public void setGravity(int gravity) {
        mWvOption1.setGravity(gravity);
        mWvOption2.setGravity(gravity);
        mWvOption3.setGravity(gravity);
    }


    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null) {
            mWvOption1.setLabel(label1);
        }
        if (label2 != null) {
            mWvOption2.setLabel(label2);
        }
        if (label3 != null) {
            mWvOption3.setLabel(label3);
        }
    }

    /**
     * 设置x轴偏移量
     */
    public void setTextXOffset(int xOffsetOne, int xOffsetTwo, int xOffsetThree) {
        mWvOption1.setTextXOffset(xOffsetOne);
        mWvOption2.setTextXOffset(xOffsetTwo);
        mWvOption3.setTextXOffset(xOffsetThree);
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    public void setTypeface(Typeface font) {
        mWvOption1.setTypeface(font);
        mWvOption2.setTypeface(font);
        mWvOption3.setTypeface(font);
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        mWvOption1.setCyclic(cyclic1);
        mWvOption2.setCyclic(cyclic2);
        mWvOption3.setCyclic(cyclic3);
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2。
     * 在快速滑动未停止时，点击确定按钮，会进行判断，如果匹配数据越界，则设为0，防止index出错导致崩溃。
     *
     * @return 索引数组
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = mWvOption1.getCurrentItem();
        List<Object> beanList = null;
        if (mOptionsItems.size() > currentItems[0]) {
            try {
                Object object = mOptionsItems.get(currentItems[0]);
                beanList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (beanList != null) {
            currentItems[1] = mWvOption2.getCurrentItem() > beanList.size() - 1 ? 0 : mWvOption2.getCurrentItem();
            List<Object> stringList = null;
            if (beanList.size() > currentItems[1]) {
                try {
                    Object object = beanList.get(currentItems[1]);
                    stringList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stringList != null) {
                currentItems[2] = mWvOption3.getCurrentItem() > (stringList.size() - 1) ? 0 : mWvOption3.getCurrentItem();
            } else {
                currentItems[2] = mWvOption3.getCurrentItem();
            }
        } else {
            currentItems[1] = mWvOption2.getCurrentItem();
        }

        return currentItems;
    }

    /**
     * 设置当前条目选中的位置
     */
    public void setCurrentItems(int option1, int option2, int option3) {
        if (linkage) {
            itemSelected(option1, option2, option3);
        } else {
            mWvOption1.setCurrentItem(option1);
            mWvOption2.setCurrentItem(option2);
            mWvOption3.setCurrentItem(option3);
        }
    }

    private void itemSelected(int opt1Select, int opt2Select, int opt3Select) {
        if (mOptionsItems != null && mOptionsItems.size() > 0) {
            mWvOption1.setCurrentItem(opt1Select);

            List<Object> beanList = null;
            try {
                Object object = mOptionsItems.get(opt1Select);
                beanList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (beanList != null && beanList.size() > 0) {
                mWvOption2.setAdapter(new ArrayWheelAdapter<Object>(beanList));
                mWvOption2.setCurrentItem(opt2Select);

                List<Object> stringList = null;
                try {
                    Object object = beanList.get(opt2Select);
                    stringList = (List<Object>) object.getClass().getMethod("getList").invoke(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (stringList != null) {
                    mWvOption3.setAdapter(new ArrayWheelAdapter<Object>(stringList));
                    mWvOption3.setCurrentItem(opt3Select);
                }

            }

        }
    }

    /**
     * @param itemHeight 设置一行的高度
     */
    public void setItemHeight(float itemHeight) {
        mWvOption1.setItemHeight(itemHeight);
        mWvOption2.setItemHeight(itemHeight);
        mWvOption3.setItemHeight(itemHeight);
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        mWvOption1.setDividerColor(dividerColor);
        mWvOption2.setDividerColor(dividerColor);
        mWvOption3.setDividerColor(dividerColor);
    }

    /**
     * 设置分割线的宽度
     *
     * @param dividerWidth
     */
    public void setDividerWidth(int dividerWidth) {
        mWvOption1.setDividerWidth(dividerWidth);
        mWvOption2.setDividerWidth(dividerWidth);
        mWvOption3.setDividerWidth(dividerWidth);
    }

    /**
     * 是否有中心区域的分割线，默认false
     */
    public void hasDivider(boolean hasDivider) {
        mWvOption1.hasDivider(hasDivider);
        mWvOption2.hasDivider(hasDivider);
        mWvOption3.hasDivider(hasDivider);
    }


    /**
     * 设置分割线以外文字的颜色
     * 设置分割线之间的文字的颜色
     *
     * @param textColorOut
     * @param textColorCenter
     */
    public void setTextColor(int textColorOut, int textColorCenter) {
        mWvOption1.setTextColor(textColorOut, textColorCenter);
        mWvOption2.setTextColor(textColorOut, textColorCenter);
        mWvOption3.setTextColor(textColorOut, textColorCenter);
    }

    /**
     * Label 是否只显示中间选中项的
     *
     * @param isCenterLabel
     */
    public void isCenterLabel(boolean isCenterLabel) {
        mWvOption1.isCenterLabel(isCenterLabel);
        mWvOption2.isCenterLabel(isCenterLabel);
        mWvOption3.isCenterLabel(isCenterLabel);
    }

    /**
     * 当 isCenterLabel=true时，此值作为文案和单位的间距
     *
     * @param labelPadding
     */
    public void setLabelPadding(int labelPadding) {
        mWvOption1.setLabelPadding(labelPadding);
        mWvOption2.setLabelPadding(labelPadding);
        mWvOption3.setLabelPadding(labelPadding);
    }

    /**
     * 当 isCenterLabel=true时，设置单位的字体的大小
     *
     */
    public void setLabelTextSize(int textSizeLabel) {
        mWvOption1.setLabelTextSize(textSizeLabel);
        mWvOption2.setLabelTextSize(textSizeLabel);
        mWvOption3.setLabelTextSize(textSizeLabel);
    }

    /**
     * 滑动系数，值越大 ，滑动越慢
     */
    public void setSlidingCoefficient(float slidingCoefficient){
        mWvOption1.setSlidingCoefficient(slidingCoefficient);
        mWvOption2.setSlidingCoefficient(slidingCoefficient);
        mWvOption3.setSlidingCoefficient(slidingCoefficient);
    }

    /**
     * 设置选择监听
     *
     * @param optionsSelectChangeListener
     */
    public void setOptionsSelectChangeListener(OnOptionsSelectChangeListener optionsSelectChangeListener) {
        this.optionsSelectChangeListener = optionsSelectChangeListener;
    }

    /**
     * 设置是否联动
     *
     * @param linkage
     */
    public void setLinkage(boolean linkage) {
        this.linkage = linkage;
    }

    /**
     * 设置滚轮的最大可见数目
     */
    public void setItemsVisible(int itemsVisible) {
        mWvOption1.setItemsVisibleCount(itemsVisible);
        mWvOption2.setItemsVisibleCount(itemsVisible);
        mWvOption3.setItemsVisibleCount(itemsVisible);
    }

}
