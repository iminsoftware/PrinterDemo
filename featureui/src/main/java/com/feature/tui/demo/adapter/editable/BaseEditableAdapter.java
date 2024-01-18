package com.feature.tui.demo.adapter.editable;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.demo.adapter.editable.inner.IEditableView;
import com.feature.tui.demo.adapter.editable.inner.IEditableViewSelectedListener;
import com.feature.tui.demo.adapter.editable.inner.Selectable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public abstract class BaseEditableAdapter<M extends Selectable, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements IEditableView<VH> {
    public static final int SHOW_MODE = 0;
    public static final int EDIT_MODE = 1;
    public static final int TOUCH_MODE_ROOT = 2;
    public static final int TOUCH_MODE_CHILD = 3;
    public static final int PAYLOAD_CHECKED_CHECK_BOX = 0;
    public static final int PAYLOAD_UNCHECKED_CHECK_BOX = 1;
    // 当前模式
    private int curMode;
    // 存放选中Item数据集
    private final LinkedList<Selectable> selectedList = new LinkedList<>();
    // 存放Item数据集
    private final List<M> mData;
    // 是否允许开启编辑模式，默认是true
    private boolean enableEditMode;
    // 编辑模式下，选中监听器
    private IEditableViewSelectedListener editableViewSelectedListener;
    // 显示模式下，Item点击事件
    private AdapterView.OnItemClickListener onItemClickListener;

    public boolean getEnableEditMode() {
        return this.enableEditMode;
    }

    public void setEnableEditMode(boolean enableEditMode) {
        this.enableEditMode = enableEditMode;
    }

    public IEditableViewSelectedListener getEditableViewSelectedListener() {
        return this.editableViewSelectedListener;
    }

    public void setEditableViewSelectedListener(IEditableViewSelectedListener iEditableViewSelectedListener) {
        this.editableViewSelectedListener = iEditableViewSelectedListener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getItemCount() {
        return mData.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onBindViewHolder(VH vh, int position) {
        processEditOrShow(vh, position);
        onBindData(vh, position, (mData.get(position)));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onBindViewHolder(VH vh, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(vh, position);
            return;
        }
        if (payloads.contains(PAYLOAD_CHECKED_CHECK_BOX) | payloads.contains(PAYLOAD_UNCHECKED_CHECK_BOX)) {
            if (checkEditMode()) {
                CheckBox checkBox = this.getCheckBox(vh);
                checkBox.setChecked((mData.get(position)).isSelected());
            }
        } else {
            processEditOrShow(vh, position);
            onBindData(vh, position, (mData.get(position)), payloads);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processEditOrShow(VH vh, int position) {
        View hideView = getHideView(vh);
        switch (curMode) {
            case EDIT_MODE: {
                View view = hideView;
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
                enterTouchMode(vh, position);
                vh.itemView.setOnLongClickListener((v) -> {
                    M model = mData.get(vh.getAdapterPosition());
                    if (model.isSelected()) {
                        removeItemForSelectedList(model);
                        getCheckBox(vh).setChecked(false);
                    } else {
                        appendItemForSelectedList(model);
                        getCheckBox(vh).setChecked(true);
                    }
                    callbackOnSelectedCountChanged();
                    return true;
                });
                break;
            }
            default: {
                View view = hideView;
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                getCheckBox(vh).setChecked(false);
                vh.itemView.setOnLongClickListener((v) -> {
                    changeMode(EDIT_MODE);
                    callbackOnModelChanged();
                    appendItemForSelectedList(mData.get(position));
                    callbackOnSelectedCountChanged();
                    return true;
                });
            }
        }
    }

    private int getTouchMode() {
        return TOUCH_MODE_ROOT;
    }

    private void enterTouchMode(VH vh, int position) {
        switch (this.getTouchMode()) {
            case TOUCH_MODE_ROOT: {
                this.enterTouchModeRoot(vh, position);
                break;
            }
            case TOUCH_MODE_CHILD: {
                this.enterTouchModeChild(vh, position);
                break;
            }
            default: {

            }
        }
    }

    private void enterTouchModeRoot(VH vh, int position) {
        M model = mData.get(position);
        View view = vh.itemView;
        View itemView = view;
        CheckBox checkBox = getCheckBox(vh);
        checkBox.setClickable(false);
        checkBox.setChecked(model.isSelected());
        itemView.setOnClickListener((v) -> {
            boolean selected = !model.isSelected();
            checkBox.setChecked(selected);
            processSelected(model, selected);
            callbackOnSelectedCountChanged();
        });
    }

    private void enterTouchModeChild(VH vh, int position) {
        CheckBox cb = getCheckBox(vh);
        M model = mData.get(position);
        cb.setClickable(true);
        cb.setChecked(model.isSelected());
        if (curMode == EDIT_MODE) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!buttonView.isPressed()) {
                    return;
                }
                processSelected(model, isChecked);
                callbackOnSelectedCountChanged();
            });
        }
    }

    private void processSelected(M model, boolean selected) {
        if (selected) {
            appendItemForSelectedList(model);
        } else {
            removeItemForSelectedList(model);
        }
    }

    public abstract void onBindData(VH holder, int position, M model);

    public void onBindData(VH holder, int position, M model, List<Object> payloads) {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void restoreUnSelected() {
        if (selectedList.size() > 0) {
            selectedList.forEach((selectable) -> {
                if (selectable.isSelected()) selectable.setSelected(false);
            });
            selectedList.clear();
        }
    }

    // 改变模式
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void changeMode(int mode) {
        if (mode == SHOW_MODE || mode == EDIT_MODE) {
            curMode = mode;
            restoreUnSelected();
            notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onBackBtnClick() {
        if (checkEditMode()) {
            changeMode(SHOW_MODE);
            callbackOnModelChanged();
            return true;
        }
        return false;
    }

    protected boolean checkEditMode() {
        return curMode == EDIT_MODE;
    }

    // 针对特定Item的操作
    private void appendItemForSelectedList(M t) {
        if (!selectedList.contains(t)) {
            t.setSelected(true);
            selectedList.add(t);
        }
    }

    private boolean removeItemForSelectedList(M t) {
        boolean result = selectedList.remove(t);
        if (result) {
            t.setSelected(false);
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectAll() {
        if (mData.size() > 0 && checkEditMode()) {
            for (int i = 0; i < mData.size(); i++) {
                appendItemForSelectedList(mData.get(i));
                notifyItemChanged(i, PAYLOAD_CHECKED_CHECK_BOX);
            }
            callbackOnSelectedCountChanged();
        }
    }

    public void unSelectAll() {
        if (mData.size() > 0 && checkEditMode()) {
            restoreUnSelected();
            for (int i = 0; i < mData.size(); i++) {
//                appendItemForSelectedList(mData.get(i));
                notifyItemChanged(i, PAYLOAD_UNCHECKED_CHECK_BOX);
            }
            callbackOnSelectedCountChanged();
        }
    }

    // 针对选中集合的操作
    public void removeSelectedItem() {
        int size = mData.size();
        if (size > 0 && this.checkEditMode()) {
            Selectable item = null;
            for (int i = size - 1; i >= 0; i--) {
                item = mData.get(i);
                if (selectedList.contains(item)) {
                    item.setSelected(false);
                    mData.remove(item);
                    selectedList.remove(item);
                    removeItem(i);
                }
            }
            callbackOnSelectedCountChanged();
        }
    }

    private void removeItem(int pos) {
        this.notifyItemRemoved(pos);
        if (pos < this.getItemCount()) {
            notifyItemRangeChanged(pos, this.getItemCount() - pos);
        }
    }

    public int getSelectedItemCount() {
        return checkEditMode() ? selectedList.size() : 0;
    }

    public boolean isSelectedAll() {
        return selectedList.size() == mData.size();
    }

    public void addData(List<? extends M> newData) {
        Collection collection = newData;
        if (!collection.isEmpty()) {
            mData.addAll(newData);
            notifyItemRangeChanged(mData.size() - newData.size(), newData.size());
        }
    }

    public void updateData(List<? extends M> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
        if (pos < getItemCount()) {
            notifyItemRangeChanged(pos, getItemCount() - pos);
        }
    }

    public void add(int pos, M item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
        if (pos < getItemCount()) {
            notifyItemRangeChanged(pos, getItemCount() - pos);
        }
    }

    private void callbackOnSelectedCountChanged() {
        if (editableViewSelectedListener != null)
            editableViewSelectedListener.onSelectedCountChanged(selectedList.size());
    }

    private void callbackOnModelChanged() {
        if (editableViewSelectedListener != null)
            editableViewSelectedListener.onEditableModeChanged(curMode == EDIT_MODE);
    }

    public BaseEditableAdapter(Context context) {
        mData = new ArrayList();
        enableEditMode = true;
    }

}
