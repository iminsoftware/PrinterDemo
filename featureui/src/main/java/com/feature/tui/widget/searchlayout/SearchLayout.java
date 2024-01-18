package com.feature.tui.widget.searchlayout;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.util.XUiDisplayHelper;

/**
 * @Description
 * @Author Chenpl
 * @Time 2021/4/26 18:34
 */
public class SearchLayout extends RelativeLayout {
    RelativeLayout rlContent;
    EditText etSearch;
    ImageView ivSearch;
    ImageView ivDelete;
    ImageView ivBack;
    TextView tvSearch;
    ImageView ivCamera;
    ImageView ivMic;

    private Boolean showSearchIcon = false;
    private Boolean showRightIcon = true;
    private int leftIconResource = 0;
    private int rightIconResource = 0;
    private int leftIconWidth = 20;

    private int leftIconHeight = 20;
    private SearchCallback mSearchCallback = null;
    private Boolean showBackIcon = true;
    private Boolean showSearchButton = true;
    private Boolean showCameraIcon = true;
    private Boolean showMicIcon = true;

    private String hint;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public SearchLayout(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.SearchLayout);
        showSearchIcon = attrArray.getBoolean(R.styleable.SearchLayout_sv_show_left_icon, false);
        showRightIcon = attrArray.getBoolean(R.styleable.SearchLayout_sv_show_right_icon, true);
        leftIconResource = attrArray.getResourceId(
                R.styleable.SearchLayout_sv_left_icon,
                R.drawable.ic_common_ic_search
        );

        rightIconResource = attrArray.getResourceId(
                R.styleable.SearchLayout_sv_right_icon, R.drawable.ic_common_ic_shut_b_min
        );
        showBackIcon = attrArray.getBoolean(R.styleable.SearchLayout_sv_show_back_icon, true);
        showSearchButton =
                attrArray.getBoolean(R.styleable.SearchLayout_sv_show_search_button, true);

        showCameraIcon = attrArray.getBoolean(R.styleable.SearchLayout_sv_show_camera_icon, false);
        showMicIcon = attrArray.getBoolean(R.styleable.SearchLayout_sv_show_mic_icon, false);
        String sv_hint = attrArray.getString(R.styleable.SearchLayout_sv_hint);
        hint = TextUtils.isEmpty(sv_hint) ? context.getString(R.string.search_hint) : sv_hint;
        attrArray.recycle();
        initView();
        addChild();
        init(showSearchIcon, showRightIcon);
        showBackIcon(showBackIcon);
        showSearchButton(showSearchButton);
        showCamera(showCameraIcon);
        showMic(showMicIcon);
    }

    private void initView() {
        rlContent = new RelativeLayout(context);
        etSearch = new EditText(context);
        ivSearch = new ImageView(context);
        ivDelete = new ImageView(context);
        ivBack = new ImageView(context);
        tvSearch = new TextView(context);
        ivCamera = new ImageView(context);
        ivMic = new ImageView(context);
    }


    public ImageView getCameraView() {
        return ivCamera;
    }

    public ImageView getMicView() {
        return ivMic;
    }

    public EditText getEtSearch() {
        return etSearch;
    }

    public ImageView getIvSearch() {
        return ivSearch;
    }

    public ImageView getIvDelete() {
        return ivDelete;
    }

    public TextView getTvSearch() {
        return tvSearch;
    }

    public ImageView getIvCamera() {
        return ivCamera;
    }

    public ImageView getIvMic() {
        return ivMic;
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void addChild() {
        //设置整体背景
        setBackgroundResource(R.drawable.corner8_bg);
        setBackgroundColor(context.getColor(R.color.xui_config_color_white));

        RelativeLayout rlSearch = new RelativeLayout(context);
        LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlSearch.setLayoutParams(params);
        addView(rlSearch);

        //添加返回按钮
        params =
                new LayoutParams(XUiDisplayHelper.dp2px(context, 24), XUiDisplayHelper.dp2px(context, 24));
        params.addRule(CENTER_VERTICAL);
        params.setMarginStart(XUiDisplayHelper.dp2px(context, 20));
        ivBack.setLayoutParams(params);
        ivBack.setImageResource(R.drawable.ic_common__ic_arrow_l);
        rlSearch.addView(ivBack);
        ivBack.setOnClickListener(v -> {
            Activity activity = (Activity) context;
            activity.finish();
        });

        //添加搜索按钮
        params =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginEnd(XUiDisplayHelper.dp2px(context, 20));
        params.addRule(ALIGN_PARENT_END);
        tvSearch.setTextColor(context.getColor(R.color.xui_config_color_main));
        tvSearch.setText(context.getString(R.string.search));
        tvSearch.setTextSize(16f);
        tvSearch.setGravity(Gravity.CENTER);
        tvSearch.setLayoutParams(params);

        tvSearch.setOnClickListener(v -> {
            if (mSearchCallback != null) {
                mSearchCallback.onSearch(etSearch.getText().toString());
            }
        });
        rlSearch.addView(tvSearch);

        //添加搜索图标
        params =
                new LayoutParams(
                        XUiDisplayHelper.dp2px(context, leftIconWidth),
                        XUiDisplayHelper.dp2px(context, leftIconHeight)
                );
        params.addRule(CENTER_VERTICAL);
        params.setMarginStart(XUiDisplayHelper.dp2px(context, 12));
        ivSearch.setLayoutParams(params);
        rlContent.addView(ivSearch);


        //增加一个输入框的容器
        params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, XUiDisplayHelper.dp2px(context, 36));
        params.addRule(CENTER_VERTICAL);
        params.setMarginStart(XUiDisplayHelper.dp2px(context, 60));
        params.setMarginEnd(XUiDisplayHelper.dp2px(context, 20));
        rlContent.setLayoutParams(params);
        rlContent.setBackgroundResource(R.drawable.corner8_f5f5f5_bg);
        rlSearch.addView(rlContent);

        //添加输入框
        params =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMarginStart(XUiDisplayHelper.dp2px(context, 38));
        params.setMarginEnd(XUiDisplayHelper.dp2px(context, 34));
        etSearch.setPadding(0, XUiDisplayHelper.dp2px(context, 1), 0, 0);
        etSearch.setBackground(null);
        etSearch.setTextSize(14f);
        etSearch.setSingleLine();
        etSearch.setLayoutParams(params);
        etSearch.setHint(hint);
        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearch.setHintTextColor(context.getColor(R.color.xui_config_color_999999));
        etSearch.setTextColor(context.getColor(R.color.xui_config_color_black));
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mSearchCallback != null) {
                    mSearchCallback.onSearch(etSearch.getText().toString());
                }
                return true;
            } else {
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //有输入内容时才显示删除按钮
                if (!TextUtils.isEmpty(s.toString())) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    ivDelete.setVisibility(View.GONE);
                }
            }
        });
        rlContent.addView(etSearch);

        //添加动作按钮
        params =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_END);
        params.addRule(CENTER_VERTICAL);
        params.setMarginEnd(XUiDisplayHelper.dp2px(context, 8));
        LinearLayout llAction = new LinearLayout(context);
        llAction.setOrientation(LinearLayout.HORIZONTAL);
        llAction.setLayoutParams(params);
        rlContent.addView(llAction);


        //添加删除按钮
        LinearLayout.LayoutParams llParams =
                new LinearLayout.LayoutParams(
                        XUiDisplayHelper.dp2px(context, 24),
                        XUiDisplayHelper.dp2px(context, 24)
                );
        ivDelete.setLayoutParams(llParams);
        ivDelete.setOnClickListener(v -> etSearch.setText(""));
        llAction.addView(ivDelete);


        llParams =
                new LinearLayout.LayoutParams(
                        XUiDisplayHelper.dp2px(context, 24),
                        XUiDisplayHelper.dp2px(context, 24)
                );

        llParams.setMarginStart(XUiDisplayHelper.dp2px(context, 12));
        ivCamera.setLayoutParams(llParams);
        ivCamera.setImageResource(R.drawable.ic_common_ic_camera_search);
        llAction.addView(ivCamera);

        llParams =
                new LinearLayout.LayoutParams(
                        XUiDisplayHelper.dp2px(context, 24),
                        XUiDisplayHelper.dp2px(context, 24)
                );
        llParams.setMarginStart(XUiDisplayHelper.dp2px(context, 12));
        ivMic.setLayoutParams(llParams);
        ivMic.setImageResource(R.drawable.ic_common_ic_speak);
        llAction.addView(ivMic);
    }

    private void init(Boolean show, Boolean showRight) {
        RelativeLayout.LayoutParams params = (LayoutParams) etSearch.getLayoutParams();
        if (show) {
            ivSearch.setVisibility(View.VISIBLE);
            params.setMarginStart(XUiDisplayHelper.dp2px(context, 38));
        } else {
            ivSearch.setVisibility(View.GONE);
            params.setMarginStart(XUiDisplayHelper.dp2px(context, 16));
        }
        ivDelete.setVisibility(View.GONE);


        ivDelete.setImageResource(rightIconResource);
        ivSearch.setImageResource(leftIconResource);
    }

    /**
     * 点击搜索的回调
     */
    public void setSearchCallback(SearchCallback searchCallback) {
        this.mSearchCallback = searchCallback;
    }

    /**
     * 是否显示返回按钮
     */
    public void showBackIcon(Boolean show) {
        RelativeLayout.LayoutParams param = (LayoutParams) rlContent.getLayoutParams();
        if (show) {
            ivBack.setVisibility(View.VISIBLE);
            param.setMarginStart(XUiDisplayHelper.dp2px(context, 60));
        } else {
            ivBack.setVisibility(View.GONE);
            param.setMarginStart(XUiDisplayHelper.dp2px(context, 20));
        }
    }

    /**
     * 显示搜索文本
     */
    public void showSearchButton(Boolean show) {
        RelativeLayout.LayoutParams param = (LayoutParams) rlContent.getLayoutParams();
        if (show) {
            tvSearch.setVisibility(View.VISIBLE);
            param.setMarginEnd(XUiDisplayHelper.dp2px(context, 68));
        } else {
            tvSearch.setVisibility(View.GONE);
            param.setMarginEnd(XUiDisplayHelper.dp2px(context, 20));
        }
    }

    /**
     * 设置搜索框背景
     */
    public void setSearchBackgroundResource(int resourceId) {
        rlContent.setBackgroundResource(resourceId);
    }

    /**
     * 获取输入框EditText
     */
    public EditText getSearchEt() {
        return etSearch;
    }

    public void showCamera(Boolean show) {
        if (show) {
            ivCamera.setVisibility(View.VISIBLE);
        } else {
            ivCamera.setVisibility(View.GONE);
        }
    }

    public void showMic(Boolean show) {
        if (show) {
            ivMic.setVisibility(View.VISIBLE);
        } else {
            ivMic.setVisibility(View.GONE);
        }
    }

    public ImageView getIvBack() {
        return ivBack;
    }

}
