package com.imin.newprinter.demo.view;

import android.annotation.SuppressLint;
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

import com.imin.newprinter.demo.R;
import com.imin.newprinter.demo.dialog.SelectDialog;
import com.imin.printer.PrinterHelper;

@SuppressLint({"MissingInflatedId", "ResourceType"})
public class TitleLayout extends LinearLayout {

    private View view;
    private TypedArray typedArray;
    private FrameLayout flyLeft;
    private FrameLayout flyLeftTitle;
    private TextView tvTitle;
    private TextView tvLeftTitle;
    private FrameLayout flyRight;
    private ImageView ivRight;
    private TextView tvPrinterStatus;

    public TitleLayout(Context context) {
        super(context);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout);
        init(context);
    }

    private boolean isBack = true;
    private boolean isRightVisible = false;
    private boolean isLeftVisible = true;
    private boolean isLeftTitleVisible = false;

    private void init(Context context) {
        flyLeft = view.findViewById(R.id.flyLeft);
        ImageView ivLeft = view.findViewById(R.id.ivLeft);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvLeftTitle = view.findViewById(R.id.tvLeftTitle);

        flyRight = view.findViewById(R.id.flyRight);
        flyLeftTitle = view.findViewById(R.id.flyLeftTitle);
        ivRight = view.findViewById(R.id.ivRight);

        tvPrinterStatus = view.findViewById(R.id.tvPrinterStatus);

        if (typedArray != null) {
            tvTitle.setText(typedArray.getString(R.styleable.TitleLayout_title));
            tvLeftTitle.setText(typedArray.getString(R.styleable.TitleLayout_leftTitle));
            tvTitle.setTextColor(typedArray.getColor(R.styleable.TitleLayout_titleTextColor, Color.BLACK));
            tvLeftTitle.setTextColor(typedArray.getColor(R.styleable.TitleLayout_titleTextColor, Color.BLACK));
            isBack = typedArray.getBoolean(R.styleable.TitleLayout_back, true);
            isRightVisible = typedArray.getBoolean(R.styleable.TitleLayout_rightVisible, false);
            isLeftVisible = typedArray.getBoolean(R.styleable.TitleLayout_leftVisible, false);
            isLeftTitleVisible = typedArray.getBoolean(R.styleable.TitleLayout_leftTitleVisible, false);
            typedArray.recycle();
        }
        flyRight.setVisibility(isRightVisible ? VISIBLE : GONE);
        flyLeft.setVisibility(isLeftVisible ? VISIBLE : GONE);
        flyLeftTitle.setVisibility(isLeftTitleVisible ? VISIBLE : GONE);

        if (isBack == true) {
            flyLeft.setOnClickListener(v -> {
                if (leftCallback != null) {
                    leftCallback.backPre();
                }
            });
        }

        tvPrinterStatus.setText(getContext().getString(R.string.print_status, PrinterHelper.getInstance().getPrinterStatus() + ""));

        if (isRightVisible){
            flyRight.setOnClickListener(view1 -> {
                if (rightCallback != null){
                    rightCallback.onClick(view1);
                }
            });
        }



    }

    SelectDialog selectDialog;

    public TitleLayout setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public TitleLayout setPrinterStatus(int status) {
        tvPrinterStatus.setText(this.getContext().getResources().getString(R.string.print_status, status + ""));
        return this;
    }

    private LeftCallback leftCallback;
    public void setLeftCallback(LeftCallback callback) {
        leftCallback = callback;
    }

    public interface LeftCallback {
        void backPre();
        void nextPage(int num);
    }
    RightCallback rightCallback;

    public void setRightCallback(RightCallback rightCallback) {
        this.rightCallback = rightCallback;
    }

    public interface RightCallback{
        void onClick(View view);
    }
}
