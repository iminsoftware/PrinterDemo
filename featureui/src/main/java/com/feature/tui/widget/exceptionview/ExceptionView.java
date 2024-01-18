package com.feature.tui.widget.exceptionview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.feature.tui.R.drawable;
import com.feature.tui.R.id;
import com.feature.tui.R.layout;
import com.feature.tui.modle.ExceptionViewBean;
import com.feature.tui.util.XUiDisplayHelper;


@RequiresApi(23)
public class ExceptionView extends RelativeLayout {
    private LinearLayout llContent;
    private LinearLayout.LayoutParams llParams;
    private TextView title;
    private OnClickListener clickListener;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NO_NET = 2;
    public static final int STATE_NET_ERROR = 3;
    public static final int STATE_LOAD_FAILED = 4;
    public static final int STATE_NO_DATA = 5;
    public static final int STATE_NO_PERMISSION = 6;
    public static final int STATE_NO_LOGIN = 7;
    public static final int STATE_DELETE = 8;
    public static final int STATE_SEARCH = 9;

    public ExceptionView(@NonNull Context ctx) {
        super(ctx);
        init(ctx);
    }

    public ExceptionView(@NonNull Context ctx, @Nullable AttributeSet attributeSet) {
        super(ctx, attributeSet);
        init(ctx);
    }

    private void init(@NonNull Context ctx) {
        llContent = new LinearLayout(ctx);
        llParams = new LinearLayout.LayoutParams(XUiDisplayHelper.dp2px(ctx, 90), XUiDisplayHelper.dp2px(ctx, 90));
        title = new TextView(ctx);
        clickListener = v -> {
        };
        addChild();
    }

    public LinearLayout.LayoutParams getLlParams() {
        return llParams;
    }

    public void setLlParams(@NonNull LinearLayout.LayoutParams var1) {
        llParams = var1;
    }

    public final TextView getTitle() {
        return title;
    }

    private void addChild() {
        LayoutParams rlParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParams.addRule(CENTER_IN_PARENT);
        llContent.setOrientation(LinearLayout.VERTICAL);
        llContent.setLayoutParams(rlParams);
        addView(llContent);
        setExceptionType(STATE_NO_NET);
    }

    public void setExceptionType(int type) {
        switch (type) {
            case STATE_LOADING:
                loading();
                break;
            case STATE_NO_NET:
                noNet();
                break;
            case STATE_NET_ERROR:
                netError();
                break;
            case STATE_NO_DATA:
                noData();
                break;
            default:
                break;
        }

    }

    public void setExceptionViewBean(@NonNull final ExceptionViewBean exceptionViewBean) {
        if (exceptionViewBean == null)
            return;
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = CENTER_HORIZONTAL;
        View view = LayoutInflater.from(getContext()).inflate(layout.no_net_layout, (ViewGroup) llContent, false);
        view.setLayoutParams(llParams);
        llContent.addView(view);
        view.setOnClickListener(it -> {
            if (exceptionViewBean.getCallbackScreen() != null)
                exceptionViewBean.getCallbackScreen().invoke();
        });

        ImageView img = view.findViewById(id.img);
        ImageView imgLoading = view.findViewById(id.img_loading);
        if (exceptionViewBean.getImgResOrType() <= 0) {
            img.setVisibility(View.GONE);
        } else if (exceptionViewBean.getImgResOrType() > 0) {
            boolean isDefaultImg = false;
            switch (exceptionViewBean.getImgResOrType()) {
                case STATE_LOADING:
                    isDefaultImg = true;
                    imgLoading.setVisibility(VISIBLE);
                    img.setVisibility(GONE);
                    break;
                case STATE_NO_NET:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_nonetwork);
                    break;
                case STATE_NET_ERROR:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_error);
                    break;
                case STATE_LOAD_FAILED:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_failed);
                    break;
                case STATE_NO_DATA:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_empty);
                    break;
                case STATE_NO_PERMISSION:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_permission);
                    break;
                case STATE_NO_LOGIN:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_nologin);
                    break;
                case STATE_DELETE:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_delete);
                    break;
                case STATE_SEARCH:
                    isDefaultImg = true;
                    img.setImageResource(drawable.common_img_search);
            }

            if (!isDefaultImg) {
                img.setImageResource(exceptionViewBean.getImgResOrType());
            }
        }

        TextView tvTitle = view.findViewById(id.tv_title);
        tvTitle.setText(exceptionViewBean.getTitle());
        if (TextUtils.isEmpty(exceptionViewBean.getTitle())) {
            tvTitle.setVisibility(GONE);
        }

        TextView tvContent = view.findViewById(id.tv_content);
        tvContent.setText(exceptionViewBean.getContent());
        if (TextUtils.isEmpty(exceptionViewBean.getContent())) {
            tvContent.setVisibility(GONE);
        }

        TextView tvJump = view.findViewById(id.tv_jump);
        if (TextUtils.isEmpty(exceptionViewBean.getBtnText())) {
            tvJump.setVisibility(GONE);
        } else {
            tvJump.setText(exceptionViewBean.getBtnText());
            tvJump.setOnClickListener(it -> exceptionViewBean.getCallback().invoke());
        }
    }

    public View setCustomView(int layoutId) {
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = 14;
        View view = LayoutInflater.from(getContext()).inflate(layoutId, (ViewGroup) llContent, false);
        ;
        view.setLayoutParams(llParams);
        llContent.addView(view);
        return view;
    }

    public void noNet() {
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = 14;
        View view = LayoutInflater.from(getContext()).inflate(layout.no_net_layout, (ViewGroup) llContent, false);
        ;
        view.setLayoutParams(llParams);
        llContent.addView(view);
        TextView tvJump = view.findViewById(id.tv_jump);
        tvJump.setOnClickListener(clickListener);
    }

    public void loading() {
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = 14;
        View view = LayoutInflater.from(getContext()).inflate(layout.loading_view_layout, (ViewGroup) llContent, false);
        view.setLayoutParams(llParams);
        llContent.addView(view);
    }

    private void netError() {
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = 14;
        View view = LayoutInflater.from(getContext()).inflate(layout.net_error_layout, (ViewGroup) llContent, false);
        ;
        view.setLayoutParams(llParams);
        llContent.addView(view);
    }

    private void noData() {
        llContent.removeAllViews();
        llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParams.gravity = 14;
        View view = LayoutInflater.from(getContext()).inflate(layout.no_data_layout, (ViewGroup) llContent, false);
        ;
        view.setLayoutParams(llParams);
        llContent.addView(view);
    }

    public void setBtnClickEvent(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
