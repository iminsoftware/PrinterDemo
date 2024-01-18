package com.feature.tui.dialog.builder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.feature.tui.R;
import com.feature.tui.demo.adapter.BaseDataBindingAdapter;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.util.XUiResHelper;

import java.util.ArrayList;
import java.util.List;

public class InputDialogBuilder
        extends BaseDialogBuilder<InputDialogBuilder> implements View.OnLayoutChangeListener {
    private final int MSG_SOFT_KEY_CHANGE = 11;
    private Handler handler;
    public DialogInputLayoutBinding binding;
    private CharSequence hintContentText;
    private CharSequence hintInputText;
    private CharSequence inputText;
    private CharSequence errorText;
    private boolean hasFocus;
    private int count = Integer.MAX_VALUE;
    private boolean isShowKeyboard = true;
    private long delayMillis = 100L;
    private List<TextWatcher> textWatcherList;
    private XUiDialogAction submitAction;
    private boolean showDelete;
    private boolean isSoftKeyShow;
    private int screenHeight;
    private String brTips;
    private int brColor;
    private View.OnClickListener brListener;
    private boolean showLengthOverTips;
    private static final String TAG = "InputDialogBuilder";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context) {
        binding = new DialogInputLayoutBinding(LayoutInflater.from(context).inflate(R.layout.dialog_input_layout, parent, false));
        binding.inputEt.requestFocus();
        hasFocus = true;
        if (isShowKeyboard) {
            binding.inputEt.postDelayed(() -> {
                XUiResHelper.showKeyboard(context, binding.inputEt);
            }, delayMillis);
        }
        binding.inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.count)});
        setEditViewFocusChangeListener();
        addEditTextChangedListener();
        setClearBtnOnClickListener();
        hintContentText(hintContentText);
        errorText(errorText);
        setBrTips(brTips, brColor, brListener);
        binding.inputEt.setHint(hintInputText);
        if (!TextUtils.isEmpty(inputText)) {
            binding.inputEt.setText(this.inputText);
            binding.inputEt.setSelection(inputText.length());
        }
        dialog.setOnKeyListener((dialog_, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                int[] a = new int[2];
                binding.inputEt.getLocationOnScreen(a);
                binding.inputEt.postDelayed(() -> {
                    int[] b = new int[2];
                    binding.inputEt.getLocationOnScreen(b);
                    if (a[1] == b[1]) {
                        dialog.dismiss();
                    }
                }, 100);
            }
            return false;

        });
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (showDelete) {
            binding.inputEt.setPadding(binding.inputEt.getPaddingLeft(), binding.inputEt.getPaddingTop(), XUiDisplayHelper.dp2px(context, 14), binding.inputEt.getPaddingBottom());
        }
        updateIconGravity();
        return binding.itemView;
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public XUiDialog create() {
        setTitleContentTogetherForScroll(true);
        XUiDialog dialog = super.create();
        screenHeight = XUiDisplayHelper.getScreenHeight(dialog.getContext());
        binding.inputEt.postDelayed(() -> {
            if (submitAction != null) {
                submitAction.setEnabled(binding.inputEt.getText().toString().isEmpty() != true);
            }
            showKeyboard(binding.inputEt);
            if (getMaxContentScrollView() != null) {
                getMaxContentScrollView().setMaxHeight((int) (screenHeight * 0.5));
            }
            mDialogView.addOnLayoutChangeListener(InputDialogBuilder.this);
        }, 10);
        return dialog;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_SOFT_KEY_CHANGE) {
                        mDialogView.removeOnLayoutChangeListener(InputDialogBuilder.this);
                        getMaxContentScrollView().setMaxHeight((int) (isSoftKeyShow ? screenHeight * 0.5 : screenHeight * 0.86));
                        mDialogView.postDelayed(() -> mDialogView.addOnLayoutChangeListener(InputDialogBuilder.this), 120);
                    }
                }
            };
        }

        int[] a = new int[2];
        getActionView(0).getLocationOnScreen(a);
        if (a[1] > screenHeight * 0.7) {//softKey hidden
            isSoftKeyShow = false;
        } else {
            isSoftKeyShow = true;
        }
        if (!handler.hasMessages(MSG_SOFT_KEY_CHANGE)) {
            handler.sendEmptyMessageDelayed(MSG_SOFT_KEY_CHANGE, 100);
        }
    }

    @Override
    public InputDialogBuilder addAction(CharSequence str, int prop, int strColorRes, int strSizeRes, Functions.Fun1 listener) {
        XUiDialogAction action = new XUiDialogAction(str, listener, prop, strColorRes, strSizeRes);
        addAction(action);
        if (prop == XUiDialogAction.ACTION_PROP_POSITIVE) {
            submitAction = action;
        }
        return this;
    }

    @Override
    public InputDialogBuilder addAction(int strRes, int prop, int strColorRes, int strSizeRes, Functions.Fun1 listener) {
        Context context = getBaseContext();
        String str = context != null && context.getResources() != null ? context.getString(strRes) : null;
        return addAction(str, prop, strColorRes, strSizeRes, listener);
    }

    private void setEditViewFocusChangeListener() {
        binding.inputEt.setOnFocusChangeListener((v, focus) -> {
            hasFocus = focus;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addEditTextChangedListener() {
        if (textWatcherList != null)
            textWatcherList.forEach((it) -> {
                binding.inputEt.addTextChangedListener(it);
            });
        binding.inputEt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    if (showDelete) {
                        binding.inputEtClear.setVisibility(View.VISIBLE);
                    }
                    if (submitAction != null)
                        submitAction.setEnabled(true);
                } else {
                    binding.inputEtClear.setVisibility(View.GONE);
                    if (submitAction != null)
                        submitAction.setEnabled(false);
                }

                if (s.toString().length() == count && showLengthOverTips) {
                    setErrorText(getBaseContext().getString(R.string.length_over_tips));
                } else {
                    setErrorText("");
                }
                updateIconGravity();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });
    }

    /**
     * 单行与多行删除小图标位置不一样
     */
    private void updateIconGravity() {
        int lines = binding.inputEt.getLineCount();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.inputEtClear.getLayoutParams();
        if (lines <= 1) {
            lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = XUiDisplayHelper.dp2px(getBaseContext(), 8);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.bottomMargin = 0;
        }
        binding.inputEtClear.setLayoutParams(lp);
    }

    /**
     * 添加EditText的内容改变监听
     */
    public InputDialogBuilder addEditTextChangedListener(TextWatcher textWatcher) {
        if (textWatcherList == null) {
            textWatcherList = new ArrayList();
        }
        textWatcherList.add(textWatcher);
        if (binding != null && binding.inputEt != null)
            binding.inputEt.addTextChangedListener(textWatcher);
        return this;
    }

    /**
     * 移除EditText的内容改变监听
     */
    public void removeEditTextChangedListener(TextWatcher textWatcher) {
        if (textWatcherList != null)
            textWatcherList.remove(textWatcher);
        binding.inputEt.removeTextChangedListener(textWatcher);
    }

    private void setClearBtnOnClickListener() {
        binding.inputEtClear.setOnClickListener((v) -> {
            binding.inputEt.setText("");
        });
    }

    private void hintContentText(CharSequence hintContentText) {
        if (binding == null || binding.inputHintTv == null)
            return;
        if (TextUtils.isEmpty(hintContentText)) {
            binding.inputHintTv.setVisibility(View.GONE);
        } else {
            binding.inputHintTv.setVisibility(View.VISIBLE);
            binding.inputHintTv.setText(hintContentText);
        }
    }

    private void errorText(CharSequence errorText) {
        if (binding == null || binding.inputErrorTv == null || binding.inputEtLine == null) {

            Log.e("TAG", "errorText: ");
            return;
        }
        Log.e("TAG", "errorText: " + hasFocus + TextUtils.isEmpty(errorText) );
        if (TextUtils.isEmpty(errorText)) {
            binding.inputErrorTv.setVisibility(View.INVISIBLE);
            if (hasFocus)
                binding.inputEtLine.setBackgroundResource(R.color.xui_config_color_main);
            else
                binding.inputEtLine.setBackgroundResource(R.color.xui_color_eaeaea);
        } else {
            binding.inputErrorTv.setVisibility(View.VISIBLE);
            binding.inputErrorTv.setText(errorText);
            binding.inputEtLine.setBackgroundResource(R.color.xui_config_color_error);
        }
    }

    /**
     * 获取EditText中的内容
     */
    public String getInputText() {
        return binding.inputEt.getText().toString();
    }

    /**
     * 获取EditText
     */
    public EditText getEditText() {
        return binding.inputEt;
    }

    /**
     * 设置提示标题文字
     */
    public InputDialogBuilder setHintContentText(CharSequence hintContentText) {
        this.hintContentText = hintContentText;
        hintContentText(hintContentText);
        return this;
    }

    /**
     * 设置错误提示文字
     */
    public InputDialogBuilder setErrorText(CharSequence errorText) {
        this.errorText = errorText;
        Log.d(TAG, "setErrorText: errorText 222");

        errorText(errorText);
        return this;
    }

    /**
     * 设置EditText的提示文字
     */
    public InputDialogBuilder setHintInputText(CharSequence hintInputText) {
        this.hintInputText = hintInputText;
        if (binding != null && binding.inputEt != null)
            binding.inputEt.setHint(inputText);
        return this;
    }

    /**
     * 设置EditText的文字
     */
    public InputDialogBuilder setInputText(CharSequence inputText) {
        this.inputText = inputText;
        if (binding != null && binding.inputEt != null) {
            binding.inputEt.setText(inputText);
            binding.inputEt.setSelection(inputText.length());
        }
        return this;
    }

    /**
     * 设置EditText的输入文字长度
     */
    public InputDialogBuilder setEditTextCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * 是否自动弹起软键盘，如果创建视图的时候弹起键盘是无效的，所以需要延时调用
     */
    public InputDialogBuilder isShowKeyboardAndDelay(boolean isShowKeyboard, long delayMillis) {
        this.isShowKeyboard = isShowKeyboard;
        this.delayMillis = delayMillis;
        return this;
    }

    /**
     * 是否显示删除小图标
     */
    public InputDialogBuilder isShowDelete(boolean isShow) {
        this.showDelete = isShow;
        return this;
    }

    public InputDialogBuilder isShowLengthOverTips(boolean showLengthOverTips) {
        this.showLengthOverTips = showLengthOverTips;
        return this;
    }

    /**
     * 设置右小角TextView
     *
     * @param tips
     * @param color
     * @param listener
     * @return
     */
    public InputDialogBuilder setBrTips(String tips, int color, View.OnClickListener listener) {
        this.brTips = tips;
        this.brColor = color;
        this.brListener = listener;
        if (binding != null && binding.inputBrTipsTv != null) {
            binding.inputBrTipsTv.setText(tips);
            if (color != 0)
                binding.inputBrTipsTv.setTextColor(color);
            binding.inputBrTipsTv.setOnClickListener(listener);
            binding.inputBrTipsTv.setVisibility(TextUtils.isEmpty(tips) ? View.GONE : View.VISIBLE);
        }
        return this;
    }

    public InputDialogBuilder(Context context) {
        super(context);
    }

    public class DialogInputLayoutBinding extends BaseDataBindingAdapter.BaseBindingViewHolder {

        public EditText inputEt;
        public ImageView inputEtClear;
        public TextView inputHintTv;
        public TextView inputErrorTv;
        public TextView inputBrTipsTv;
        public View inputEtLine;

        DialogInputLayoutBinding(View view) {
            super(view);
            inputEt = view.findViewById(R.id.input_et);
            inputEtClear = view.findViewById(R.id.input_et_clear);
            inputHintTv = view.findViewById(R.id.input_hint_tv);
            inputErrorTv = view.findViewById(R.id.input_error_tv);
            inputBrTipsTv = view.findViewById(R.id.input_br_tips_tv);
            inputEtLine = view.findViewById(R.id.input_et_line);
        }

    }

    @Override
    protected LinearLayout.LayoutParams onCreateOperatorLayoutLayoutParams(Context context) {
        return super.onCreateOperatorLayoutLayoutParams(context);

    }
}
