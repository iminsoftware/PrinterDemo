package com.feature.tui.demo.adapter.editable;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.R;
import com.feature.tui.dialog.Functions;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.widget.recyclerview.XUIRVItemSwipeAction;
import com.feature.tui.widget.recyclerview.XUISwipeAction;
import com.feature.tui.widget.recyclerview.XUISwipeViewHolder;


import java.util.ArrayList;
import java.util.List;

public abstract class BaseSwipeActionAdapter<M extends BaseSwipeActionAdapter.BaseModel, VH extends XUISwipeViewHolder>
        extends RecyclerView.Adapter<VH> {
    private final XUISwipeAction mDeleteAction;
    private final List<M> mData;
    private final Context context;
    private XUIRVItemSwipeAction swipeAction;
    private Functions.Fun2<M> mOnClickActionListener;

    protected List<M> getMData() {
        return mData;
    }

    public void onBindViewHolder(VH vh, int position) {
        setSwipeAction(vh);
    }

    public void onBindViewHolder(VH vh, int position, List<Object> payloads) {
        setSwipeAction(vh);
    }


    public boolean isOpenDelete() {
        return swipeAction.mSelected != null;
    }

    public void clear() {
        swipeAction.clear();
    }

    private void setSwipeAction(VH vh) {
        if (!vh.hasAction()) {
            vh.addSwipeAction(this.mDeleteAction);
        }
    }

    protected boolean isSwipeDeleteWhenOnlyOneAction() {
        return false;
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        swipeAction = new XUIRVItemSwipeAction(isSwipeDeleteWhenOnlyOneAction(), new SwipeActionCallback());
        swipeAction.attachToRecyclerView(recyclerView);
    }

    public int getItemCount() {
        return this.mData.size();
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

    public void addData(List<? extends M> newData) {
        if (!newData.isEmpty()) {
            mData.addAll(newData);
            notifyItemRangeChanged(mData.size() - newData.size(), newData.size());
        }
    }

    public void updateData(List<? extends M> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnClickActionListener(Functions.Fun2<M> callback) {
        mOnClickActionListener = callback;
    }

    public BaseSwipeActionAdapter(Context context) {
        this.context = context;
        this.mData = new ArrayList();
        XUISwipeAction.ActionBuilder builder = new XUISwipeAction.ActionBuilder().textSize(XUiDisplayHelper.sp2px(this.context, 14)).
                textColor(Color.WHITE).paddingStartEnd(XUiDisplayHelper.dp2px(this.context, 14));
        int bgColor = context.getResources().getColor(R.color.xui_config_color_error);
        XUISwipeAction mUISwipeAction = builder.text(context.getString(R.string.xui_delete)).backgroundColor(bgColor).build();
        mDeleteAction = mUISwipeAction;
    }

    public class SwipeActionCallback
            extends XUIRVItemSwipeAction.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            remove(viewHolder.getAdapterPosition());
        }

        public int getSwipeDirection(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return (getMData().get(viewHolder.getAdapterPosition())).getEnableSwipeDelete() ?
                    XUIRVItemSwipeAction.SWIPE_LEFT : XUIRVItemSwipeAction.SWIPE_NONE;
        }

        public void onClickAction(XUIRVItemSwipeAction swipeAction, RecyclerView.ViewHolder selected, XUISwipeAction action) {
            if (action == mDeleteAction) {
                int aPosition = selected.getAdapterPosition();
                if (aPosition >= 0) {
                    M item = getMData().get(aPosition);
                    remove(aPosition);
                    if (mOnClickActionListener != null) {
                        mOnClickActionListener.invoke(aPosition, item);
                    }
                }
            } else {
                swipeAction.clear();
            }
        }
    }

    public static class BaseModel {
        private boolean enableSwipeDelete = false;

        public boolean getEnableSwipeDelete() {
            return this.enableSwipeDelete;
        }

        public void setEnableSwipeDelete(boolean bl) {
            this.enableSwipeDelete = bl;
        }

        public BaseModel(boolean enableSwipeDelete) {
            this.enableSwipeDelete = enableSwipeDelete;
        }

        public BaseModel() {

        }
    }

}
