package com.imin.newprinterdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.imin.newprinterdemo.dialog.BaseDialog;
import com.imin.newprinterdemo.dialog.SelectDialog;
import com.imin.newprinterdemo.utils.Utils;
import com.imin.printer.PrinterHelper;


@SuppressLint({"MissingInflatedId","ResourceType"})
public class TitleLayout extends LinearLayout {

    private View view;
    private TypedArray typedArray;
    private FrameLayout flyLeft;
    private TextView tvTitle;
    private FrameLayout flyRight;
    private ImageView ivRight;
    private TextView tvPrinterStatus;

    public TitleLayout(Context context) {
        super(context);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.layout_title,this);
        typedArray = context.obtainStyledAttributes(attrs,R.styleable.TitleLayout);
        init(context);
    }

    boolean isBack = true;
    boolean isRightVisible = false;
    boolean isLeftVisible = true;
    private void init(Context context) {
        flyLeft = view.findViewById(R.id.flyLeft);
        ImageView ivLeft = view.findViewById(R.id.ivLeft);
        tvTitle = view.findViewById(R.id.tvTitle);
        flyRight = view.findViewById(R.id.flyRight);
        ivRight = view.findViewById(R.id.ivRight);
        tvPrinterStatus = view.findViewById(R.id.tvPrinterStatus);
        if (typedArray != null){
            isRightVisible = typedArray.getBoolean(R.styleable.TitleLayout_rightVisible,true);
            tvTitle.setText(typedArray.getString(R.styleable.TitleLayout_title));
            tvTitle.setTextColor(typedArray.getColor(R.styleable.TitleLayout_titleTextColor, Color.BLACK));
            isBack = typedArray.getBoolean(R.styleable.TitleLayout_back,true);
            isLeftVisible = typedArray.getBoolean(R.styleable.TitleLayout_leftVisible,false);
            typedArray.recycle();
        }
        flyRight.setVisibility(isRightVisible==false?GONE:VISIBLE);
        flyLeft.setVisibility(isLeftVisible == true?VISIBLE:GONE);
        if (isBack==true){
            flyLeft.setOnClickListener(v -> {
                if (context != null){
                    ((Activity)context).finish();
                }
            });
        }
        if (isRightVisible == true){
            flyRight.setOnClickListener(v -> {
                if (selectDialog != null){
                    selectDialog.dismiss();
                    selectDialog = null;
                }
                selectDialog = new SelectDialog(v.getContext());
                selectDialog.setClickListener(new BaseDialog.ClickListener() {
                    @Override
                    public void dismiss() {

                    }
                    @Override
                    public void selectItem(String s, int i) {
                        switch (i){
                            case 0:
                                PrinterHelper.getInstance().printAndFeedPaper(70);
                                break;
                            case 1:
                                PrinterHelper.getInstance().printAndFeedPaper(50);
                                PrinterHelper.getInstance().partialCut();
                                break;
                            case 2:
                                PrinterHelper.getInstance().openDrawer();
                                break;
                        }
                    }
                    @Override
                    public void cancel() {

                    }
                    @Override
                    public void sure(String s) {

                    }
                });
                selectDialog.setRvStringListData(Utils.getMoreSelect());
                selectDialog.show();
            });
        }
        tvPrinterStatus.setText(getContext().getString(R.string.print_status,PrinterHelper.getInstance().getPrinterStatus()+""));

    }
    SelectDialog selectDialog;

    public TitleLayout setTitle(String title){
        tvTitle.setText(title);
        return this;
    }

    public TitleLayout setPrinterStatus(int status){
        tvPrinterStatus.setText(this.getContext().getResources().getString(R.string.print_status,status+""));
        return this;
    }
}
