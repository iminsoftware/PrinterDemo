package com.feature.tui.dialog.builder;

import android.content.Context;
import android.view.View;

import com.feature.tui.modle.DialogItemDescription;

import java.util.List;

public class SingleChoiceDialogBuilder
        extends BaseChoiceDialogBuilder<SingleChoiceDialogBuilder> {
    private DialogItemDescription checkedItem;
    private int checkedPosition;
    private boolean hasIcon;

    protected void setCheckedItems(List<DialogItemDescription> listData) {
        if (listData == null || listData.isEmpty()) {
            return;
        }
        for (int i = 0; i < listData.size(); i++) {
            DialogItemDescription item = listData.get(i);
            if (item.isChecked()) {
                checkedItem = item;
                checkedPosition = i;
                break;
            }
        }
    }

    protected void itemClick(View view, DialogItemDescription item, int position) {
        if (checkedItem == null) {
            item.setChecked(true);
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position);
            }
            checkedItem = item;
            checkedPosition = position;
        } else if (checkedItem == item) {
            item.setChecked(false);
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position);
            }
            checkedItem = null;
        } else {
            item.setChecked(true);
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position);
            }
            if (checkedItem != null) {
                checkedItem.setChecked(false);
            }
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(this.checkedPosition);
            }
            checkedItem = item;
            checkedPosition = position;
        }
    }

    public DialogItemDescription getCheckedItem() {
        return checkedItem;
    }

    public SingleChoiceDialogBuilder(Context context, boolean hasIcon) {
        super(context, hasIcon);
        this.hasIcon = hasIcon;
        this.checkedPosition = -1;
    }

    public SingleChoiceDialogBuilder(Context context) {
        this(context, false);
    }

}
