package com.imin.newprinter.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imin.newprinter.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: hy
 * @date: 2025/4/27
 * @description:
 */
public class ListAdapter extends BaseAdapter {
    private List<String> list = new ArrayList<>();
    private Context context;

    public ListAdapter(Context mContext,List<String> list) {
        this.context = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list == null?null:list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
        TextView textView = convertView.findViewById(R.id.listTv);
        if (list != null){
            textView.setText(list.get(i));
        }
        return convertView;
    }
}
