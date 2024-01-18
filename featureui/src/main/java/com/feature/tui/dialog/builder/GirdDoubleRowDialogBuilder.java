package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.dialog.adapter.DialogGridContentAdapter;
import com.feature.tui.dialog.center.DialogGirdPagerView;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.util.XUiDisplayHelper;

import java.util.ArrayList;
import java.util.List;

public class GirdDoubleRowDialogBuilder
        extends BaseChoiceDialogBuilder<GirdDoubleRowDialogBuilder> {
    private DialogItemDescription checkedItem;
    private DialogGirdPagerView girdPagerView;

    private int spanCount = 4;
    private final int ROW_COUNT = 2;
    private int pageListCount = spanCount * ROW_COUNT;

    private BaseDataBindingAdapter.OnItemClickListener<DialogItemDescription> itemClickListener = (view, item, position) -> {
        int currentItem = girdPagerView.getCurrentItem();
        itemClick(view, item, pageListCount * currentItem + position);
        if (getOnClickListener() != null)
            getOnClickListener().invoke(getMDialog(), pageListCount * currentItem + position);
    };

    @Override
    protected BaseDataBindingAdapter.OnItemClickListener<DialogItemDescription> getItemClickListener() {
        return itemClickListener;
    }

    @Override
    protected void setCheckedItems(List<DialogItemDescription> listData) {
    }

    @Override
    protected void itemClick(View view, DialogItemDescription item, int position) {
        checkedItem = item;
    }

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        pageListCount = spanCount * ROW_COUNT;
        {
            if (getListData() == null) {
                return null;
            }
            int pageSize = getListData().size() / pageListCount;
            if (getListData().size() % pageListCount != 0) {
                ++pageSize;
            }
            girdPagerView = new DialogGirdPagerView(context, pageSize > 1);
            ArrayList<RecyclerView> list = new ArrayList<RecyclerView>();
            int i = 0;
            while (i < pageSize) {
                if ((i + 1) * pageListCount <= getListData().size()) {
                    list.add(createRecyclerView(context, getListData().subList(i * pageListCount, (i + 1) * pageListCount)));
                } else {
                    list.add(createRecyclerView(context, getListData().subList(i * pageListCount, getListData().size())));
                }
                i++;
            }
            girdPagerView.setListData(list);
        }
        return girdPagerView;
    }

    private RecyclerView createRecyclerView(Context context, List<DialogItemDescription> data) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setMAdapter(new DialogGridContentAdapter(context, getItemCallback()));
        ((DialogGridContentAdapter) getMAdapter()).setTitleTextSize(getTitleTextSize());
        getMAdapter().setOnItemClickListener(getItemClickListener());
        recyclerView.setAdapter(getMAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        recyclerView.setPadding(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_8), 0, XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_8), 0);
        getMAdapter().submitList(data);
        return recyclerView;
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_10);
        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_10);
        return layoutParams;
    }


    public DialogItemDescription getCheckedItem() {
        return checkedItem;
    }

    public GirdDoubleRowDialogBuilder(Context context) {
        super(context, false);
    }

    public GirdDoubleRowDialogBuilder setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        return this;
    }

}
