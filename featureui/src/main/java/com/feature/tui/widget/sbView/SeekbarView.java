package com.feature.tui.widget.sbView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.feature.tui.R;

public class SeekbarView extends LinearLayout {


    private static final String TAG = "SeekbarView";
    private SeekBar seekBar;

    public SeekbarView(Context context) {
        super(context);

        initView();
    }

    public SeekbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public SeekbarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void initView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_seekbar_layout, null);
        addView(view);

        seekBar = view.findViewById(R.id.seek_bar);


//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                Log.d(TAG, "onProgressChanged: i= " + i);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

    }
}
