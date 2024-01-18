package com.feature.tui.dialog.builder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.StyleRes;

import com.feature.tui.R;
import com.feature.tui.dialog.Functions;
import com.feature.tui.dialog.center.XUiDialog;
import com.feature.tui.dialog.center.XUiDialogAction;
import com.feature.tui.util.XUiDisplayHelper;
import com.feature.tui.util.XUiResHelper;
import com.feature.tui.widget.layout.MaxContentScrollView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseDialogBuilder<T> {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int DRAWABLE_BOTTOM = 0;
    public static final int DRAWABLE_TOP = 1;
    public static final int DRAWABLE_LEFT = 2;
    public static final int DRAWABLE_RIGHT = 3;
    public static final int DIALOG_GRAVITY_TOP = 0;
    public static final int DIALOG_GRAVITY_CENTER = 1;
    public static final int DIALOG_GRAVITY_BOTTOM = 2;


    private Context baseContext;
    private XUiDialog mDialog;
    private String mTitle;
    private int mTitleColorRes;
    private int mTitleSizeRes;
    private boolean mTitleIsCenter;
    private int mTitlePaddingTop;
    private int mDialogGravity = DIALOG_GRAVITY_CENTER;
    private int mTitlePaddingBottom;

    private boolean mCancelable;
    private boolean mCanceledOnTouchOutside;
    protected LinearLayout mDialogView;
    private int mTitleIcon;
    private int mTitleIconPadding;
    private int mTitleDrawableOrientation;

    private View titleView;
    private List<XUiDialogAction> mActions;
    private int mActionContainerOrientation;
    private int mActionDividerColor;
    private int mDialogMarginBottom;
    private int mDialogMarginLeftOrRight;

    private boolean isTitleContentTogetherForScroll = false;
    private MaxContentScrollView maxContentScrollView;
    private boolean titleWhiteBg;
    private int secondLimit;

    protected  XUiDialog getMDialog() {
        return mDialog;
    }

    protected  void setMDialog(XUiDialog mUiDialog) {
        mDialog = mUiDialog;
    }

    protected  int getMTitleIcon() {
        return mTitleIcon;
    }

    protected  void setMTitleIcon(int n) {
        mTitleIcon = n;
    }

    protected  int getMTitleIconPadding() {
        return mTitleIconPadding;
    }

    protected  void setMTitleIconPadding(int n) {
        mTitleIconPadding = n;
    }

    protected  int getMTitleDrawableOrientation() {
        return mTitleDrawableOrientation;
    }

    protected  void setMTitleDrawableOrientation(int n) {
        mTitleDrawableOrientation = n;
    }

    public  View getTitleView() {
        return titleView;
    }

    public  void setTitleView(View view) {
        titleView = view;
    }

    public void setTitleContentTogetherForScroll(boolean titleContentTogetherForScroll) {
        isTitleContentTogetherForScroll = titleContentTogetherForScroll;
    }

    public MaxContentScrollView getMaxContentScrollView() {
        return maxContentScrollView;
    }

    /**
     * 设置对话框顶部的标题文字
     *
     * @param titleColorRes=R.color.xxx, 为0时表示用默认的
     * @param titleSizeRes=R.dimen.xxx,  为0时表示用默认的
     * @param titleIsCenter              默认居中显示
     */
    public  T setTitle(String title, int titleColorRes, int titleSizeRes, boolean titleIsCenter, int titleIcon, int titleIconPadding, int drawableOrientation) {
        this.mTitle = title;
        this.mTitleColorRes = titleColorRes;
        this.mTitleSizeRes = titleSizeRes;
        this.mTitleIsCenter = titleIsCenter;
        this.mTitleIcon = titleIcon;
        this.mTitleIconPadding = titleIconPadding;
        this.mTitleDrawableOrientation = drawableOrientation;
        return (T) this;
    }

    public  T setTitle(String title) {
        return this.setTitle(title, 0, 0, true, 0, 0, DRAWABLE_BOTTOM);
    }

    /**
     * 设置对话框顶部的标题文字
     *
     * @param titleColorRes 文字颜色 R.color.xxx, 为0时表示用默认的
     * @param titleSizeRes  文字大小 R.dimen.xxx, 为0时表示用默认的
     * @param titleIsCenter 标题是否居中显示,默认居中显示
     */
    public  T setTitle(int resId, int titleColorRes, int titleSizeRes, boolean titleIsCenter, int titleIcon, int titleIconPadding, int drawableOrientation) {
        Context context = this.baseContext;
        return this.setTitle(context.getString(resId), titleColorRes, titleSizeRes, titleIsCenter, titleIcon, titleIconPadding, drawableOrientation);
    }

    /**
     * 设置标题底部padding 为0时表示用默认的
     */
    public  T setDialogGravity(int gravity) {
        this.mDialogGravity = gravity;
        return (T) this;
    }

    /**
     * 设置标题与上边的距离，为0时表示用默认的
     */
    public  T setTitlePaddingTop(int paddingTop) {
        this.mTitlePaddingTop = paddingTop;
        return (T) this;
    }

    /**
     * 设置标题底部padding 为0时表示用默认的
     */
    public  T setTitlePaddingBottom(int paddingBottom) {
        this.mTitlePaddingBottom = paddingBottom;
        return (T) this;
    }

    public  T setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        return (T) this;
    }

    public  T setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.mCanceledOnTouchOutside = canceledOnTouchOutside;
        return (T) this;
    }

    /**
     * 设置操纵按钮的排列方向，默认水平排列
     */
    public  T setActionContainerOrientation(int actionContainerOrientation) {
        this.mActionContainerOrientation = actionContainerOrientation;
        return (T) this;
    }

    /**
     * 设置操纵按钮分割线的颜色
     */
    public  T setActionDividerColor(int color) {
        this.mActionDividerColor = color;
        return (T) this;
    }

    /**
     * 设置对话框距离底部的距离，默认20dp
     */
    public  T setDialogMarginBottom(int dialogMarginBottom) {
        this.mDialogMarginBottom = dialogMarginBottom;
        return (T) this;
    }

    /**
     * 设置对话框距离左右两边的距离，默认20dp
     */
    public  T setDialogMarginLeftOrRight(int dialogMarginLeftOrRight) {
        this.mDialogMarginLeftOrRight = dialogMarginLeftOrRight;
        return (T) this;
    }


    /**
     * 添加对话框底部的操作按钮
     */
    public  T addAction(XUiDialogAction action) {
        if (action != null) {
            this.mActions.add(action);
        }
        return (T) this;
    }

    /**
     * 添加操作按钮
     *
     * @param strRes                   文字
     * @param prop                     按钮的权限
     * @param strColorRes=R.color.xxx, 为0时表示用默认的
     * @param strSizeRes=R.dimen.xxx,  为0时表示用默认的
     * @param listener                 点击回调事件
     */
    public T addAction(int strRes, int prop, int strColorRes, int strSizeRes, Functions.Fun1 listener) {
        return addAction(baseContext.getString(strRes), prop, strColorRes, strSizeRes, listener);
    }


    /**
     * 添加操作按钮
     *
     * @param str                      文字
     * @param prop                     按钮的权限
     * @param strColorRes=R.color.xxx, 为0时表示用默认的
     * @param strSizeRes=R.dimen.xxx,  为0时表示用默认的
     * @param listener                 点击回调事件
     */
    public T addAction(CharSequence str, int prop, int strColorRes, int strSizeRes, Functions.Fun1 listener) {
        XUiDialogAction action = new XUiDialogAction(str, listener, prop, strColorRes, strSizeRes);
        this.mActions.add(action);
        return (T) this;
    }

    public T addAction(CharSequence str, Functions.Fun1 listener) {
        return this.addAction(str, XUiDialogAction.ACTION_PROP_NEUTRAL, 0, 0, listener);
    }

    public T addAction(CharSequence str, int prop, Functions.Fun1 listener) {
        return this.addAction(str, prop, 0, 0, listener);
    }

    /**
     * 得到一个操纵View
     */
    public  TextView getActionView(int index) {
        Collection collection = this.mActions;
        if (!collection.isEmpty() && index < this.mActions.size()) {
            return this.mActions.get(index).getButton();
        }
        return null;
    }

    /**
     * 设置对话框title背景色
     */
    public  T setTitleWhiteBg(boolean isWhitBg) {
        this.titleWhiteBg = isWhitBg;
        return (T) this;
    }


    /**
     * 设置对话框倒计时dismiss
     * second
     */
    public  T setDismissLimitTime(int secondLimit) {
        this.secondLimit = secondLimit;
        return (T) this;
    }

    /**
     * 产生一个Dialog并显示出来
     */
    public  XUiDialog show() {
        XUiDialog mUiDialog = create();
        if (mUiDialog != null)
            mUiDialog.show();
        return mUiDialog;
    }

    /**
     * 只产生一个 Dialog, 不显示出来
     */
    public XUiDialog create() {
        return create(R.style.XUI_BaseDialog);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View getLineLayout(Context context) {

        LinearLayout layout = new LinearLayout(context);
        layout.setId(R.id.xui_dialog_operator_layout);

        LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lineLp.height = 1;
        View lineView = new View(context);

        layout.setBackground(context.getDrawable(R.drawable.bottom_line_bg));
        layout.addView(lineView, lineLp);



        return layout;
    }

    /**
     * 产生一个Dialog，不显示出来。
     */
    public  XUiDialog create(@StyleRes int style) {
        mDialog = new XUiDialog(baseContext, style);
        Context context = mDialog.getContext();
        mDialogView = onCreateDialogView(context);
        titleView = onCreateTitle(mDialog, mDialogView, context);
        View operatorLayout = onCreateOperatorLayout(mDialog, mDialogView, context);
        View contentLayout = onCreateContent(mDialog, mDialogView, context);
        View lineLayout = getLineLayout(context);

        LinearLayout.LayoutParams lpTitle = onCreateTitleLayoutParams(context);
        LinearLayout.LayoutParams lpContent = onCreateContentLayoutParams(context);
        LinearLayout.LayoutParams lpLine = onCreateLineLayoutParams(context);
        if (isTitleContentTogetherForScroll) {
            LinearLayout togetherLay = new LinearLayout(context);
            togetherLay.setOrientation(LinearLayout.VERTICAL);
            if (titleView != null) {
                togetherLay.addView(titleView, lpTitle);
            }
            if (contentLayout != null) {
                togetherLay.addView(contentLayout, lpContent);
            }
            maxContentScrollView = (MaxContentScrollView) wrapWithScroll(togetherLay, XUiDisplayHelper.dp2px(context, 240));
            maxContentScrollView.setVerticalScrollBarEnabled(false);
//            maxContentScrollView.setPadding(0, XUiDisplayHelper.dp2px(context, 6), 0, 0);
            mDialogView.addView(maxContentScrollView);
        } else {
            if (titleView != null) {
                mDialogView.addView(titleView, lpTitle);
            }
            if (contentLayout != null) {
                mDialogView.addView(contentLayout, lpContent);
            }
        }

        if (lineLayout != null) {
            mDialogView.addView(lineLayout, lpLine);
        }

        if (operatorLayout != null) {
            if (mActionContainerOrientation == VERTICAL && ((titleView != null && titleView.getVisibility() == View.VISIBLE)
                    || contentLayout != null)
            ) {
                mDialogView.addView(createActionHorizontalDivider(context));
            }
            LinearLayout.LayoutParams lp = onCreateOperatorLayoutLayoutParams(context);
            mDialogView.addView(operatorLayout, lp);
        }
        int screenWidth = XUiDisplayHelper.getScreenWidth(baseContext);
        int screenHeight = XUiDisplayHelper.getScreenHeight(baseContext);

        int width = screenWidth - mDialogMarginLeftOrRight * 2;
        if (screenWidth > screenHeight) {
            width = screenWidth * 3 / 10;
        }

        mDialog.addContentView(mDialogView, new ViewGroup.LayoutParams(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        //设置显示在底部
        if (mDialogGravity == DIALOG_GRAVITY_TOP) {

            mDialog.getWindow().setGravity(Gravity.TOP);
        } else if (mDialogGravity == DIALOG_GRAVITY_BOTTOM) {
            mDialog.getWindow().setGravity(Gravity.BOTTOM);
        } else {
            mDialog.getWindow().setGravity(Gravity.CENTER);
        }
        WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();
        //距离底部的位置
        p.y = mDialogMarginBottom;
        mDialog.setCancelable(mCancelable);
        mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        return mDialog;
    }

    /**
     * 获取当前的dialog，只有走了create方法之后才会有实体
     */
    public  Dialog getDialog() {
        return this.mDialog;
    }

    /**
     * 创建一个dialog的根布局View
     */
    protected  LinearLayout onCreateDialogView(Context context) {
        LinearLayout dialogView = new LinearLayout(context);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setId(R.id.xui_dialog_layout);
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.XUIDialogRootLayoutStyle, R.attr.xui_dialog_root_layout_style, 0);
        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingTop = 0;
        int paddingBottom = 0;
        int n = 0;
        while (n < a.getIndexCount()) {
            int attr2 = a.getIndex(n);
            if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_background) {
                dialogView.setBackgroundResource(a.getResourceId(attr2, 0));
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_paddingLeft) {
                paddingLeft = a.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_paddingRight) {
                paddingRight = a.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_paddingTop) {
                paddingTop = a.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_paddingBottom) {
                paddingBottom = a.getDimensionPixelSize(attr2, 0);
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_minHeight) {
                dialogView.setMinimumHeight(a.getDimensionPixelSize(attr2, 0));
            } else if (attr2 == R.styleable.XUIDialogRootLayoutStyle_android_minWidth) {
                dialogView.setMinimumWidth(a.getDimensionPixelSize(attr2, 0));
            }
            ++n;
        }
        dialogView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        a.recycle();
        return dialogView;
    }

    /**
     * 创建一个标题View
     */
    protected  View onCreateTitle(XUiDialog dialog, LinearLayout parent, Context context) {
        TextView tv = new TextView(context);
        tv.setId(R.id.xui_dialog_title);
        tv.setText(mTitle);
        XUiResHelper.assignTextViewWithAttr(tv, R.attr.xui_dialog_title_style);
        try {
            if (mTitleColorRes > 0) {
                tv.setTextColor(tv.getResources().getColorStateList(mTitleColorRes));
            }
            if (mTitleSizeRes > 0) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getResources().getDimension(mTitleSizeRes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mTitleIsCenter) {
            tv.setGravity(Gravity.CENTER);
        } else {
            tv.setGravity(Gravity.START);
        }

        if (titleWhiteBg) {

            tv.setBackground(context.getDrawable(R.drawable.white_bg_dialog_title));
        } else {

            tv.setBackground(context.getDrawable(R.drawable.bg_dialog_title));
        }
//        tv.setBackgroundResource(R.color.xui_config_color_error_press);
        if (mTitlePaddingBottom == 0) {

            mTitlePaddingBottom = XUiDisplayHelper.dp2px(context, 20);
        }

        tv.setPadding(tv.getPaddingLeft(), mTitlePaddingTop > 0 ? mTitlePaddingTop : tv.getPaddingTop(), tv.getPaddingRight(), mTitlePaddingBottom > 0 ? mTitlePaddingBottom : tv.getPaddingBottom());
        if (mTitleIcon > 0) {
            if (mTitleIconPadding > 0) {
                tv.setCompoundDrawablePadding(mTitleIconPadding);
            }
            switch (mTitleDrawableOrientation) {
                case DRAWABLE_LEFT: {
                    tv.setCompoundDrawablesWithIntrinsicBounds(this.mTitleIcon, 0, 0, 0);
                    break;
                }
                case DRAWABLE_TOP: {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, this.mTitleIcon, 0, 0);
                    break;
                }
                case DRAWABLE_RIGHT: {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, this.mTitleIcon, 0);
                    break;
                }
                case DRAWABLE_BOTTOM: {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, this.mTitleIcon);
                    break;
                }
            }
        }
        if (TextUtils.isEmpty(mTitle) && mTitleIcon <= 0) {
            tv.setVisibility(View.GONE);
        }
        return tv;
    }

    /**
     * 标题View的布局参数
     */
    protected LinearLayout.LayoutParams onCreateTitleLayoutParams(Context context) {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 创建一个内容View
     */
    protected abstract View onCreateContent(XUiDialog dialog, LinearLayout parent, Context context);

    /**
     * 得到一个可滑动View
     */
    protected  ScrollView wrapWithScroll(View view, int maxHeight) {
        MaxContentScrollView scrollView = (MaxContentScrollView) LayoutInflater.from(baseContext).inflate(R.layout.max_scroll_view, null);
        scrollView.setMaxHeight(maxHeight <= 0 ? XUiDisplayHelper.getScreenHeight(view.getContext()) * 2 / 3 : maxHeight);
        scrollView.addView(view);
        int pLeft = view.getPaddingLeft();
        int pTop = view.getPaddingTop();
        int pRight = view.getPaddingRight();
        int pBottom = view.getPaddingBottom();
        view.postDelayed(() -> {
            scrollView.setPadding(pLeft,
                    pTop,
                    view.getHeight() > maxHeight ? XUiDisplayHelper.dp2px(baseContext, 8f) :
                            pRight,
                    pBottom);
        }, 10L);
        scrollView.setPadding(view.getPaddingLeft(), view.getPaddingTop(), XUiDisplayHelper.dp2px(this.baseContext, 8.0f), view.getPaddingBottom());
        view.setPadding(0, 0, 0, 0);
        return scrollView;
    }

    /**
     * 内容View的布局参数
     */
    protected LinearLayout.LayoutParams onCreateContentLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        return layoutParams;
    }

    /**
     * 内容View的布局参数
     */
    protected LinearLayout.LayoutParams onCreateLineLayoutParams(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
//        layoutParams.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_10);
        return layoutParams;
    }

    /**
     * 得到一个操纵View
     */
    protected  View onCreateOperatorLayout(XUiDialog dialog, LinearLayout parent, Context context) {
        int size = mActions.size();
        if (size > 0) {
            LinearLayout layout = new LinearLayout(context);
            layout.setId(R.id.xui_dialog_operator_layout);
            int n = 0;
            while (n < size) {
                XUiDialogAction action = mActions.get(n);
                TextView actionView = action.buildActionView(mDialog, n);
                LinearLayout.LayoutParams actionLp = null;
                if (mActionContainerOrientation == VERTICAL) {
                    layout.setOrientation(LinearLayout.VERTICAL);
                    actionLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    actionView.setPadding(0, 0, 0, 0);
                } else {
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    actionLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    actionLp.weight = 1.0f;
                }
                layout.addView((View) actionView, (ViewGroup.LayoutParams) actionLp);
                if (n < size - 1) {
                    if (mActionContainerOrientation == VERTICAL) {
                        layout.addView(createActionHorizontalDivider(context));
                    } else {
                        layout.addView(createActionVerticalDivider(context));
                    }
                }
                ++n;
            }
            return layout;
        }
        return null;
    }

    /**
     * 操纵View的布局参数
     */
    protected  LinearLayout.LayoutParams onCreateOperatorLayoutLayoutParams(Context context) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_10);
        return layoutParams;
    }

    /**
     * 创建水平方向的分割线
     */
    private  View createActionHorizontalDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(mActionDividerColor);
        LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.xui_module_divider));
        divider.setLayoutParams(dividerLp);
        return divider;
    }

    /**
     * 创建垂直方向的分割线
     */
    private  View createActionVerticalDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(mActionDividerColor);
        LinearLayout.LayoutParams dividerLp = new LinearLayout.LayoutParams(XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.xui_dialog_action_divider_with), XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.xui_dialog_action_divider_height));
        dividerLp.gravity = Gravity.CENTER;
        dividerLp.bottomMargin = XUiDisplayHelper.getDimensionPixelToId(context, R.dimen.dp_4);
        divider.setLayoutParams((ViewGroup.LayoutParams) dividerLp);
        return divider;
    }

    public  Context getBaseContext() {
        return this.baseContext;
    }

    public BaseDialogBuilder(Context baseContext) {
        this.baseContext = baseContext;
        this.mTitleIsCenter = true;
        this.mCancelable = true;
        this.mCanceledOnTouchOutside = true;
        this.mActions = new ArrayList();
        this.mActionDividerColor = baseContext.getResources().getColor(R.color.xui_color_eaeaea);
        this.mDialogMarginBottom = XUiDisplayHelper.getDimensionPixelToId(this.baseContext, R.dimen.xui_dialog_margin_bottom_distance);
        this.mDialogMarginLeftOrRight = XUiDisplayHelper.getDimensionPixelToId(this.baseContext, R.dimen.xui_dialog_margin_left_or_right);
    }

}
