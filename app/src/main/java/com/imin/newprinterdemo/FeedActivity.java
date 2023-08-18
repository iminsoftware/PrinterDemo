package com.imin.newprinterdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.imin.newprinterdemo.databinding.ActivityFeedBinding;
import com.imin.printer.PrinterHelper;

public class FeedActivity extends BaseActivity {

    private ActivityFeedBinding binding;
    private int line = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        binding.tvPrintAndLine.setOnClickListener(v -> {
            PrinterHelper.getInstance().printAndLineFeed();
        });

        binding.tvFrontPrint.setOnClickListener(v -> {
            String column = binding.etColumn.getText().toString().trim();

            if (column.length() > 0 && column.length() < 4){
                line = Integer.parseInt(column);
                PrinterHelper.getInstance().printAndFeedPaper(line);
            } else {
                Toast.makeText(FeedActivity.this, getText(R.string.edit_input_correct_num), Toast.LENGTH_SHORT).show();
            }
        });

    }
}