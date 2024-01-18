package com.imin.newprinter.demo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feature.tui.dialog.builder.SeekbarDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.widget.buttonview.ButtonView;
import com.imin.newprinter.demo.BR;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.CustomDividerItemDecoration;
import com.imin.newprinter.demo.adapter.TableTestAdapter;
import com.imin.newprinter.demo.bean.TableBean;
import com.imin.newprinter.demo.databinding.FragmentQrCodeTestBinding;
import com.imin.newprinter.demo.databinding.FragmentTableFormTestBinding;
import com.imin.newprinter.demo.view.TitleLayout;
import com.imin.newprinter.demo.viewmodel.FragmentCommonViewModel;
import com.imin.printer.IPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;

/**
 * @Author: Mark
 * @date: 2023/12/5 Time：11:30
 * @description:
 */
public class TableFormFragment extends BaseListFragment<FragmentTableFormTestBinding, FragmentCommonViewModel, TableTestAdapter> {

    private static final String TAG = "TableFormFragment";
    private ArrayList<TableBean> list;
    private int colCount = 1;
    private RelativeLayout setCol;
    private TextView tvCount;
    private TextView addItem;
    private ButtonView print;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_table_form_test;
    }

    @Override
    protected RecyclerView.LayoutManager getRvLayoutManger() {
        return new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
    }

    @Override
    protected DividerItemDecoration getItemDecoration() {
        CustomDividerItemDecoration decoration = new CustomDividerItemDecoration(this.getActivity(),
                LinearLayoutManager.VERTICAL, 2);
        decoration.setDrawable(this.getActivity().getDrawable(R.drawable.shape_line));

        return decoration;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected TableTestAdapter initAdapter() {
        list = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            TableBean bean = new TableBean(colCount);

            list.add(bean);
        }

        return new TableTestAdapter(R.layout.item_table, list);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void initViewObservable() {
        super.initViewObservable();

        tvCount = binding.getRoot().findViewById(R.id.tv_col_count);
        setCol = binding.getRoot().findViewById(R.id.rl_col);
        addItem = binding.getRoot().findViewById(R.id.tv_add_item);
        print = binding.getRoot().findViewById(R.id.print);

        tvCount.setText(String.valueOf(colCount));
        setCol.setOnClickListener(v -> {
            SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
            builder.setTitle(getString(R.string.set_table_column))
                    .setSeeBarMaxProcess(4)
                    .setSeeBarMinProcess(1)
                    .setSeeBarProcess(colCount)
                    .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                    .addAction(getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                        colCount = builder.getProgress();
                        Log.d(TAG, "colCount= " + colCount);
                        tvCount.setText(String.valueOf(colCount));

                        for (int j = 0; j < list.size(); j++) {
                            list.get(j).setItemCount(colCount);
                        }
                        getRvAdapter().notifyDataSetChanged();

                        dialog.dismiss();
                    }));
            XUiDialog xuiDialog = builder.create();
            xuiDialog.show();
        });

        addItem.setOnClickListener(v -> {
            Log.d(TAG, "initViewObservable: ");
            list.add(new TableBean(colCount));
            getRvAdapter().notifyDataSetChanged();
        });

        print.setOnClickListener(v -> {

            for (int i = 0; i < list.size(); i++) {
                int itemCount = list.get(i).getItemCount();
                Log.d(TAG, "initViewObservable: " + itemCount);

                String[] colsContentArr = new String[itemCount];
                int[] colsWeightArr = new int[itemCount];
                int[] colsAlignArr = new int[itemCount];
                int[] colsSizeArr = new int[itemCount];
                for (int j = 0; j < list.get(i).getInnerBeanList().size(); j++) {
                    Log.d(TAG, "getInnerBeanList: " + list.get(i).getInnerBeanList().get(j).toString());
                    colsContentArr[j] = list.get(i).getInnerBeanList().get(j).getContent();
                    colsWeightArr[j] = list.get(i).getInnerBeanList().get(j).getWeight();
                    colsAlignArr[j] = list.get(i).getInnerBeanList().get(j).getAlign();
                    colsSizeArr[j] = list.get(i).getInnerBeanList().get(j).getSize();
                }

                PrinterHelper.getInstance().printColumnsString(colsContentArr, colsWeightArr, colsAlignArr, colsSizeArr, new IPrinterCallback() {
                    @Override
                    public void onRunResult(boolean isSuccess) throws RemoteException {

                    }

                    @Override
                    public void onReturnString(String result) throws RemoteException {

                    }

                    @Override
                    public void onRaiseException(int code, String msg) throws RemoteException {

                    }

                    @Override
                    public void onPrintResult(int code, String msg) throws RemoteException {

                    }

                    @Override
                    public IBinder asBinder() {
                        return null;
                    }
                });

            }
            PrinterHelper.getInstance().printAndLineFeed();
        });
    }
}
