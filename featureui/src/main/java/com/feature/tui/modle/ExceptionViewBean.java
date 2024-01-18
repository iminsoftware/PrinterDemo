package com.feature.tui.modle;

import com.feature.tui.dialog.Functions;

/**
 * 异常视图参数
 */
public class ExceptionViewBean {
    private int imgResOrType = 0;

    private String title;

    private String content;

    private String btnText;

    private Functions.Fun0 callback;

    private Functions.Fun0 callbackScreen;

    public int getImgResOrType() {
        return this.imgResOrType;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getBtnText() {
        return this.btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }


    public Functions.Fun0 getCallback() {
        return this.callback;
    }

    public void setCallback(Functions.Fun0 callback) {
        this.callback = callback;
    }


    public Functions.Fun0 getCallbackScreen() {
        return this.callbackScreen;
    }

    public void setCallbackScreen(Functions.Fun0 callbackScreen) {
        this.callbackScreen = callbackScreen;
    }

    public ExceptionViewBean(int imgResOrType, String title, String content, String btnText, Functions.Fun0 callback, Functions.Fun0 callbackScreen) {
        this.imgResOrType = imgResOrType;
        this.title = title;
        this.content = content;
        this.btnText = btnText;
        this.callback = callback;
        this.callbackScreen = callbackScreen;
    }

    public ExceptionViewBean(int imgResOrType, String title, String content, String btnText, Functions.Fun0 callback) {
        this(imgResOrType, title, content, btnText, callback, null);
    }

    public ExceptionViewBean(int imgResOrType, String title, String content, String btnText) {
        this(imgResOrType, title, content, btnText, null, null);
    }

    public ExceptionViewBean(int imgResOrType, String title, String content) {
        this(imgResOrType, title, content, null, null, null);
    }

    public ExceptionViewBean(int imgResOrType, String title) {
        this(imgResOrType, title, null, null, null, null);
    }

    public ExceptionViewBean(int imgResOrType) {
        this(imgResOrType, null, null, null, null, null);
    }
}
