package com.feature.tui.widget.inputlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.R.color;
import com.feature.tui.R.dimen;
import com.feature.tui.R.drawable;
import com.feature.tui.R.styleable;
import com.feature.tui.dialog.Functions;
import com.feature.tui.popup.NormalPopup;
import com.feature.tui.popup.SimpleCornerPopup;
import com.feature.tui.util.XUiDisplayHelper;

import java.util.List;

@RequiresApi(23)
public class MarkEditText extends LinearLayout {

    private final String HIDDEN_STR = "······";
    private long lastDismissTime;

    private ImageView imgIcon;
    private TextView tvSelect;
    private AutoCompleteTextView etContent;
    private ImageView ivHidden;
    private ImageView ivDelete;
    private TextView markView;
    private ImageView imgSelect;
    private View bottomLine;
    private TextView errorTipsView;

    private boolean isSingle = true;
    private int count = Integer.MAX_VALUE - 1;

    private int bottomLineColor;
    private int bottomLineHeight;

    private CharSequence etText;
    private CharSequence etHintText;
    private CharSequence defaultText;
    private int textColor;
    private float textSize;

    private boolean isShowDelete;//删除小按钮
    private boolean isShowMark = true;//输入个数提示
    private boolean isShowHidden;//显示、隐藏小按钮
    private boolean selectRight;//是否显示右侧下拉列表
    private boolean isCanDelete;//下拉列表是否可以删除
    private boolean isShowLine = true;
    private boolean showLengthOverTips;

    private String helpText;
    private int defaultLines = 1;
    private String errorText;
    private int initHeight = 0;

    private Drawable icon = null;//左边图标
    private String strSelect = null;//左边可选标签

    private SimpleCornerPopup listPopup;

    private List<String> itemsRight;
    private ContentAdapterForEdit adapterRight;

    public MarkEditText(@NonNull Context context) {
        super(context);
        initMark(null);
    }

    public MarkEditText(@NonNull Context context, boolean isSingle) {
        super(context);
        this.isSingle = isSingle;
        initMark(null);
    }

    public MarkEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMark(attrs);
    }

    public MarkEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMark(attrs);
    }

    private void initMark(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.common_edit_text, null);
        addView(view);
        imgIcon = view.findViewById(R.id.img_icon);
        tvSelect = view.findViewById(R.id.tv_select);
        etContent = view.findViewById(R.id.edit_text);
        ivHidden = view.findViewById(R.id.img_hide_view);
        ivDelete = view.findViewById(R.id.img_delete_view);
        markView = view.findViewById(R.id.tv_mark);
        imgSelect = view.findViewById(R.id.img_select);
        bottomLine = view.findViewById(R.id.bottom_line);
        errorTipsView = view.findViewById(R.id.tv_error_tips);
        bottomLineColor = getContext().getResources().getColor(color.xui_color_eaeaea);
        bottomLineHeight = XUiDisplayHelper.dp2px(getContext(), 1);
        textColor = getResources().getColor(color.xui_config_color_title);
        textSize = getResources().getDimension(dimen.sp_14);

        initView(getContext(), attrs);

        etContent.setOnFocusChangeListener((v, hasFocus) -> {
            if (isShowDelete && hasFocus && etContent.getText().length() != 0) {
                ivDelete.setVisibility(VISIBLE);
            } else {
                ivDelete.setVisibility(GONE);
            }
            if (hasFocus) {
                if (bottomLine != null) {
                    bottomLine.setBackgroundColor(getContext().getColor(color.xui_config_color_main));
                }
            } else {
                if (bottomLine != null) {
                    bottomLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
                }
            }
            if (!TextUtils.isEmpty(errorText)) {
                bottomLine.setBackgroundColor(getContext().getColor(color.xui_config_color_error));
            }
        });

        etContent.setOnTouchListener((v, event) -> {
            if (etContent.getLineCount() <= defaultLines)
                return false;
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (MotionEvent.ACTION_UP == event.getAction()) {
                /*告诉父组件可以拦截他的触摸事件*/
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }

    private void initView(Context context, AttributeSet attrs) {
        boolean isEditEnable = true;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, styleable.MarkEditText, 0, 0);
            isSingle = a.getBoolean(styleable.MarkEditText_isSingle, true);
            isShowDelete = a.getBoolean(styleable.MarkEditText_isShowDelete, isShowDelete);
            isShowMark = a.getBoolean(styleable.MarkEditText_isShowMark, isShowMark);
            isShowHidden = a.getBoolean(styleable.MarkEditText_isShowHidden, isShowHidden);
            isShowLine = a.getBoolean(styleable.MarkEditText_isShowLine, isShowLine);
            bottomLineColor = a.getColor(styleable.MarkEditText_bottomLineColor, bottomLineColor);
            bottomLineHeight = a.getDimensionPixelSize(styleable.MarkEditText_bottomLineHeight, bottomLineHeight);
            etText = a.getText(styleable.MarkEditText_text);
            textColor = a.getColor(styleable.MarkEditText_textColor, textColor);
            textSize = a.getDimension(styleable.MarkEditText_textSize, textSize);
            etHintText = a.getText(styleable.MarkEditText_hintText);
            count = a.getInt(styleable.MarkEditText_count, count);
            showLengthOverTips = a.getBoolean(styleable.MarkEditText_showLengthOverTips, false);
            isEditEnable = a.getBoolean(styleable.MarkEditText_enable, true);
            strSelect = a.getString(styleable.MarkEditText_select);
            selectRight = a.getBoolean(styleable.MarkEditText_isSelectRight, selectRight);
            icon = a.getDrawable(styleable.MarkEditText_icon);
            a.recycle();
        }

        setCount(count);
        setEtText(etText);
        etContent.setHint(etHintText);
        etContent.setHintTextColor(getResources().getColor(color.xui_config_color_999999));
        etContent.setTextColor(textColor);
        etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        etContent.setBackground(null);

        ivHidden.setVisibility(isShowHidden ? View.VISIBLE : View.GONE);
        ivHidden.setOnClickListener(it -> {
            if (!etContent.isEnabled() && isShowHidden) {
                if (etContent.getText().toString().equals(HIDDEN_STR)) {
                    etContent.setText(defaultText);
                    etContent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivHidden.setImageResource(drawable.ic_common_ic_eye_s);
                } else {
                    etContent.setText(HIDDEN_STR);
                    etContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivHidden.setImageResource(drawable.ic_common_ic_eye_d);
                }
                return;
            }
            if (etContent.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                etContent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivHidden.setImageResource(drawable.ic_common_ic_eye_s);
            } else {
                etContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivHidden.setImageResource(drawable.ic_common_ic_eye_d);
            }
            etContent.setSelection(etContent.getText().length());
        });
        ivDelete.setVisibility(isShowDelete && etContent.getText().length() != 0 ? View.VISIBLE : View.GONE);
        ivDelete.setOnClickListener(it -> {
            etContent.setText("");
            ivDelete.setVisibility(GONE);
        });

        setBottomLineColor(bottomLineColor);

        setEnabled(isEditEnable);

        addTextChangedListener();

        if (!etContent.isEnabled() && isShowHidden) {
            etContent.setText(defaultText);
            ivHidden.performClick();
        }
        updatePadding();
        if (isSingle)
            etContent.setSingleLine(true);
        else {
            etContent.setGravity(Gravity.TOP);
            LinearLayout layAction = findViewById(R.id.lay_action);
            layAction.setGravity(Gravity.BOTTOM);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layAction.getLayoutParams();
            lp.bottomMargin = 0;
            layAction.setLayoutParams(lp);
            etContent.setPadding(etContent.getPaddingLeft(), etContent.getPaddingTop(), 0,
                    count == Integer.MAX_VALUE - 1 ? etContent.getPaddingBottom() : XUiDisplayHelper.dp2px(getContext(), 25));
        }

        bottomLine.setVisibility(isShowLine ? View.VISIBLE : View.GONE);

        setErrorText(errorText);
        setBottomLineHeight(bottomLineHeight);

        setIcon(icon, true);
        setSelect(strSelect, null);
        setSelectRight(selectRight, null, isCanDelete);
    }

    public void setIcon(Drawable icon, boolean isLineBaseEdit) {
        if (icon != null) {
            imgIcon.setImageDrawable(icon);
            imgIcon.setVisibility(View.VISIBLE);
        } else {
            imgIcon.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bottomLine.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_LEFT, isLineBaseEdit ? R.id.edit_text : R.id.img_icon);
        bottomLine.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) errorTipsView.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_LEFT, isLineBaseEdit ? R.id.edit_text : R.id.img_icon);
        errorTipsView.setLayoutParams(lp);
    }

    public void setSelect(String str, List<String> items) {
        this.strSelect = str;
        tvSelect.setText(str);
        tvSelect.setVisibility(TextUtils.isEmpty(str) ? View.GONE : View.VISIBLE);
        tvSelect.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable.ic_common_ic_arrow_down_min, 0);
        tvSelect.setOnClickListener(v -> {
            if (listPopup == null)
                return;
            listPopup.show(tvSelect);
            tvSelect.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable.ic_common_ic_arrow_up_min, 0);
        });
        etContent.postDelayed(() -> {
            if (listPopup == null)
                listPopup = new SimpleCornerPopup(getContext(), XUiDisplayHelper.dp2px(getContext(), 160), XUiDisplayHelper.dp2px(getContext(), 200));
            listPopup.setItems(items, (Functions.Fun4<String>) (item, position) -> {
                listPopup.dismiss();
                tvSelect.setText(item);
            })
                    .animDirection(NormalPopup.FROM_LEFT)
                    .preferredDirection(NormalPopup.DIRECTION_BOTTOM)
                    .setOnDismissListener(() -> tvSelect.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable.ic_common_ic_arrow_down_min, 0));
            listPopup.offsetY(-XUiDisplayHelper.dp2px(getContext(), 6));
            listPopup.dismissIfOutsideTouch(true);
        }, 10);
    }

    public void setSelect(List<String> items) {
        this.setSelect(strSelect, items);
    }

    public void setSelectRight(boolean selectRight, List<String> items, boolean isCanDelete) {
        this.selectRight = selectRight;
        this.itemsRight = items;
        this.isCanDelete = isCanDelete;
        imgSelect.setVisibility(selectRight ? View.VISIBLE : View.GONE);
        imgSelect.setOnClickListener(v -> {
            if (itemsRight == null || itemsRight.size() == 0)
                return;
            if (System.currentTimeMillis() - lastDismissTime < 200)
                return;
            etContent.showDropDown();
            List<String> list = ((ContentAdapterForEdit) etContent.getAdapter()).getList("mObjects");
            if (list != null && list.size() > 0)
                imgSelect.setImageResource(drawable.ic_common_ic_arrow_up_min);
        });
        etContent.setOnDismissListener(() -> {
            imgSelect.setImageResource(drawable.ic_common_ic_arrow_down_min);
            lastDismissTime = System.currentTimeMillis();
        });
        if (items == null || items.size() == 0)
            return;
        etContent.postDelayed(() -> {
            adapterRight = new ContentAdapterForEdit(getContext(), itemsRight, isCanDelete);
            etContent.setAdapter(adapterRight);
        }, 10);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setEditEnabled(enabled);
    }

    private void setEditEnabled(boolean enabled) {
        etContent.setEnabled(enabled);
        if (enabled) {
            if (bottomLine != null) {
                bottomLine.setBackgroundColor(bottomLineColor);
            }
        } else {
            if (bottomLine != null) {
                bottomLine.setBackgroundResource(drawable.common_dash_line);
                bottomLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    private TextWatcher tw = new TextWatcher() {
        public void afterTextChanged(@NonNull Editable s) {
            if (isShowDelete)
                ivDelete.setVisibility(etContent.getText().length() == 0 ? GONE : VISIBLE);
            String overTips = getContext().getString(R.string.length_over_tips);
            if (s.toString().length() == count + 1) {
                if (showLengthOverTips)
                    setErrorText(getContext().getString(R.string.length_over_tips));
                etContent.removeTextChangedListener(tw);
                etContent.setText(etContent.getText().toString().substring(0, count));
                etContent.setSelection(count);
                etContent.addTextChangedListener(tw);
            } else {
                if (overTips.equals(errorTipsView.getText().toString())) {
                    setErrorText("");
                }
            }
            if (TextUtils.isEmpty(errorTipsView.getText().toString()))
                setHelpText(helpText);
            setMarkViewCurrentCount(s.length());
        }

        public void beforeTextChanged(@Nullable CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(@Nullable CharSequence s, int start, int before, int count) {
        }
    };

    private void addTextChangedListener() {
        etContent.addTextChangedListener(tw);
    }

    public void setErrorText(String tips) {
        errorText = tips;
        if (TextUtils.isEmpty(tips)) {
            if (etContent.hasFocus()) {
                if (bottomLine != null) {
                    bottomLine.setBackgroundColor(getContext().getColor(color.xui_config_color_main));
                }
            } else {
                if (bottomLine != null) {
                    bottomLine.setBackgroundColor(getContext().getColor(color.xui_color_eaeaea));
                }
            }
            setErrorTipsVisibility(View.GONE);
        } else {
            bottomLine.setBackgroundColor(getContext().getColor(color.xui_config_color_error));
            errorTipsView.setTextColor(getContext().getColor(R.color.xui_config_color_error));
            setErrorTipsVisibility(View.VISIBLE);
        }
        errorTipsView.setText(tips);
    }

    public void setHelpText(String tips) {
        errorTipsView.setTextColor(getContext().getColor(R.color.xui_color_333333));
        errorTipsView.setText(tips);
        setErrorTipsVisibility(TextUtils.isEmpty(tips) ? View.GONE : View.VISIBLE);
        helpText = tips;
    }

    public void setErrorTipsVisibility(int status) {
        errorTipsView.setVisibility(status);
        if (isSingle)
            return;
        postDelayed(() -> {//多行时 EditText高度受底部错误提示显示状态影响
            if (initHeight == 0) initHeight = getHeight();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) etContent.getLayoutParams();
            int etHeight = initHeight - XUiDisplayHelper.dp2px(getContext(), 1);
            if (status == View.VISIBLE)
                etHeight = etHeight - XUiDisplayHelper.dp2px(getContext(), 26);
            lp.height = etHeight;
            etContent.setLayoutParams(lp);
        }, 10);
    }

    public void setBottomLineColor(int color) {
        bottomLineColor = color;
        bottomLine.setBackgroundColor(color);
    }

    public void setBottomLineHeight(int height) {
        bottomLineHeight = height;
        ViewGroup.LayoutParams layoutParams = bottomLine != null ? bottomLine.getLayoutParams() : null;
        if (layoutParams != null) {
            layoutParams.height = height;
        }
        if (bottomLine != null) {
            bottomLine.setLayoutParams(layoutParams);
        }
    }

    public void setEtText(@NonNull CharSequence etText) {
        defaultText = etText;
        etContent.setText(etText);
    }

    public void setEtHintText(@NonNull CharSequence etHintText) {
        etContent.setHint(etHintText);
    }

    public void setCount(int count) {
        //继续输入第count+1才提示超长错误 所以需要设置可输入count+1
        etContent.setFilters(new InputFilter[]{new LengthFilter(count + 1)});
        setMarkViewCurrentCount(etContent.getText().length());
    }

    public void setShowLengthOverTips(boolean showLengthOverTips) {
        this.showLengthOverTips = showLengthOverTips;
    }

    @NonNull
    public String getText() {
        return etContent.getText().toString();
    }

    @NonNull
    public EditText getEditText() {
        return etContent;
    }

    //多行时etContent滑动需要设置
    public void setDefaultLines(int defaultLines) {
        this.defaultLines = defaultLines;
    }

    public void setMarkViewCurrentCount(int currentCount) {
        if (count == Integer.MAX_VALUE - 1 || !isShowMark) {
            markView.setVisibility(GONE);
            return;
        }
        int tempCount = currentCount;
        if (currentCount < 0) {
            tempCount = 0;
        } else if (currentCount > count) {
            tempCount = count;
        }
        markView.setText("" + tempCount + '/' + count);
        if (!isSingle)
            markView.setPadding(XUiDisplayHelper.dp2px(getContext(), 2), XUiDisplayHelper.dp2px(getContext(), 4), XUiDisplayHelper.dp2px(getContext(), 2), XUiDisplayHelper.dp2px(getContext(), 4));
    }

    /**
     * 设置editText右padding防止被lay_action覆盖
     */
    private void updatePadding() {
        if (!isSingle)
            return;
        int paddingDpValue = 0;
        if (isShowDelete) {
            paddingDpValue += 25;
        }
        if (isShowMark && count != Integer.MAX_VALUE - 1) {
            paddingDpValue += 18 * String.valueOf(count).length() + 8;
        }
        if (isShowHidden) {
            paddingDpValue += 28;
        }
        etContent.setPadding(etContent.getPaddingLeft(), etContent.getPaddingTop(), XUiDisplayHelper.dp2px(getContext(), paddingDpValue), etContent.getPaddingBottom());
    }

}
