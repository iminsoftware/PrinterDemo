package com.imin.newprinter.demo.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:33
 * @description:
 */
public class FragmentCommonViewModel extends BaseViewModel {

    private static final String TAG = "FragmentCommonViewModel";

    public FragmentCommonViewModel(@NonNull Application application) {
        super(application);
    }

    //点击事件
    public View.OnClickListener commonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "commonOnClick: ");
            ToastUtils.showShort("allTestFragmentOnClick");
        }
    };
}
