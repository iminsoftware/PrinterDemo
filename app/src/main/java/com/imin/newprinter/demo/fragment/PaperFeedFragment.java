package com.imin.newprinter.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.dialog.builder.SeekbarDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.widget.buttonview.ButtonView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CommonTestAdapter;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.bean.FunctionTestBean;
import com.imin.newprinter.demo.databinding.FragmentBarcodeTestBinding;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class PaperFeedFragment extends BaseListFragment<FragmentBarcodeTestBinding, FragmentCommonViewModel, CommonTestAdapter> {

    private static final String TAG = "PaperFeedFragment";
    private String[] contents;
    private String[] values;

    private int progress = 80;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_paper_feed_test;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
    }

    @Override
    public void initData() {
        contents = getResources().getStringArray(R.array.line_test_list);
        values = new String[]{ String.valueOf(progress)};
        super.initData();
    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        getRvAdapter().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                FunctionTestBean item = getRvAdapter().getItem(position);
                Log.d(TAG, "onItemChildClick: " + item.getTitle());
                TextView tvValue = view.findViewById(R.id.tv_item_value);

                if (position == 0) {
                    SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());

                    builder.setTitle(item.getTitle())
                            .setSeeBarMaxProcess(250)
                            .setSeeBarProcess(progress)
                            .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                            .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                                progress = builder.getProgress();
                                Log.d(TAG, "onItemClick: " + i + ", progress= " + progress);
                                tvValue.setText(progress + "");
                                dialog.dismiss();
                            }));
                    XUiDialog xuiDialog = builder.create();
                    xuiDialog.show();
                }

            }
        });

        ButtonView printLine = binding.getRoot().findViewById(R.id.print_line);
        ButtonView printFront = binding.getRoot().findViewById(R.id.print_front);

        printLine.setOnClickListener(v -> {
            PrinterHelper.getInstance().printAndLineFeed();
        });

        printFront.setOnClickListener(v -> {
            PrinterHelper.getInstance().printAndFeedPaper(progress);
        });
    }

    @Override
    protected CommonTestAdapter initAdapter() {
        ArrayList<FunctionTestBean> list = new ArrayList<>();

        for (int i = 0; i < contents.length; i++) {
            list.add(new FunctionTestBean(contents[i], values[i]));
        }

        return new CommonTestAdapter(R.layout.item_common, list);
    }
}
