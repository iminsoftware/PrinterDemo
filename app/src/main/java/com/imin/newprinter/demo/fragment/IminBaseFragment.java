package com.imin.newprinter.demo.fragment;

import android.util.Log;

import androidx.databinding.ViewDataBinding;

import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.view.TitleLayout;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * @Author: Mark
 * @date: 2023/12/6 Time：10:13
 * @description:
 */
//public abstract class BaseListFragment<V extends ViewDataBinding, VM extends HttpViewModel
//        , A extends BaseQuickAdapter> extends BaseViewModelFragment<V, VM> {
//    BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment
public abstract class IminBaseFragment <V extends ViewDataBinding, VM extends BaseViewModel> extends BaseFragment implements TitleLayout.LeftCallback {


    private static final String TAG = "IminBaseFragment";
    public SwitchFragmentListener listener;
    TitleLayout.LeftCallback leftCallback;


    public void callback(SwitchFragmentListener listener) {
        this.listener = listener;
    }

    public void setLeftCallback(TitleLayout.LeftCallback leftCallback) {
        this.leftCallback = leftCallback;
    }


    @Override
    protected void registorUIChangeLiveDataCallBack() {
        super.registorUIChangeLiveDataCallBack();

        TitleLayout titleLayout = binding.getRoot().findViewById(R.id.view_title);
        titleLayout.setLeftCallback(this);
    }


    @Override
    public void backPre() {
        Log.d(TAG, "backPre: " + (leftCallback != null) );

        if (leftCallback != null) {
            leftCallback.backPre();
        }
    }

    @Override
    public void nextPage(int num) {
        Log.d(TAG, "nextPage: " + num);
    }
}
