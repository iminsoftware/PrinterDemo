package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;
import com.feature.tui.dialog.adapter.DialogGridContentAdapter;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.modle.DialogItemDescription;
import com.feature.tui.util.XUiDisplayHelper;

import java.util.List;

public class GirdSingleRowDialogBuilder
        extends BaseChoiceDialogBuilder<GirdSingleRowDialogBuilder> {
    private DialogItemDescription checkedItem;

    @Override
    protected void setCheckedItems(List<DialogItemDescription> listData) {
    }

    @Override
    protected void itemClick(View view, DialogItemDescription item, int position) {
        checkedItem = item;
    }

    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        int itemMarginHor = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
        if (getListData() != null && getListData().size() < 4) {
            itemMarginHor = context.getResources().getDimensionPixelSize(R.dimen.dp_16);
        }
        setMAdapter(new DialogGridContentAdapter(context, getItemCallback(), R.layout.dialog_grid_item_layout, itemMarginHor, itemMarginHor));
        ((DialogGridContentAdapter) getMAdapter()).setTitleTextSize(getTitleTextSize());
        getMAdapter().setOnItemClickListener(getItemClickListener());
        recyclerView.setAdapter(getMAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        getMAdapter().submitList(getListData());
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(XUiDisplayHelper.dp2px(context, 60) * getListData().size(), ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(recyclerView);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        return linearLayout;
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

    public GirdSingleRowDialogBuilder(Context context) {
        super(context, false);
    }
}
