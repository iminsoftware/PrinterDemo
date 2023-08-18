package com.imin.newprinterdemo.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinterdemo.R;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.newprinterdemo.view.PrinterMarginLayout;

import java.util.ArrayList;
import java.util.List;

public class AgingTestDialog extends BaseDialog{
    private List<SelectDialogBean> list = new ArrayList<>();
    private TextView tvCancel;
    private TextView tvSure;
    private PrinterMarginLayout printNumLayout, printIntervalLayout;
    private int totalPrinterNum = 100;
    private int totalInterval = 10000;
    private static final String TAG = "AgingTestDialog";

    public AgingTestDialog(@NonNull Context context) {
        super(context);
    }

    public AgingTestDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.select_dialog);
    }

    public AgingTestDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void initDialog(int width, int height, int gravity, boolean isCancelable) {
        super.initDialog(640, height, gravity, isCancelable);
    }

    public void setTotalPrinterNum(int num) {
        totalPrinterNum = num;
    }

    public void setTotalInterval(int time) {
        totalInterval = time;
    }

    String string = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ageing_test);
        printNumLayout = (PrinterMarginLayout) findViewById(R.id.print_num_layout);
        printIntervalLayout = (PrinterMarginLayout) findViewById(R.id.interval_layout);
        updatePrintLayout();
        printIntervalLayout.setTextViewSize(Utils.sp2px(getContext(), 9));
        tvCancel = findViewById(R.id.tvCancel);
        tvSure = findViewById(R.id.tvSure);
        ((TextView) findViewById(R.id.tv_max_print_num)).setText(String.valueOf(totalPrinterNum));
        ((TextView) findViewById(R.id.tv_max_interval_time)).setText(String.valueOf(totalInterval));
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            if (testDialogClick !=null){
                testDialogClick.cancel();
            }
            dismiss();
        });
        tvCancel = findViewById(R.id.tvCancel);
        tvSure = findViewById(R.id.tvSure);
        tvCancel.setOnClickListener(v -> {
            if (testDialogClick !=null){
                testDialogClick.cancel();
            }
            dismiss();
        });
        
        tvSure.setOnClickListener(v -> {
            if (testDialogClick !=null){
                testDialogClick.agingTestClick(v, printIntervalLayout.getNum(), printNumLayout.getNum());
            }
            dismiss();
        });
    }

    private void updatePrintLayout() {
        printNumLayout.setTotalNum(totalPrinterNum);
        printIntervalLayout.setTotalNum(totalInterval);
    }

    int min = 0;
    int max = 0;

    public AgingTestDialog setString(String string) {
        this.string = string;
        return this;
    }

    public AgingTestDialog setMin(int min) {
        this.min = min;
        return this;
    }

    public AgingTestDialog setMax(int max) {
        this.max = max;
        return this;
    }

    public void setTestDialogClick(TestDialogClick testDialogClick) {
        this.testDialogClick = testDialogClick;
    }

    TestDialogClick testDialogClick;

    public interface TestDialogClick{
        void agingTestClick(View view,int time,int num1);
        void cancel();
    }
}
