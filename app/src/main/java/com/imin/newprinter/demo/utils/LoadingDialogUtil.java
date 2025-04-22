package com.imin.newprinter.demo.utils;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.imin.newprinter.demo.view.LoadingDialog;
/**
 * @Author: hy
 * @date: 2025/4/21
 * @description:
 */


public class LoadingDialogUtil {
    // 单例实例
    private static final LoadingDialogUtil INSTANCE = new LoadingDialogUtil();
    private LoadingDialog loadingDialog;
    private int loadingCount = 0;

    // 私有构造方法（单例模式）
    private LoadingDialogUtil() {}

    public static LoadingDialogUtil getInstance() {
        return INSTANCE;
    }

    // 带配置参数的show方法
    public void show(@NonNull Context context,LoadingConfig configObj) {

//        if (loadingDialog == null) {
//            loadingDialog = new LoadingDialog(context);
//            loadingDialog.setAnimationType(configObj.getAnimationType());
//            loadingDialog.setText(configObj.getText());
//            if (loadingDialog.getWindow() != null) {
//                loadingDialog.getWindow().setDimAmount(configObj.getDimAmount());
//            }
//            TextView textView = (TextView) loadingDialog.findViewById(R.id.tv_text);
//            if (textView != null) {
//                textView.setTextColor(configObj.getTextColor());
//            }
//        }
//
//        if (loadingCount == 0) {
//            loadingDialog.show();
//        }
//        loadingCount++;
    }

    // 重载默认配置的show方法
    public void show(@NonNull Context context,String text) {
        loadingDialog = new LoadingDialog(context)
                .setText(text)
                .setAnimationType(LoadingDialog.AnimationType.SPIN);

        loadingDialog.show();

    }

    public void hide() {
//        if (loadingCount > 0) {
//            loadingCount--;
//        }
//        if (loadingCount == 0 && loadingDialog != null) {
//            loadingDialog.dismiss();
//            loadingDialog = null;
//        }
        // 关闭对话框
        if (loadingDialog != null){
            loadingDialog.dismiss();
        }

    }

    // 配置类（静态内部类）
    public static class LoadingConfig {
        private String text = "加载中...";
        private int animationType = LoadingDialog.AnimationType.SPIN.ordinal();
        private int textColor = Color.WHITE;
        private float dimAmount = 0.5f;

        // Getters/Setters（Java需显式定义）
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getAnimationType() {
            return animationType;
        }

        public void setAnimationType(int animationType) {
            this.animationType = animationType;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public float getDimAmount() {
            return dimAmount;
        }

        public void setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
        }
    }
}

