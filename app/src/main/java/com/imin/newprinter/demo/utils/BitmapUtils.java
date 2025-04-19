package com.imin.newprinter.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2020/9/16
 */
public class BitmapUtils {

    public static final int PRINTER_TYPE_58 = 58;
    public static final int WIDTH_58_PIXEL = 384;
    public static final int WIDTH_80_PIXEL = 576;

    /**
     * 缩放图片
     *
     * @param bitmap    源图片
     * @param dstWidth  目标图宽度
     * @param dstHeight 目标图高度
     * @return 缩放后的图片
     */
    public static Bitmap resize(Bitmap bitmap, int dstWidth, int dstHeight) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() == dstWidth && bitmap.getHeight() == dstHeight) {
            return bitmap;
        }
        if (dstWidth < 1 || dstHeight < 1) {
            throw new IllegalArgumentException("Bitmap output width and height must greater than 1");
        }
        float scaleX = ((float) dstWidth) / bitmap.getWidth();
        float scaleY = ((float) dstHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
