package com.feature.tui.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataBindingAdapter<M, VH extends BaseDataBindingAdapter.BaseBindingViewHolder>
        extends ListAdapter<M, RecyclerView.ViewHolder> {
    private OnItemClickListener<M> onItemClickListener;
    private OnItemLongClickListener<M> onItemLongClickListener;
    protected final Context context;
    private List list = null;

    public final OnItemClickListener<M> getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public final void setOnItemClickListener(OnItemClickListener<M> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public final OnItemLongClickListener<M> getOnItemLongClickListener() {
        return this.onItemLongClickListener;
    }

    public final void setOnItemLongClickListener(OnItemLongClickListener<M> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public abstract VH createMyViewHolder(View view, ViewGroup parent, int viewType);

    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(getLayoutId(viewType), parent, false);
        VH vh = createMyViewHolder(view, parent, viewType);
        vh.itemView.setOnClickListener((v) -> {
            int pos = vh.getAdapterPosition();
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(vh.itemView, getItem(pos), pos);
        });
        vh.itemView.setOnLongClickListener((v) -> {
            if (onItemLongClickListener == null)
                return false;
            int pos = vh.getAdapterPosition();
            return onItemLongClickListener.onItemLongClick(vh.itemView, getItem(pos), pos);
        });
        return vh;
    }

    @LayoutRes
    public abstract int getLayoutId(int var1);

    public void onBindItem(VH holder, M item, int position) {
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        {
            onBindItem((VH) holder, getItem(position), position);
        }
    }


    public void submitList(List<M> list) {
        if (list == null) {
            list = new ArrayList();
        }
        this.list = list;
        super.submitList(list);
    }

    public void removeItem(M o) {
        list.remove(o);
        submitList(list);
        notifyDataSetChanged();
    }

    public BaseDataBindingAdapter(Context context, DiffUtil.ItemCallback<M> diffCallback) {
        super(diffCallback);
        this.context = context;
    }

    public static class BaseBindingViewHolder
            extends RecyclerView.ViewHolder {
        public BaseBindingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<M> {
        void onItemClick(View view, M item, int position);
    }

    public interface OnItemLongClickListener<M> {
        boolean onItemLongClick(View view, M item, int position);
    }

}
