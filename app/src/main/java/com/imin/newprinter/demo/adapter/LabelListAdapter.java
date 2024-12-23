package com.imin.newprinter.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.bean.LabelTitleBean;

import java.util.List;

public class LabelListAdapter extends RecyclerView.Adapter<LabelListAdapter.ViewHolder> {
    List<LabelTitleBean> list;
    public LabelListAdapter() {
    }

    public void setList(List<LabelTitleBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label_rv,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null){
            LabelTitleBean bean = list.get(position);
            if (bean != null) {

                ViewGroup.LayoutParams layoutParams = holder.itemIv.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.itemIv.setLayoutParams(layoutParams);
                holder.itemIv.setImageBitmap(bean.getiMage());

                holder.itemText.setText(bean.getTitle());
            }
            if (listener != null){
                listener.onItemClick(bean,position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list== null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemIv;
        TextView itemText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIv = itemView.findViewById(R.id.itemIv);
            itemText = itemView.findViewById(R.id.itemText);
        }
    }

    OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         * @param position The position of the view in the adapter.
         */
        void onItemClick(LabelTitleBean bean, int position);
    }


}
