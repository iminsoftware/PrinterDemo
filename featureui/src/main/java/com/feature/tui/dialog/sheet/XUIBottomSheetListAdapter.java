package com.feature.tui.dialog.sheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class XUIBottomSheetListAdapter<M>
        extends RecyclerView.Adapter<XUIBottomSheetListAdapter.VH> {
    private View mHeaderView;
    private View mFooterView;
    private List<M> mData;
    private OnItemClickListener<M> onItemClickListener;
    private LayoutInflater inflater;
    private int layoutID;
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_FOOTER = 2;
    public static final int ITEM_TYPE_NORMAL = 3;

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH vH;
        switch (viewType) {
            case ITEM_TYPE_HEADER: {
                vH = new VH(mHeaderView);
                break;
            }
            case ITEM_TYPE_FOOTER: {
                vH = new VH(mFooterView);
                break;
            }
            default: {
                View view = this.inflater.inflate(layoutID, parent, false);
                VH vh = new VH(view);
                view.setOnClickListener((v) -> {
                    int adapterPos = vh.getAdapterPosition();
                    int dataPosition = mHeaderView != null ? adapterPos - 1 : adapterPos;
                    if (onItemClickListener != null)
                        onItemClickListener.onClick(dataPosition, mData.get(dataPosition));
                });
                vH = vh;
            }
        }
        return vH;
    }

    public void onBindViewHolder(VH holder, int position) {
        if (holder.getItemViewType() != ITEM_TYPE_NORMAL) {
            return;
        }
        int newPosition = position;
        if (mHeaderView != null) {
            newPosition = position - 1;
        }
        onBindContentDataToView(holder, newPosition, mData.get(newPosition));
    }

    public abstract void onBindContentDataToView(VH holder, int position, M model);

    public int getItemCount() {
        return mData.size() + (mHeaderView != null ? 1 : 0) + (mFooterView != null ? 1 : 0);
    }

    public void setOnItemClickListener(OnItemClickListener<M> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return ITEM_TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && mFooterView != null) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }

    public final void setHeaderFooterView(View headerView, View footerView) {
        this.mHeaderView = headerView;
        this.mFooterView = footerView;
    }

    public final void setData(List<? extends M> list) {
        mData.clear();
        mData.addAll(list);
    }

    public XUIBottomSheetListAdapter(Context context, int layoutID) {
        this.layoutID = layoutID;
        this.mData = new ArrayList();
        this.inflater = LayoutInflater.from((Context) context);
    }


    public static final class VH
            extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }

    public static interface OnItemClickListener<M> {
        public void onClick(int var1, M var2);
    }

}
