package com.imin.newprinterdemo.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.imin.newprinterdemo.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingInflatedId")
public class SeekSizeDialog extends BaseDialog {

    private TextView setTitleDialog;
    private FrameLayout flClose;
    private RecyclerView rvDialog;
    private List<SelectDialogBean> list = new ArrayList<>();
    private SeekBar sbSeekbar;
    private TextView sbMin;
    private TextView sbResult;
    private TextView sbMax;
    private TextView tvCancel;
    private TextView tvSure;

    public SeekSizeDialog(@NonNull Context context) {
        super(context);
    }

    public SeekSizeDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.select_dialog);
    }

    public SeekSizeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    String string = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_seek_size);
        setTitleDialog = findViewById(R.id.setTitleDialog);
        setTitleDialog.setText(title);
        sbSeekbar = findViewById(R.id.sbSeekbar);
        sbMin = findViewById(R.id.sbMin);
        sbResult = findViewById(R.id.sbResult);
        sbMax = findViewById(R.id.sbMax);
        tvCancel = findViewById(R.id.tvCancel);
        tvSure = findViewById(R.id.tvSure);
        tvCancel.setOnClickListener(v -> {
            if (clickListener !=null){
                clickListener.cancel();
            }
            dismiss();
        });
        sbMin.setText(min+"");
        sbMax.setText(max+"");
        sbSeekbar.setMin(min);
        sbSeekbar.setMax(max);
        sbResult.setText(string);
        sbSeekbar.setProgress(Integer.parseInt(string));
        sbSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rs = progress;
                Log.d("printerTest_dialog","  onProgressChanged ====>    "+progress);
                sbResult.setText(rs + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvSure.setOnClickListener(v -> {
            if (clickListener !=null){
                String inputText = sbResult.getText().toString().trim();
                if (inputText != null && inputText.length() >0){
                    string = inputText;
                }
                clickListener.sure(string);
            }
            dismiss();
        });
    }

    int min = 0;
    int max = 0;

    public SeekSizeDialog setString(String string) {
        this.string = string;
        return this;
    }

    public SeekSizeDialog setMin(int min) {
        this.min = min;
        return this;
    }

    public SeekSizeDialog setMax(int max) {
        this.max = max;
        return this;
    }
}
