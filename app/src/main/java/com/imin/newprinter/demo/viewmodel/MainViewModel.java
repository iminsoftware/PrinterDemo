package com.imin.newprinter.demo.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;


import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：10:53
 * @description:
 */
public class MainViewModel extends BaseViewModel {

    private static final String TAG = "MainViewModel";
    public MainViewModel(@androidx.annotation.NonNull Application application) {
        super(application);
    }

    //点击事件
    public View.OnClickListener mainOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "mainOnClick: ");
            ToastUtils.showShort("mainOnClick");
        }
    };
}
