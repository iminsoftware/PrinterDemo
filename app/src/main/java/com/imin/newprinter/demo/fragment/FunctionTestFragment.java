package com.imin.newprinter.demo.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.callback.SwitchFragmentListener;
import com.imin.newprinter.demo.databinding.FragmentFunctionTestBinding;
import com.imin.newprinter.demo.utils.Utils;
import com.imin.newprinter.demo.viewmodel.FunctionTestFragmentViewModel;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class FunctionTestFragment extends BaseFragment<FragmentFunctionTestBinding, FunctionTestFragmentViewModel>
        implements SwitchFragmentListener {

    private static final String TAG = "FunctionTestFragment";

    private SwitchFragmentListener listener;
    private BaseQuickAdapter<FunctionTestBean, BaseViewHolder> adapter;
    private ArrayList<FunctionTestBean> list;
    private RecyclerView recyclerView;
    private FrameLayout setting;
    private BaseFragment fragment = null;
    private RelativeLayout rlPrintStatus;
    private TextView tvPrintStatus;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_function_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void registorUIChangeLiveDataCallBack() {
        super.registorUIChangeLiveDataCallBack();

        recyclerView = binding.getRoot().findViewById(R.id.recycler_view);
        setting = binding.getRoot().findViewById(R.id.flyRight);
        rlPrintStatus = binding.getRoot().findViewById(R.id.rl_print_status);
        tvPrintStatus = binding.getRoot().findViewById(R.id.tv_printer_status);

    }

    @Override
    public void initData() {
        super.initData();

        list = new ArrayList<>();
        list.add(new FunctionTestBean(getString(R.string.function_all), R.mipmap.ic_all));
        list.add(new FunctionTestBean(getString(R.string.function_qrcode), R.drawable.ic_qrcode));
        list.add(new FunctionTestBean(getString(R.string.function_barcode), R.drawable.ic_barcode));
        list.add(new FunctionTestBean(getString(R.string.function_text_pic), R.drawable.ic_text_pic));
        list.add(new FunctionTestBean(getString(R.string.function_tab), R.drawable.ic_table));
        list.add(new FunctionTestBean(getString(R.string.function_pic), R.drawable.ic_pic));
        list.add(new FunctionTestBean(getString(R.string.function_buffer), R.drawable.ic_buffer));
        list.add(new FunctionTestBean(getString(R.string.paper_feed), R.drawable.ic_line));

        adapter = new BaseQuickAdapter<FunctionTestBean, BaseViewHolder>(R.layout.item_funtion_test) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, FunctionTestBean functionTestBean) {
                ImageView ivImage = viewHolder.getView(R.id.iv_draw);
                TextView rvTitle = viewHolder.getView(R.id.tv_title);
                if (functionTestBean != null) {
                    rvTitle.setText(functionTestBean.getTitle());
                    ivImage.setImageResource(functionTestBean.getImageResource());
                }
            }

        };

        adapter.setNewData(list);
        recyclerView.setAdapter(adapter);

        updatePrinterStatus(PrinterHelper.getInstance().getPrinterStatus());
        Log.d(TAG, "initData: ");

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FunctionTestBean bean = list.get(position);

            Log.d(TAG, "initViewObservable: " + bean.toString()
                    + ", " + bean.getFragment()
                    + ", position" + position
            );
            switchFragment(position);

        });


        setting.setOnClickListener(v -> {
            Log.d(TAG, "setting: ");
            switchFragment(100);
        });
        Log.d(TAG, "initViewObservable: ");

    }

    private SwitchFragmentListener fragmentListener;

    public void setCallback(SwitchFragmentListener listener) {

        fragmentListener = listener;
        Log.d(TAG, "setCallback: " + (fragmentListener != null));
    }

    @Override
    public void switchFragment(int num) {
        Log.d(TAG, "switchPager :num=  " + num + (fragmentListener != null));

        if (fragmentListener != null) {
            fragmentListener.switchFragment(num);
        }
    }

    public void updatePrinterStatus(int status) {
        boolean isNormal = (status == Utils.PRINTER_NORMAL);
        String printerStatusTip = Utils.getPrinterStatusTip(getContext(), status);
        Log.d(TAG, "updateStatus: " + status + ", isNormal= " + isNormal);

        Drawable drawable = IminApplication.mContext.getResources().getDrawable(isNormal ? R.drawable.bg_printer_normal : R.drawable.bg_printer_exception);
        if (rlPrintStatus != null) {
            rlPrintStatus.setBackground(drawable);
        }

        if (tvPrintStatus != null) {
            tvPrintStatus.setText(printerStatusTip);
        }
    }
}
