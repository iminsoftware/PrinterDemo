package com.feature.tui.popup;

import android.content.Context;
import android.view.View;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.dialog.Functions;
import com.feature.tui.widget.layout.MaxRecyclerView;
import com.feature.tui.widget.recyclerview.decoration.ListDividerItemDecoration;

import java.util.List;

/**
 * @Author: zhongxing
 * @Date: 2021/7/15 14:15
 * @Description:
 */
public class SimpleCornerPopup extends ListPopup {

    private SimpleCornerAdapter adapter;

    public SimpleCornerPopup(Context context) {
        super(context);
    }

    public SimpleCornerPopup(Context context, int width, int maxHeight) {
        super(context, width, maxHeight);
    }

    public SimpleCornerPopup(Context context, int width) {
        super(context, width);
    }

    public SimpleCornerPopup setItems(List listData, Functions.Fun4 onClickListener) {
        super.setItems(listData, onClickListener);
        return this;
    }

    public SimpleCornerPopup updateData(List listData) {
        this.listData = listData;
        if (adapter != null)
            adapter.submitList(listData);
        return this;
    }

    @Override
    protected View onCreateContent(Context context) {
        MaxRecyclerView view = (MaxRecyclerView) super.onCreateContent(context);
        view.addItemDecoration(new ListDividerItemDecoration(mContext, ListDividerItemDecoration.VERTICAL_LIST));
        return view;
    }

    @Override
    protected BaseDataBindingAdapter createAdapter() {
        adapter = new SimpleCornerAdapter(mContext, itemCallback, R.layout.popup_list_item, this.itemHeight);
        return adapter;
    }

}
