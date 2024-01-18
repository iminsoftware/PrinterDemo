package com.imin.newprinter.demo.fragment;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imin.newprinter.demo.R;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ToastUtils;


public abstract class BaseListFragment<V extends ViewDataBinding, VM extends BaseViewModel
        , A extends BaseQuickAdapter> extends IminBaseFragment {

    private RecyclerView recyclerView;
    private A rvAdapter;
    protected String TAG = this.getClass().getSimpleName();

    @Override
    public void initData() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");
        recyclerView = binding.getRoot().findViewById(R.id.recycler_view);
        rvAdapter = initAdapter();
        recyclerView.setLayoutManager(getRvLayoutManger());
        recyclerView.setAdapter(rvAdapter);
        rvAdapter.setEmptyView(R.layout.list_empty_view);
        DividerItemDecoration itemDecoration = getItemDecoration();
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }

    }

    public boolean checkData(String data, String errorMsg) {
        if (TextUtils.isEmpty(data)) {
            ToastUtils.showShort(errorMsg);
            return true;
        }
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new LinearLayoutManager(this.getActivity());
    }

    protected void addData(List<?> data) {
        getRvAdapter().setNewInstance(data);
    }


    /**
     * 是否开启上拉加载更多
     *
     * @return boolean
     */
    protected boolean isEnableLoadMore() {
        return false;
    }

    @Override
    protected void registorUIChangeLiveDataCallBack() {
        super.registorUIChangeLiveDataCallBack();

    }


    protected DividerItemDecoration getItemDecoration() {
        return new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL);
    }


    protected abstract A initAdapter();

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected A getRvAdapter() {
        return rvAdapter;
    }

    public void addListData(Object data) {
        int itemCount = getRvAdapter().getItemCount();
        getRvAdapter().addData(data);
        getRecyclerView().scrollToPosition(itemCount);
    }

}
