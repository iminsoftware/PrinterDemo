package com.feature.tui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.feature.tui.R;
import com.feature.tui.dialog.builder.CustomContentDialogBuilder;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.widget.pickerview.listener.OnTimeSelectChangeListener;

import java.util.Calendar;
import java.util.Date;

public class DialogUtil {

    /**
     * 时间选择器-系统默认选择器
     */
    public static CustomContentDialogBuilder showSysDateDialog(Context context, String title, final Date date, OnTimeSelectChangeListener onTimeSelectChangeListener) {
        DatePicker view = new DatePicker(context);
        view.setFirstDayOfWeek(android.icu.util.Calendar.MONDAY);
        hideDatePickerHeader(context, view);

        Calendar calendar = Calendar.getInstance();
        if (date != null)
            calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        CustomContentDialogBuilder builder = new CustomContentDialogBuilder(context)
                .setTitle(title)
                .setContentView(view)
                .addAction(new XUiDialogAction(context.getString(R.string.action_cancel), (dialog, i) -> dialog.dismiss()))
                .addAction(new XUiDialogAction(context.getString(R.string.action_confirm), (dialog, i) -> {
                    dialog.dismiss();
                    onTimeSelectChangeListener.onTimeSelectChanged(calendar.getTime());
                }));
        XUiDialog xuiDialog = builder.create();
        xuiDialog.show();

        view.init(year, monthOfYear, dayOfMonth, (view1, year1, monthOfYear1, dayOfMonth1) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, monthOfYear1);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
            if (view1 == null) {
                xuiDialog.dismiss();
                onTimeSelectChangeListener.onHeadClick(calendar.getTime());
            }
        });
        return builder;
    }

    private static void hideDatePickerHeader(Context context, DatePicker datePicker) {
        ViewGroup rootView = (ViewGroup) datePicker.getChildAt(0);
        if (rootView == null)
            return;
        View headerView = rootView.getChildAt(0);
        if (headerView == null)
            return;
        //5.0+
        int headerId = context.getResources().getIdentifier("day_picker_selector_layout", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
            layoutParamsRoot.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rootView.setLayoutParams(layoutParamsRoot);
            ViewGroup animator = (ViewGroup) rootView.getChildAt(1);
            ViewGroup.LayoutParams layoutParamsAnimator = animator.getLayoutParams();
            layoutParamsAnimator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            animator.setLayoutParams(layoutParamsAnimator);
            View child = animator.getChildAt(0);
            ViewGroup.LayoutParams layoutParamsChild = child.getLayoutParams();
            layoutParamsChild.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            child.setLayoutParams(layoutParamsChild);
            return;
        }
        //6.0+
        headerId = context.getResources().getIdentifier("date_picker_header", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
        }
    }

}
