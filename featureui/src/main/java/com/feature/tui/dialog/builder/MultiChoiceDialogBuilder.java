package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.View;

import com.feature.tui.modle.DialogItemDescription;

import java.util.ArrayList;
import java.util.List;

public class MultiChoiceDialogBuilder
        extends BaseChoiceDialogBuilder<MultiChoiceDialogBuilder> {
    private List<DialogItemDescription> checkedItems;

    protected void setCheckedItems(List<DialogItemDescription> listData) {
        if (listData == null || listData.isEmpty()) {
            return;
        }
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).isChecked())
                checkedItems.add(listData.get(i));
        }
    }

    public void setAllStatus(boolean isCheck) {
        getListData().forEach(item -> {
                    item.setChecked(isCheck);
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
        );
    }

    protected void itemClick(View view, DialogItemDescription item, int position) {
        item.setChecked(!item.isChecked());
        if (checkedItems.contains(item)) {
            checkedItems.remove(item);
        } else {
            checkedItems.add(item);
        }
        if (mAdapter != null)
            mAdapter.notifyItemChanged(position);
    }

    /**
     * 获取选中的条目
     */
    public List<DialogItemDescription> getCheckedItems() {
        return checkedItems;
    }

    public MultiChoiceDialogBuilder(Context context) {
        super(context, false);
        checkedItems = new ArrayList();
    }

}
