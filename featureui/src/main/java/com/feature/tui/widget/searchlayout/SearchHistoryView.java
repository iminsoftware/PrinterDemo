package com.feature.tui.widget.searchlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feature.tui.R;

import java.util.ArrayList;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/26 18:17
 */
public class SearchHistoryView extends RelativeLayout {

    private LayoutInflater mLayoutInflater;
    private SearchFlowLayout mFlowLayout;
    private SearchCallback callback;
    private Context context;

    public SearchHistoryView(Context context) {
        this(context,null);
    }

    public SearchHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addChild();

    }

    void addChild() {
        mLayoutInflater = LayoutInflater.from(context);

        View view = View.inflate(context, R.layout.layout_search_history, null);
        addView(view);

        mFlowLayout = view.findViewById(R.id.fl_tag);
        view.findViewById(R.id.clear).setOnClickListener(v -> {
            if(mFlowLayout != null){
                mFlowLayout.removeAllViews();
            }
        });
    }

    public void setClickCallback(SearchCallback searchCallback) {
        this.callback = searchCallback;
    }

    public void setTags(ArrayList<String> tags) {
        for (String str: tags) {
            addTags(str);
        }
    }

    public void addTags(String tag) {
        TextView tv = (TextView) mLayoutInflater.inflate(
                R.layout.search_label_tv, mFlowLayout, false
        );
        tv.setText(tag);
        tv.setOnClickListener(v -> {
            if(callback != null){
                callback.onSearch(tag);
            }
        });
        mFlowLayout.addView(tv);
    }

    public SearchFlowLayout getmFlowLayout() {
        return mFlowLayout;
    }
}
