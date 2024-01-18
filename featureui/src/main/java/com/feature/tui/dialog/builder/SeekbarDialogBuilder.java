package com.feature.tui.dialog.builder;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.feature.tui.R;
import com.feature.tui.dialog.center.XUiDialog;

public class SeekbarDialogBuilder
        extends BaseDialogBuilder<SeekbarDialogBuilder> {

    private static final String TAG = "SeekbarDialogBuilder";

    private SeekBar seekBar;
    private TextView sbValue;
    private int mProgress = 3;
    private int maxProcess = 6;
    private int minProcess = 0;


    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_seekbar_layout, parent, false);

        seekBar = inflate.findViewById(R.id.seek_bar);
        sbValue = inflate.findViewById(R.id.tv_sb_value);

        TextView first = inflate.findViewById(R.id.tv_first);
        TextView second = inflate.findViewById(R.id.tv_second);
        TextView third = inflate.findViewById(R.id.tv_third);
        TextView fourth = inflate.findViewById(R.id.tv_fourth);

        first.setText(String.valueOf(minProcess));
        second.setText(String.valueOf((maxProcess - minProcess)/3 + minProcess));
        third.setText(String.valueOf((maxProcess - minProcess) * 2/3 + minProcess));
        fourth.setText(String.valueOf(maxProcess));

        Log.d(TAG, "onCreateContent: " + mProgress);
        sbValue.setText(String.valueOf(mProgress));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(minProcess);
        }
        seekBar.setMax(maxProcess);
        seekBar.setProgress(mProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: " + progress + ", from= " + fromUser);
                mProgress = progress;
                sbValue.setText(String.valueOf(mProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return inflate;
    }

    /**
     * see bar process
     */
    public SeekbarDialogBuilder setSeeBarProcess(int process) {
        this.mProgress = process;
        return this;
    }

    /**
     * see bar process
     */
    public SeekbarDialogBuilder setSeeBarMaxProcess(int maxProcess) {
        this.maxProcess = maxProcess;
        return this;
    }

    /**
     * see bar process setSeeBarMinProcess
     */
    public SeekbarDialogBuilder setSeeBarMinProcess(int minProcess) {
        this.minProcess = minProcess;
        return this;
    }

    public SeekbarDialogBuilder(Context context) {
        super(context);
    }

    @Override
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER_HORIZONTAL;
        return layoutParams;
    }

    /**
     * 获取EditText中的内容
     */
    public int getProgress() {
        return mProgress;
    }
}
