package com.feature.tui.dialog.sheet;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class QXUIBottomSheetListItemModel {
    Drawable image = null;
    int imageRes = 0;
    int imageSkinTintColorAttr = 0;
    int imageSkinSrcAttr = 0;
    int textSkinColorAttr = 0;
    CharSequence text;
    String tag = "";
    boolean hasRedPoint = false;
    boolean isDisabled = false;
    Typeface typeface;

    public QXUIBottomSheetListItemModel(CharSequence text, String tag) {
        this.text = text;
        this.tag = tag;
    }

    public QXUIBottomSheetListItemModel image(Drawable image) {
        this.image = image;
        return this;
    }

    public QXUIBottomSheetListItemModel image(int imageRes) {
        this.imageRes = imageRes;
        return this;
    }

    public QXUIBottomSheetListItemModel skinTextColorAttr(int attr) {
        this.textSkinColorAttr = attr;
        return this;
    }

    public QXUIBottomSheetListItemModel skinImageTintColorAttr(int attr) {
        this.imageSkinTintColorAttr = attr;
        return this;
    }

    public QXUIBottomSheetListItemModel skinImageSrcAttr(int attr) {
        this.imageSkinSrcAttr = attr;
        return this;
    }

    public QXUIBottomSheetListItemModel redPoint(boolean hasRedPoint) {
        this.hasRedPoint = hasRedPoint;
        return this;
    }

    public QXUIBottomSheetListItemModel disabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    public QXUIBottomSheetListItemModel typeface(Typeface typeface){
        this.typeface = typeface;
        return this;
    }
}
