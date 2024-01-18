package com.imin.newprinter.demo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:33
 * @description:
 */
public class FunctionTestFragmentViewModel extends BaseViewModel {

    private static final String TAG = "MainFragmentViewModel";

    public FunctionTestFragmentViewModel(@NonNull Application application) {
        super(application);
    }

}
