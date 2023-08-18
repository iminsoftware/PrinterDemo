package com.imin.newprinterdemo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.imin.newprinterdemo.databinding.ActivityTableBinding;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.INeoPrinterCallback;
import com.imin.printer.PrinterHelper;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends BaseActivity {
    private static final String TAG = "printerTest_TableActivity";
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    private final List<String> list = new ArrayList<>();
    private ActivityTableBinding binding;
    private int tableRow = 1;
    private String[] colsTextArr = new String[]{};
    private int[] colsWidthArr = new int[]{};
    private int[] colsAlignArr = new int[]{};
    private int[] colsSizeArr = new int[]{};
    private List<TableItem> tableItemList = new ArrayList<>();
    private TextView tvAddTable;
    private EditText etTabContent1;
    private EditText etTabContent2;
    private EditText etTabContent3;
    private EditText etTabContent4;
    private EditText etTabWeight1;
    private EditText etTabWeight4;
    private EditText etTabWeight3;
    private EditText etTabWeight2;
    private EditText etTabAlign1;
    private EditText etTabAlign2;
    private EditText etTabAlign3;
    private EditText etTabAlign4;
    private EditText etTabSize1;
    private EditText etTabSize2;
    private EditText etTabSize3;
    private EditText etTabSize4;

    private class EditTextWatcher implements TextWatcher {

        private TextView view;

        public EditTextWatcher(View view) {

            if (view instanceof TextView) {

                this.view = (TextView) view;
            } else {
                throw new ClassCastException(
                        "view must be an instance Of TextView");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() > 0 && binding.etColumn.getText().toString().trim().length() > 0) {
                tableItemList.clear();
                adapter.notifyDataSetChanged();

                Log.d(TAG, "afterTextChanged: = " + (view.getId() == R.id.etTabContent1));
                switch (view.getId()) {

                    case R.id.etTabContent1:

                        etTabContent1.setFocusable(true);
                        etTabContent1.setFocusableInTouchMode(true);
                        etTabContent1.requestFocus();
                        break;

                    case R.id.etTabContent2:


                        break;

                    case R.id.etTabContent3:


                        break;

                    case R.id.etTabContent4:


                        break;


                    default:

                        break;
                }
            }
        }
    }

    private int colum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTableBinding.inflate(LayoutInflater.from(TableActivity.this));
        setContentView(binding.getRoot());
        binding.tableRv.setLayoutManager(new LinearLayoutManager(TableActivity.this));
        list.add(getString(R.string.table_lable, tableRow + ""));
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.layout_tab_rv) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, String s) {
                tvAddTable = viewHolder.getView(R.id.tvAddTable);
                etTabContent1 = viewHolder.getView(R.id.etTabContent1);
                etTabContent2 = viewHolder.getView(R.id.etTabContent2);
                etTabContent3 = viewHolder.getView(R.id.etTabContent3);
                etTabContent4 = viewHolder.getView(R.id.etTabContent4);

                etTabWeight1 = viewHolder.getView(R.id.etTabWeight1);
                etTabWeight2 = viewHolder.getView(R.id.etTabWeight2);
                etTabWeight3 = viewHolder.getView(R.id.etTabWeight3);
                etTabWeight4 = viewHolder.getView(R.id.etTabWeight4);

                etTabAlign1 = viewHolder.getView(R.id.etTabAlign1);
                etTabAlign2 = viewHolder.getView(R.id.etTabAlign2);
                etTabAlign3 = viewHolder.getView(R.id.etTabAlign3);
                etTabAlign4 = viewHolder.getView(R.id.etTabAlign4);

                etTabSize1 = viewHolder.getView(R.id.etTabSize1);
                etTabSize2 = viewHolder.getView(R.id.etTabSize2);
                etTabSize3 = viewHolder.getView(R.id.etTabSize3);
                etTabSize4 = viewHolder.getView(R.id.etTabSize4);
                etTabContent2.setVisibility(View.GONE);
                etTabContent3.setVisibility(View.GONE);
                etTabContent4.setVisibility(View.GONE);

                etTabWeight2.setVisibility(View.GONE);
                etTabWeight3.setVisibility(View.GONE);
                etTabWeight4.setVisibility(View.GONE);

                etTabAlign2.setVisibility(View.GONE);
                etTabAlign3.setVisibility(View.GONE);
                etTabAlign4.setVisibility(View.GONE);

                etTabSize2.setVisibility(View.GONE);
                etTabSize3.setVisibility(View.GONE);
                etTabSize4.setVisibility(View.GONE);

                etTabContent1.addTextChangedListener(new EditTextWatcher(etTabContent1));
                etTabContent2.addTextChangedListener(new EditTextWatcher(etTabContent2));
                etTabContent3.addTextChangedListener(new EditTextWatcher(etTabContent3));
                etTabContent4.addTextChangedListener(new EditTextWatcher(etTabContent4));
                etTabWeight1.addTextChangedListener(new EditTextWatcher(etTabWeight1));
                etTabWeight2.addTextChangedListener(new EditTextWatcher(etTabWeight2));
                etTabWeight3.addTextChangedListener(new EditTextWatcher(etTabWeight3));
                etTabWeight4.addTextChangedListener(new EditTextWatcher(etTabWeight4));
                etTabAlign1.addTextChangedListener(new EditTextWatcher(etTabAlign1));
                etTabAlign2.addTextChangedListener(new EditTextWatcher(etTabAlign2));
                etTabAlign3.addTextChangedListener(new EditTextWatcher(etTabAlign3));
                etTabAlign4.addTextChangedListener(new EditTextWatcher(etTabAlign4));
                etTabSize1.addTextChangedListener(new EditTextWatcher(etTabSize1));
                etTabSize2.addTextChangedListener(new EditTextWatcher(etTabSize2));
                etTabSize3.addTextChangedListener(new EditTextWatcher(etTabSize3));
                etTabSize4.addTextChangedListener(new EditTextWatcher(etTabSize4));

                if (s != null && s.length() > 0) {
                    tvAddTable.setText(s);
                    colum = 1;

                    if (binding.etColumn.getText().toString().trim().length() > 0) {
                        colum = Integer.parseInt(binding.etColumn.getText().toString().trim());
                    }

                    colsTextArr = new String[colum];
                    colsWidthArr = new int[colum];
                    colsAlignArr = new int[colum];
                    colsSizeArr = new int[colum];
                    switch (colum) {
                        case 1:
                            etTabContent1.setVisibility(View.VISIBLE);
                            etTabWeight1.setVisibility(View.VISIBLE);
                            etTabAlign1.setVisibility(View.VISIBLE);
                            etTabSize1.setVisibility(View.VISIBLE);
                            colsTextArr[0] = etTabContent1.getText().toString().trim() + "1";
                            colsWidthArr[0] = Integer.parseInt(etTabWeight1.getText().toString().trim());
                            colsAlignArr[0] = Integer.parseInt(etTabAlign1.getText().toString().trim());
                            colsSizeArr[0] = Integer.parseInt(etTabSize1.getText().toString().trim());
                            break;
                        case 2:
                            etTabContent1.setVisibility(View.VISIBLE);
                            etTabWeight1.setVisibility(View.VISIBLE);
                            etTabAlign1.setVisibility(View.VISIBLE);
                            etTabSize1.setVisibility(View.VISIBLE);
                            etTabContent2.setVisibility(View.VISIBLE);
                            etTabWeight2.setVisibility(View.VISIBLE);
                            etTabAlign2.setVisibility(View.VISIBLE);
                            etTabSize2.setVisibility(View.VISIBLE);
                            colsTextArr[0] = etTabContent1.getText().toString().trim() + "1";
                            colsWidthArr[0] = Integer.parseInt(etTabWeight1.getText().toString().trim());
                            colsAlignArr[0] = Integer.parseInt(etTabAlign1.getText().toString().trim());
                            colsSizeArr[0] = Integer.parseInt(etTabSize1.getText().toString().trim());
                            colsTextArr[1] = etTabContent2.getText().toString().trim() + "2";
                            colsWidthArr[1] = Integer.parseInt(etTabWeight2.getText().toString().trim());
                            colsAlignArr[1] = Integer.parseInt(etTabAlign2.getText().toString().trim());
                            colsSizeArr[1] = Integer.parseInt(etTabSize2.getText().toString().trim());
                            break;
                        case 3:
                            etTabContent1.setVisibility(View.VISIBLE);
                            etTabWeight1.setVisibility(View.VISIBLE);
                            etTabAlign1.setVisibility(View.VISIBLE);
                            etTabSize1.setVisibility(View.VISIBLE);
                            etTabContent2.setVisibility(View.VISIBLE);
                            etTabWeight2.setVisibility(View.VISIBLE);
                            etTabAlign2.setVisibility(View.VISIBLE);
                            etTabSize2.setVisibility(View.VISIBLE);
                            etTabContent3.setVisibility(View.VISIBLE);
                            etTabWeight3.setVisibility(View.VISIBLE);
                            etTabAlign3.setVisibility(View.VISIBLE);
                            etTabSize3.setVisibility(View.VISIBLE);
                            colsTextArr[0] = etTabContent1.getText().toString().trim() + "1";
                            colsWidthArr[0] = Integer.parseInt(etTabWeight1.getText().toString().trim());
                            colsAlignArr[0] = Integer.parseInt(etTabAlign1.getText().toString().trim());
                            colsSizeArr[0] = Integer.parseInt(etTabSize1.getText().toString().trim());
                            colsTextArr[1] = etTabContent2.getText().toString().trim() + "2";
                            colsWidthArr[1] = Integer.parseInt(etTabWeight2.getText().toString().trim());
                            colsAlignArr[1] = Integer.parseInt(etTabAlign2.getText().toString().trim());
                            colsSizeArr[1] = Integer.parseInt(etTabSize2.getText().toString().trim());
                            colsTextArr[2] = etTabContent3.getText().toString().trim() + "3";
                            colsWidthArr[2] = Integer.parseInt(etTabWeight3.getText().toString().trim());
                            colsAlignArr[2] = Integer.parseInt(etTabAlign3.getText().toString().trim());
                            colsSizeArr[2] = Integer.parseInt(etTabSize3.getText().toString().trim());
                            break;
                        case 4:
                            etTabContent1.setVisibility(View.VISIBLE);
                            etTabWeight1.setVisibility(View.VISIBLE);
                            etTabAlign1.setVisibility(View.VISIBLE);
                            etTabSize1.setVisibility(View.VISIBLE);
                            etTabContent2.setVisibility(View.VISIBLE);
                            etTabWeight2.setVisibility(View.VISIBLE);
                            etTabAlign2.setVisibility(View.VISIBLE);
                            etTabSize2.setVisibility(View.VISIBLE);
                            etTabContent3.setVisibility(View.VISIBLE);
                            etTabWeight3.setVisibility(View.VISIBLE);
                            etTabAlign3.setVisibility(View.VISIBLE);
                            etTabSize3.setVisibility(View.VISIBLE);
                            etTabContent4.setVisibility(View.VISIBLE);
                            etTabWeight4.setVisibility(View.VISIBLE);
                            etTabAlign4.setVisibility(View.VISIBLE);
                            etTabSize4.setVisibility(View.VISIBLE);
                            colsTextArr[0] = etTabContent1.getText().toString().trim() + "1";
                            colsWidthArr[0] = Integer.parseInt(etTabWeight1.getText().toString().trim());
                            colsAlignArr[0] = Integer.parseInt(etTabAlign1.getText().toString().trim());
                            colsSizeArr[0] = Integer.parseInt(etTabSize1.getText().toString().trim());
                            colsTextArr[1] = etTabContent2.getText().toString().trim() + "2";
                            colsWidthArr[1] = Integer.parseInt(etTabWeight2.getText().toString().trim());
                            colsAlignArr[1] = Integer.parseInt(etTabAlign2.getText().toString().trim());
                            colsSizeArr[1] = Integer.parseInt(etTabSize2.getText().toString().trim());
                            colsTextArr[2] = etTabContent3.getText().toString().trim() + "3";
                            colsWidthArr[2] = Integer.parseInt(etTabWeight3.getText().toString().trim());
                            colsAlignArr[2] = Integer.parseInt(etTabAlign3.getText().toString().trim());
                            colsSizeArr[2] = Integer.parseInt(etTabSize3.getText().toString().trim());
                            colsTextArr[3] = etTabContent4.getText().toString().trim() + "4";
                            colsWidthArr[3] = Integer.parseInt(etTabWeight4.getText().toString().trim());
                            colsAlignArr[3] = Integer.parseInt(etTabAlign4.getText().toString().trim());
                            colsSizeArr[3] = Integer.parseInt(etTabSize4.getText().toString().trim());
                            break;
                    }
                }
                updateTableData();
            }
        };

        binding.etColumn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.etColumn.getText().toString().trim().length() > 0) {
                    int a = Integer.parseInt(binding.etColumn.getText().toString().trim());

                    if (a > 0 && colum != a) {
                        tableItemList.clear();
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });

        adapter.setNewData(list);
        binding.tableRv.setAdapter(adapter);
        binding.tvAddTable.setOnClickListener(v -> {
            if (!Utils.isFastDoubleClick()) {
                tableRow += 1;
                list.add(getString(R.string.table_lable, tableRow + ""));
                tableItemList.clear();
                adapter.notifyDataSetChanged();
            }

        });

        binding.tvPrint1.setOnClickListener(v -> {

            if (binding.etColumn.getText().toString().trim().length() == 0) {
                Toast.makeText(TableActivity.this, getResources().getString(R.string.toast_table_num_tip), Toast.LENGTH_LONG).show();
            } else {
                for (TableItem tableItem : tableItemList) {
                    PrinterHelper.getInstance().printColumnsString(tableItem.colsTextArr, tableItem.colsWidthArr, tableItem.colsAlignArr, tableItem.colsSizeArr, new INeoPrinterCallback() {
                        @Override
                        public void onRunResult(boolean isSuccess) throws RemoteException {
                            Log.d(TAG, " printColumnsText    onRunResult ====>    " + isSuccess);
                        }

                        @Override
                        public void onReturnString(String result) throws RemoteException {
                            Log.d(TAG, "  onReturnString ====>    " + result);
                        }

                        @Override
                        public void onRaiseException(int code, String msg) throws RemoteException {

                        }

                        @Override
                        public void onPrintResult(int code, String msg) throws RemoteException {

                        }
                    });

                }
            }
        });
    }

    private void updateTableData() {
        TableItem tableItem = new TableItem(colsTextArr, colsWidthArr, colsAlignArr, colsSizeArr);
        tableItemList.add(tableItem);
    }
}