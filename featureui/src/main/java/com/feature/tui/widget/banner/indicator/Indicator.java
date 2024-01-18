package com.feature.tui.widget.banner.indicator;

import android.view.View;

import androidx.annotation.NonNull;

import com.feature.tui.widget.banner.config.IndicatorConfig;
import com.feature.tui.widget.banner.listener.OnPageChangeListener;


public interface Indicator extends OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
