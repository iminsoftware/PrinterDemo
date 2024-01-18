package com.feature.tui.widget.inputlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.feature.tui.R;
import com.feature.tui.R.drawable;

import java.lang.reflect.Field;
import java.util.List;

public class ContentAdapterForEdit extends ArrayAdapter<String> implements View.OnClickListener {

    private boolean isCanDelete;

    public ContentAdapterForEdit(@NonNull Context context, @NonNull List<String> objects, boolean isCanDelete) {
        super(context, 0, objects);
        this.isCanDelete = isCanDelete;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.popup_list_item_img_right, null);
        }
        TextView popupItemTitle = convertView.findViewById(R.id.popup_item_title);
        ImageView popupItemIcon = convertView.findViewById(R.id.popup_item_icon);
        View viewDivider = convertView.findViewById(R.id.view_divider);
        popupItemTitle.setText(getItem(position));
        if (position == 0) {
            convertView.setBackgroundResource(drawable.list_item_bg_top);
        } else if (position == getCount() - 1) {
            convertView.setBackgroundResource(drawable.list_item_bg_bottom);
        } else {
            convertView.setBackgroundResource(drawable.list_item_bg);
        }

        if (position == 0 && getCount() == 1) {
            convertView.setBackgroundResource(drawable.list_item_bg_all);
        }

        popupItemIcon.setVisibility(isCanDelete ? View.VISIBLE : View.GONE);
        popupItemIcon.setTag(getItem(position));
        popupItemIcon.setOnClickListener(this);

        viewDivider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
        return convertView;
    }

    @Override
    public void remove(@Nullable String object) {
        List<String> list = getList("mOriginalValues");
        if (list != null)
            list.remove(object);
        list = getList("mObjects");
        if (list != null)
            list.remove(object);
        notifyDataSetChanged();
    }

    public List<String> getList(String name) {
        Field field = null;
        List<String> list = null;
        try {
            field = getClass().getSuperclass().getDeclaredField(name);
            field.setAccessible(true);
            list = (List<String>) field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        remove((String) v.getTag());
    }

}
