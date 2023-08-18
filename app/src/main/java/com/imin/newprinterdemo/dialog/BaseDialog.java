package com.imin.newprinterdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinterdemo.R;

public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.select_dialog);

    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

     ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    @Override
    public void show() {
        super.show();
        initDialog(380, WindowManager.LayoutParams.WRAP_CONTENT, Gravity.CENTER,false);

    }

    /**
     * 初始化Dialog
     * @param width 设置宽度
     * @param height 设置高度
     * @param gravity 设置显示位置
     * @param isCancelable 是否禁用back键
     * @param  //animation 设置动画资源文件
     * @param  //isAnimation 设置是否开启动画
     */
    public void initDialog(int width, int height, int gravity, boolean isCancelable/*,int animation, boolean isAnimation*/) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //设置宽
        switch (width) {
            case WindowManager.LayoutParams.MATCH_PARENT:
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                break;
            case WindowManager.LayoutParams.WRAP_CONTENT:
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            default:
                layoutParams.width = (int) dp2px(getContext(), width);
                break;
        }
        //设置高
        switch (height) {
            case WindowManager.LayoutParams.MATCH_PARENT:
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                break;
            case WindowManager.LayoutParams.WRAP_CONTENT:
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            default:
                layoutParams.height = (int) dp2px(getContext(), width);
                break;
        }
        //设置显示位置
        layoutParams.gravity = gravity;
        //设置是否屏蔽返回键与点击空白区域不关闭Dialog
        setCancelable(isCancelable);
        //设置是否开启动画
//        if (isAnimation) {
            //如果动画资源为空,则设置成默认动画
//            if (animation != 0) {
//                layoutParams.windowAnimations = animation;
//            } else {
//                layoutParams.windowAnimations = R.style.Animation_Design_BottomSheetDialog;
//            }
//        }
        //设置属性
        getWindow().setAttributes(layoutParams);
    }

    public float dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public interface ClickListener{
        void dismiss();
        void selectItem(String s,int i);
        void cancel();
        void sure(String s);
    }
   public String title= getContext().getString(R.string.input_text);
    public BaseDialog setTitle(String title){
        this.title = title;
        return this;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
