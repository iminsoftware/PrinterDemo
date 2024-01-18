package com.imin.newprinter.demo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.feature.tui.dialog.builder.SeekbarDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.modle.PopupItemDescription;
import com.feature.tui.popup.ListPopup;
import com.feature.tui.popup.NormalPopup;
import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.adapter.holder.BaseDataBingHolder;
import com.imin.newprinter.demo.bean.TableBean;
import com.imin.newprinter.demo.bean.TableInnerBean;
import com.imin.newprinter.demo.databinding.ItemTableBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Mark
 * @date: 2023/12/7 Time：17:06
 * @description:
 */
public class TableTestAdapter extends BaseQuickAdapter<TableBean, BaseDataBingHolder> {

    private static final String TAG = "TableTestAdapter";
    private List<TableBean> list;
    private int colCount = 1;
    private BaseQuickAdapter adapter;

    public TableTestAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
        this.list = data;
    }


    @Override
    protected void convert(@NonNull BaseDataBingHolder baseViewHolder, TableBean bean) {
        Log.d(TAG, "convert: bean= " + bean.toString());

        ItemTableBinding binding = baseViewHolder.getBinding();

        binding.tvSerial.setText(getContext().getString(R.string.table_col_num,String.valueOf(getItemPosition(bean) + 1)));

        binding.colNum.setOnClickListener(v -> {
            SeekbarDialogBuilder builder = new SeekbarDialogBuilder(getContext());
            builder.setTitle(getContext().getString(R.string.set_table_column))
                    .setSeeBarMaxProcess(4)
                    .setSeeBarMinProcess(1)
                    .setSeeBarProcess(Integer.parseInt(binding.colNum.getText().toString()))
                    .addAction(new XUiDialogAction(getContext().getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                    .addAction(getContext().getString(R.string.action_confirm), XUiDialogAction.ACTION_PROP_POSITIVE, ((dialog, i) -> {
                        colCount = builder.getProgress();
                        Log.d(TAG, "colCount= " + colCount);
                        binding.colNum.setText(String.valueOf(colCount));
                        bean.setItemCount(colCount);

                        notifyDataSetChanged();
                        dialog.dismiss();
                    }));
            XUiDialog xuiDialog = builder.create();
            xuiDialog.show();
        });

        adapter = new BaseQuickAdapter<TableInnerBean, BaseViewHolder>(R.layout.item_table_inner) {
            @Override
            protected void convert(@NonNull BaseViewHolder viewHolder, TableInnerBean innerBean) {
                Log.d(TAG, "convert:innerBean " + innerBean.toString());
                TextView tvPop = viewHolder.getView(R.id.tv_pop);

                EditText etContent = viewHolder.getView(R.id.et_content);
                EditText etWeight = viewHolder.getView(R.id.et_weight);
                EditText etSize = viewHolder.getView(R.id.et_size);

                tvPop.setText(getAlignmentArray()[innerBean.getAlign()]);

                tvPop.setOnClickListener(v->{
                    showPopup(tvPop, NormalPopup.FROM_CENTER, NormalPopup.DIRECTION_BOTTOM, innerBean);
                });

                etContent.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Log.d(TAG, "etContent: " + etContent.getText().toString().trim());
                        innerBean.setContent(etContent.getText().toString().trim());
                    }
                });

                etWeight.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Log.d(TAG, "etWeight: " + etWeight.getText().toString().trim());
                        String weight = etWeight.getText().toString().trim();
                        if (TextUtils.isEmpty(weight)) {
                            Log.e(TAG, "weight is null!!!");
                            return;
                        }

                        innerBean.setWeight(Integer.parseInt(weight));
                    }
                });

                etSize.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        Log.d(TAG, "etSize: " + etSize.getText().toString().trim());
                        String size = etSize.getText().toString().trim();
                        if (TextUtils.isEmpty(size)) {
                            Log.e(TAG, "size is null!!!");
                            return;
                        }
                        innerBean.setSize(Integer.parseInt(size));
                    }
                });
            }
        };

        Log.d(TAG, "convert: " + bean.getInnerBeanList().size());
        adapter.setNewData(bean.getInnerBeanList());
        binding.recyclerView.setAdapter(adapter);

        binding.setData(bean);
    }


    @NonNull
    @Override
    public BaseDataBingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    private String[] getAlignmentArray() {
        String[] array = getContext().getResources().getStringArray(R.array.align_ment);
        return array;
    }

    private List<PopupItemDescription> getAlignmentList() {
        List<PopupItemDescription> arrayList = new ArrayList<>();
        for (int i = 0; i < getAlignmentArray().length; i++) {
            PopupItemDescription itemDescription = new PopupItemDescription(getAlignmentArray()[i]);
            arrayList.add(itemDescription);
        }
        return arrayList;
    }

    private void setCompoundDrawable(TextView view, boolean up) {

        Drawable drawable = getContext().getResources().getDrawable(up ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        view.setCompoundDrawables(null, null, drawable, null);

    }

    private void showPopup(TextView v, int direction, int preferredDirection, TableInnerBean innerBean) {

        setCompoundDrawable(v, true);
        ListPopup<PopupItemDescription> listPopup = new ListPopup<>(getContext());
        listPopup.setItems(getAlignmentList(), (item, position) -> {
                    PopupItemDescription description = (PopupItemDescription) item;
                    v.setText(description.getTitle());
                    innerBean.setAlign(position);
                    listPopup.dismiss();
                    Log.d(TAG, "PopupItemDescription: " + item + ", = " + position);
                })
                .animDirection(direction)
                .preferredDirection(preferredDirection)
                .setOnDismissListener(() -> {
                    Log.d(TAG, "setOnDismissListener: ");
                    setCompoundDrawable(v, false);
                });
        listPopup.show(v);
    }
}
