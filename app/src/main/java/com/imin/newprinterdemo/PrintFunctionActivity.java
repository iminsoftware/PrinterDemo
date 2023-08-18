package com.imin.newprinterdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PrintFunctionActivity extends BaseActivity {

    private BaseQuickAdapter<FunctionBean, BaseViewHolder> adapter;
    private final List<FunctionBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_function);
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        list.add(new FunctionBean(1, getString(R.string.function_all), R.mipmap.ic_all));
        list.add(new FunctionBean(2, getString(R.string.function_qrcode), R.mipmap.ic_qrcode));
        list.add(new FunctionBean(3, getString(R.string.function_barcode), R.mipmap.ic_barcode));
        list.add(new FunctionBean(5, getString(R.string.function_text_pic), R.mipmap.ic_text_pic));
        list.add(new FunctionBean(6, getString(R.string.function_tab), R.mipmap.ic_table));
        list.add(new FunctionBean(7, getString(R.string.function_pic), R.mipmap.ic_pic));
        list.add(new FunctionBean(8, getString(R.string.function_line), R.mipmap.ic_line));
        list.add(new FunctionBean(9, getString(R.string.function_buffer), R.mipmap.ic_buffer));

        adapter = new BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.layout_main_rv) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, FunctionBean functionBean) {
                ImageView ivImage = viewHolder.getView(R.id.ivImage);
                TextView rvTitle = viewHolder.getView(R.id.rvTitle);
                if (functionBean != null) {
                    rvTitle.setText(functionBean.getName());
                    ivImage.setImageResource(functionBean.getResource());
                }
            }
        };
        adapter.setNewData(list);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FunctionBean functionBean = list.get(position);

            if (functionBean != null) {
                switch (functionBean.getId()) {
                    case 1:
                        startActivity(new Intent(view.getContext(), AllActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(view.getContext(), QrActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(view.getContext(), BarCodeActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(view.getContext(), TextPicActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(view.getContext(), TableActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(view.getContext(), PicActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(view.getContext(), FeedActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(view.getContext(), BufferActivity.class));
                        break;
                }
            }
        });
    }
}