package com.imin.newprinterdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GpUtils {
    private static Pattern pattern = Pattern.compile("([a-zA-Z0-9!@#$^&*\\(\\)~\\{\\}:\",\\.<>/]+)");

    private static int[] p0 = {0, 128};
    private static int[] p1 = {0, 64};
    private static int[] p2 = {0, 32};
    private static int[] p3 = {0, 16};
    private static int[] p4 = {0, 8};
    private static int[] p5 = {0, 4};
    private static int[] p6 = {0, 2};

    private static int[][] Floyd16x16 = {{0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170},
            {192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106},
            {48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154},
            {240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90},
            {12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166},
            {204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102},
            {60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150},
            {252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86},
            {3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169},
            {195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105},
            {51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153},
            {243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89},
            {15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165},
            {207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101},
            {63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149},
            {254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85}};

    private static int[][] Floyd8x8 = {{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26},
            {12, 44, 4, 36, 14, 46, 6, 38}, {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41},
            {51, 19, 59, 27, 49, 17, 57, 25}, {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};

    public static final int PAPER_58_WIDTH = 32;
    public static final int PAPER_80_WIDTH = 48;
    private static int sPaperWidth = PAPER_80_WIDTH;

    private static Integer[] theSet0 = {0x0621, 0x0622, 0x0623, 0x0624, 0x0625, 0x0626, 0x0627, 0x0628, 0x0629, 0x062A,
            0x062B, 0x062C, 0x062D, 0x062E, 0x062F, 0x0630, 0x0631, 0x0632, 0x0633, 0x0634, 0x0635, 0x0636, 0x0637,
            0x0638, 0x0639, 0x063A, 0x0641, 0x0642, 0x0643, 0x0644, 0x0645, 0x0646, 0x0647, 0x0648, 0x0649, 0x064A,
            0x4422, 0x4423, 0x4425, 0x4427};
    private static Integer[][] FormatTable =

			/* isolated, final, initial, medial */
            {new Integer[]{0xFE80, 0xFE80, 0xFE80, 0xFE80}, /* 0621 */
                    new Integer[]{0xFE81, 0xFE82, 0xFE81, 0xFE82}, /* 0622 */
                    new Integer[]{0xFE83, 0xFE84, 0xFE83, 0xFE84}, /* 0623 */
                    new Integer[]{0xFE85, 0xFE85, 0xFE85, 0xFE85}, /* 0624 */
                    new Integer[]{0xFE7D, 0xFE7D, 0xFE7D, 0xFE7D}, /* 0625 */
                    new Integer[]{0xFE8B, 0xFE8B, 0xFE8B, 0xFE8B}, /* 0626 */
                    new Integer[]{0xFE8D, 0xFE8E, 0xFE8D, 0xFE8E}, /* 0627 */
                    new Integer[]{0xFE8F, 0xFE8F, 0xFE91, 0xFE91}, /* 0628 */
                    new Integer[]{0xFE93, 0xFE93, 0xFE93, 0xFE93}, /* 0629 */
                    new Integer[]{0xFE95, 0xFE95, 0xFE97, 0xFE97}, /* 062A */
                    new Integer[]{0xFE99, 0xFE99, 0xFE9B, 0xFE9B}, /* 062B */
                    new Integer[]{0xFE9D, 0xFE9D, 0xFE9F, 0xFE9F}, /* 062C */
                    new Integer[]{0xFEA1, 0xFEA1, 0xFEA3, 0xFEA3}, /* 062D */
                    new Integer[]{0xFEA5, 0xFEA5, 0xFEA7, 0xFEA7}, /* 062E */
                    new Integer[]{0xFEA9, 0xFEA9, 0xFEA9, 0xFEA9}, /* 062F */
                    new Integer[]{0xFEAB, 0xFEAB, 0xFEAB, 0xFEAB}, /* 0630 */
                    new Integer[]{0xFEAD, 0xFEAD, 0xFEAD, 0xFEAD}, /* 0631 */
                    new Integer[]{0xFEAF, 0xFEAF, 0xFEAF, 0xFEAF}, /* 0632 */
                    new Integer[]{0xFEB1, 0xFEB1, 0xFEB3, 0xFEB3}, /* 0633 */
                    new Integer[]{0xFEB5, 0xFEB5, 0xFEB7, 0xFEB7}, /* 0634 */
                    new Integer[]{0xFEB9, 0xFEB9, 0xFEBB, 0xFEBB}, /* 0635 */
                    new Integer[]{0xFEBD, 0xFEBD, 0xFEBF, 0xFEBF}, /* 0636 */
                    new Integer[]{0xFEC1, 0xFEC1, 0xFEC1, 0xFEC1}, /* 0637 */
                    new Integer[]{0xFEC5, 0xFEC5, 0xFEC5, 0xFEC5}, /* 0638 */
                    new Integer[]{0xFEC9, 0xFECA, 0xFECB, 0xFECC}, /* 0639 */
                    new Integer[]{0xFECD, 0xFECE, 0xFECF, 0xFED0}, /* 063A */
                    new Integer[]{0xFED1, 0xFED1, 0xFED3, 0xFED3}, /* 0641 */
                    new Integer[]{0xFED5, 0xFED5, 0xFED7, 0xFED7}, /* 0642 */
                    new Integer[]{0xFED9, 0xFED9, 0xFEDB, 0xFEDB}, /* 0643 */
                    new Integer[]{0xFEDD, 0xFEDD, 0xFEDF, 0xFEDF}, /* 0644 */
                    new Integer[]{0xFEE1, 0xFEE1, 0xFEE3, 0xFEE3}, /* 0645 */
                    new Integer[]{0xFEE5, 0xFEE5, 0xFEE7, 0xFEE7}, /* 0646 */
                    new Integer[]{0xFEE9, 0xFEE9, 0xFEEB, 0xFEEB}, /* 0647 */
                    new Integer[]{0xFEED, 0xFEED, 0xFEED, 0xFEED}, /* 0648 */
                    new Integer[]{0xFEEF, 0xFEF0, 0xFEEF, 0xFEF0}, /* 0649 */
                    new Integer[]{0xFEF1, 0xFEF2, 0xFEF3, 0xFEF3}, /* 064A */
                    new Integer[]{0xFEF5, 0xFEF6, 0xFEF5,
                            0xFEF6}, /* 0644 + 0622 = 4422 */
                    new Integer[]{0xFEF7, 0xFEF8, 0xFEF7,
                            0xFEF8}, /* 0644 + 0623 = 4423 */
                    new Integer[]{0xFEF9, 0xFEFA, 0xFEF9,
                            0xFEFA}, /* 0644 + 0625 = 4425 */
                    new Integer[]{0xFEFB, 0xFEFC, 0xFEFB,
                            0xFEFC}, /* 0644 + 0627 = 4427 */
            };

    /* 集合1 ,检测前连 */
    static Integer[] theSet1 = {0x626, 0x628, 0x62a, 0x62b, 0x62c, 0x62d, 0x62e, 0x633, 0x634, 0x635, 0x636, 0x637,
            0x638, 0x639, 0x63a, 0x640, 0x641, 0x642, 0x643, 0x644, 0x645, 0x646, 0x647, 0x64a};
    /* 集合2 ，检测后连 */
    static Integer[] theSet2 = {0x622, 0x623, 0x624, 0x625, 0x626, 0x627, 0x628, 0x629, 0x62a, 0x62b, 0x62c, 0x62d,
            0x62e, 0x62f, 0x630, 0x631, 0x632, 0x633, 0x634, 0x635, 0x636, 0x637, 0x638, 0x639, 0x63a, 0x640, 0x641,
            0x642, 0x643, 0x644, 0x645, 0x646, 0x647, 0x648, 0x649, 0x64a};

    public static final int ALGORITHM_DITHER_16x16 = 16;
    public static final int ALGORITHM_DITHER_8x8 = 8;
    public static final int ALGORITHM_TEXTMODE = 2;
    public static final int ALGORITHM_GRAYTEXTMODE = 1;

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth / width);
        float scaleHeight = ((float) newHeight / height);
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    public static void saveMyBitmap(Bitmap mBitmap) {
        File f = new File(Environment.getExternalStorageDirectory().getPath(), "Btatotest.jpeg");
        try {
            f.createNewFile();
        } catch (IOException localIOException) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException localFileNotFoundException) {
        } catch (IOException localIOException1) {
        }
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    public static byte[] pixToEscRastBitImageCmd(byte[] src, int nWidth, int nMode) {
        int nHeight = src.length / nWidth;
        byte[] data = new byte[8 + src.length / 8];
        data[0] = 29;
        data[1] = 118;
        data[2] = 48;
        data[3] = (byte) (nMode & 0x1);
        data[4] = (byte) (nWidth / 8 % 256);
        data[5] = (byte) (nWidth / 8 / 256);
        data[6] = (byte) (nHeight % 256);
        data[7] = (byte) (nHeight / 256);
        for (int i = 8, k = 0; i < data.length; i++) {
            data[i] = (byte) (p0[src[k]] + p1[src[(k + 1)]] + p2[src[(k + 2)]] + p3[src[(k + 3)]] + p4[src[(k + 4)]]
                    + p5[src[(k + 5)]] + p6[src[(k + 6)]] + src[(k + 7)]);
            k += 8;
        }
        return data;
    }

    public static byte[] pixToEscRastBitImageCmd(byte[] src) {
        byte[] data = new byte[src.length / 8];
        for (int i = 0, k = 0; i < data.length; i++) {
            data[i] = (byte) (p0[src[k]] + p1[src[(k + 1)]] + p2[src[(k + 2)]] + p3[src[(k + 3)]] + p4[src[(k + 4)]]
                    + p5[src[(k + 5)]] + p6[src[(k + 6)]] + src[(k + 7)]);
            k += 8;
        }
        return data;
    }
    public static byte[] bitmapToBWPix2(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] data = new byte[width * height];
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);//取得BITMAP的所有像素点

        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < width; ++j) {
                int color =pixels[i*width+j];
                int gray = (int) ((0.299d * ((color & 0xFF0000) >> 16)) + (0.587d * ((color & 0xFF00) >> 8)) + (0.114d * (color & 0xFF)));
                byte y = (byte)1;
                byte n = (byte)0;
                data[i * width + j] = gray < 128 ? y : n;
            }
        }
        return data;
    }
    public static byte[] pixToEscNvBitImageCmd(byte[] src, int width, int height) {
        byte[] data = new byte[src.length / 8 + 4];
        data[0] = (byte) ((width / 8) % 256);
        data[1] = (byte) ((width / 8) / 256);
        data[2] = (byte) ((height / 8) % 256);
        data[3] = (byte) ((height / 8) / 256);
        int k = 0;
        for (int i = 0; i < width; i++) {
            k = 0;
            for (int j = 0; j < height / 8; j++) {
                data[4 + j + i * height / 8] = (byte) (p0[src[i + k]] + p1[src[(i + k + 1 * width)]]
                        + p2[src[(i + k + 2 * width)]] + p3[src[(i + k + 3 * width)]] + p4[src[(i + k + 4 * width)]]
                        + p5[src[(i + k + 5 * width)]] + p6[src[(i + k + 6 * width)]] + src[(i + k + 7 * width)]);
                k += 8 * width;
            }
        }
        return data;
    }

    public static byte[] pixToLabelCmd(byte[] src) {
        byte[] data = new byte[src.length / 8];
        byte temp;
        for (int k = 0, j = 0; k < data.length; ++k) {
            temp = (byte) (p0[src[j]] + p1[src[(j + 1)]] + p2[src[(j + 2)]] + p3[src[(j + 3)]] + p4[src[(j + 4)]]
                    + p5[src[(j + 5)]] + p6[src[(j + 6)]] + src[(j + 7)]);
            data[k] = (byte) ~temp;
            j += 8;
        }
        return data;
    }

    public static byte[] pixToTscCmd(int x, int y, int mode, byte[] src, int nWidth) {
        int height = src.length / nWidth;
        int width = nWidth / 8;
        String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode + ",";
        byte[] bitmap = null;
        try {
            bitmap = str.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] arrayOfByte = new byte[src.length / 8];
        byte temp;
        for (int k = 0, j = 0; k < arrayOfByte.length; ++k) {
            temp = (byte) (p0[src[j]] + p1[src[(j + 1)]] + p2[src[(j + 2)]] + p3[src[(j + 3)]] + p4[src[(j + 4)]]
                    + p5[src[(j + 5)]] + p6[src[(j + 6)]] + src[(j + 7)]);
            arrayOfByte[k] = (byte) ~temp;
            j += 8;
        }
        byte[] data = new byte[bitmap.length + arrayOfByte.length];
        System.arraycopy(bitmap, 0, data, 0, bitmap.length);
        System.arraycopy(arrayOfByte, 0, data, bitmap.length, arrayOfByte.length);
        return data;
    }

    private static void format_K_dither16x16(int[] orgpixels, int xsize, int ysize, byte[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                if ((orgpixels[k] & 0xFF) > Floyd16x16[(x & 0xF)][(y & 0xF)])
                    despixels[k] = 0;
                else {
                    despixels[k] = 1;
                }
                k++;
            }
        }
    }

    public static byte[] bitmapToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];
        Bitmap grayBitmap = toGrayscale(mBitmap);
        grayBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        format_K_dither16x16(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), data);

        return data;
    }

    private static void format_K_dither16x16_int(int[] orgpixels, int xsize, int ysize, int[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                if ((orgpixels[k] & 0xFF) > Floyd16x16[(x & 0xF)][(y & 0xF)])
                    despixels[k] = -1;
                else {
                    despixels[k] = -16777216;
                }
                k++;
            }
        }
    }

    private static void format_K_dither8x8_int(int[] orgpixels, int xsize, int ysize, int[] despixels) {
        int k = 0;
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                if ((orgpixels[k] & 0xFF) >> 2 > Floyd8x8[(x & 0x7)][(y & 0x7)])
                    despixels[k] = -1;
                else {
                    despixels[k] = -16777216;
                }
                k++;
            }
        }
    }

    public static int[] bitmapToBWPix_int(Bitmap mBitmap, int algorithm) {
        int[] pixels = new int[0];
        switch (algorithm) {
            case 8:
                Bitmap grayBitmap = toGrayscale(mBitmap);
                pixels = new int[grayBitmap.getWidth() * grayBitmap.getHeight()];
                grayBitmap.getPixels(pixels, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
                format_K_dither8x8_int(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), pixels);
                break;
            case 2:
                break;
            case 16:
            default:
                grayBitmap = toGrayscale(mBitmap);
                pixels = new int[grayBitmap.getWidth() * grayBitmap.getHeight()];
                grayBitmap.getPixels(pixels, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
                format_K_dither16x16_int(pixels, grayBitmap.getWidth(), grayBitmap.getHeight(), pixels);
        }

        return pixels;
    }

    public static Bitmap toBinaryImage(Bitmap mBitmap, int nWidth, int algorithm) {
        int width = (nWidth + 7) / 8 * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        Bitmap rszBitmap = resizeImage(mBitmap, width, height);

        int[] pixels = bitmapToBWPix_int(rszBitmap, algorithm);
        rszBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return rszBitmap;
    }

    public final static int[][] COLOR_PALETTE = new int[][]{{0, 0, 0}, {255, 255, 255}};

    private static int getCloseColor(int tr, int tg, int tb) {
        int minDistanceSquared = 255 * 255 + 255 * 255 + 255 * 255 + 1;
        int bestIndex = 0;
        for (int i = 0; i < COLOR_PALETTE.length; i++) {
            int rdiff = tr - COLOR_PALETTE[i][0];
            int gdiff = tg - COLOR_PALETTE[i][1];
            int bdiff = tb - COLOR_PALETTE[i][2];
            int distanceSquared = rdiff * rdiff + gdiff * gdiff + bdiff * bdiff;
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static void setPixel(int[] input, int width, int height, int col, int row, int[] p) {
        if (col < 0 || col >= width)
            col = 0;
        if (row < 0 || row >= height)
            row = 0;
        int index = row * width + col;
        input[index] = (0xff << 24) | (clamp(p[0]) << 16) | (clamp(p[1]) << 8) | clamp(p[2]);
    }

    private static int[] getPixel(int[] input, int width, int height, int col, int row, float error, int[] ergb) {
        if (col < 0 || col >= width)
            col = 0;
        if (row < 0 || row >= height)
            row = 0;
        int index = row * width + col;
        int tr = (input[index] >> 16) & 0xff;
        int tg = (input[index] >> 8) & 0xff;
        int tb = input[index] & 0xff;
        tr = (int) (tr + error * ergb[0]);
        tg = (int) (tg + error * ergb[1]);
        tb = (int) (tb + error * ergb[2]);
        return new int[]{tr, tg, tb};
    }

    public static int clamp(int value) {
        return value > 255 ? 255 : (value < 0 ? 0 : value);
    }

    public static Bitmap filter(Bitmap nbm, int width, int height) {
        int[] inPixels = new int[width * height];
        nbm.getPixels(inPixels, 0, width, 0, 0, width, height);
        int[] outPixels = new int[inPixels.length];
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                int r1 = (inPixels[index] >> 16) & 0xff;
                int g1 = (inPixels[index] >> 8) & 0xff;
                int b1 = inPixels[index] & 0xff;
                int cIndex = getCloseColor(r1, g1, b1);
                outPixels[index] = (255 << 24) | (
                        COLOR_PALETTE[cIndex][0] << 16) |
                        (COLOR_PALETTE[cIndex][1] << 8)
                        | COLOR_PALETTE[cIndex][2];
                // 获取错误
                int[] ergb = new int[3];
                ergb[0] = r1 - COLOR_PALETTE[cIndex][0];
                ergb[1] = g1 - COLOR_PALETTE[cIndex][1];
                ergb[2] = b1 - COLOR_PALETTE[cIndex][2];

                // 错误扩散功能
                if (method == FLOYD_STEINBERG_DITHER) {
                    float e1 = 7f / 16f;
                    float e2 = 5f / 16f;
                    float e3 = 3f / 16f;
                    float e4 = 1f / 16f;
                    int[] rgb1 = getPixel(inPixels, width, height, col + 1, row, e1, ergb);
                    int[] rgb2 = getPixel(inPixels, width, height, col, row + 1, e2, ergb);
                    int[] rgb3 = getPixel(inPixels, width, height, col - 1, row + 1, e3, ergb);
                    int[] rgb4 = getPixel(inPixels, width, height, col + 1, row + 1, e4, ergb);
                    setPixel(inPixels, width, height, col + 1, row, rgb1);
                    setPixel(inPixels, width, height, col, row + 1, rgb2);
                    setPixel(inPixels, width, height, col - 1, row + 1, rgb3);
                    setPixel(inPixels, width, height, col + 1, row + 1, rgb4);
                } else if (method == ATKINSON_DITHER) {
                    float e1 = 0.125f;
                    int[] rgb1 = getPixel(inPixels, width, height, col + 1, row, e1, ergb);
                    int[] rgb2 = getPixel(inPixels, width, height, col + 2, row, e1, ergb);
                    int[] rgb3 = getPixel(inPixels, width, height, col - 1, row + 1, e1, ergb);
                    int[] rgb4 = getPixel(inPixels, width, height, col, row + 1, e1, ergb);
                    int[] rgb5 = getPixel(inPixels, width, height, col + 1, row + 1, e1, ergb);
                    int[] rgb6 = getPixel(inPixels, width, height, col, row + 2, e1, ergb);
                    setPixel(inPixels, width, height, col + 1, row, rgb1);
                    setPixel(inPixels, width, height, col + 2, row, rgb2);
                    setPixel(inPixels, width, height, col - 1, row + 1, rgb3);
                    setPixel(inPixels, width, height, col, row + 1, rgb4);
                    setPixel(inPixels, width, height, col + 1, row + 1, rgb5);
                    setPixel(inPixels, width, height, col, row + 2, rgb6);
                } else {
                    throw new IllegalArgumentException("Not Supported Dither Mothed!!");
                }

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(outPixels, 0, width, width, height, Bitmap.Config.RGB_565);

        return bitmap;
    }

    public static byte[] printEscDraw(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] bitbuf = new byte[width / 8];

        byte[] imgbuf = new byte[width / 8 * height + 8];
        imgbuf[0] = 0x1d;
        imgbuf[1] = 0x76;// 十六进制0x76
        imgbuf[2] = 0x30;// 30
        imgbuf[3] = 0;// 位图模式 0,1,2,3
        // 表示水平方向位图字节数（xL+xH × 256）
        imgbuf[4] = (byte) (width / 8);
        imgbuf[5] = 0;
        // 表示垂直方向位图点数（ yL+ yH × 256）
        imgbuf[6] = (byte) (height % 256);//
        imgbuf[7] = (byte) (height / 256);

        int s = 7;
        for (int i = 0; i < height; i++) {// 循环位图的高度
            for (int k = 0; k < width / 8; k++) {// 循环位图的宽度
                int c0 = bitmap.getPixel(k * 8, i);// 返回指定坐标的颜色
                int p0;
                if (c0 == -1 || c0 == 0) {// 判断颜色是不是白色、全透明
                    p0 = 0;// 0,不打印该点
                } else {
                    p0 = 1;// 1,打印该点
                }
                int c1 = bitmap.getPixel(k * 8 + 1, i);
                int p1;
                if (c1 == -1)
                    p1 = 0;
                else {
                    p1 = 1;
                }
                int c2 = bitmap.getPixel(k * 8 + 2, i);
                int p2;
                if (c2 == -1)
                    p2 = 0;
                else {
                    p2 = 1;
                }
                int c3 = bitmap.getPixel(k * 8 + 3, i);
                int p3;
                if (c3 == -1)
                    p3 = 0;
                else {
                    p3 = 1;
                }
                int c4 = bitmap.getPixel(k * 8 + 4, i);
                int p4;
                if (c4 == -1)
                    p4 = 0;
                else {
                    p4 = 1;
                }
                int c5 = bitmap.getPixel(k * 8 + 5, i);
                int p5;
                if (c5 == -1)
                    p5 = 0;
                else {
                    p5 = 1;
                }
                int c6 = bitmap.getPixel(k * 8 + 6, i);
                int p6;
                if (c6 == -1)
                    p6 = 0;
                else {
                    p6 = 1;
                }
                int c7 = bitmap.getPixel(k * 8 + 7, i);
                int p7;
                if (c7 == -1)
                    p7 = 0;
                else {
                    p7 = 1;
                }
                int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                bitbuf[k] = (byte) value;
            }

            for (int t = 0; t < width / 8; t++) {
                s++;
                imgbuf[s] = bitbuf[t];
            }

        }
        return imgbuf;
    }
    public static byte[] printZlibTscDraw(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] bitbuf = new byte[width / 8];
        byte[] imgbuf = new byte[width / 8 * height];
        int s = 0;

        for(int i = 0; i < height; ++i) {
            int t;
            for(t = 0; t < width / 8; ++t) {
                int c0 = bitmap.getPixel(t * 8, i);
                byte p0;
                if(c0 == -1) {
                    p0 = 0;
                } else {
                    p0 = 1;
                }

                int c1 = bitmap.getPixel(t * 8 + 1, i);
                byte p1;
                if(c1 == -1) {
                    p1 = 0;
                } else {
                    p1 = 1;
                }

                int c2 = bitmap.getPixel(t * 8 + 2, i);
                byte p2;
                if(c2 == -1) {
                    p2 = 0;
                } else {
                    p2 = 1;
                }

                int c3 = bitmap.getPixel(t * 8 + 3, i);
                byte p3;
                if(c3 == -1) {
                    p3 = 0;
                } else {
                    p3 = 1;
                }

                int c4 = bitmap.getPixel(t * 8 + 4, i);
                byte p4;
                if(c4 == -1) {
                    p4 = 0;
                } else {
                    p4 = 1;
                }

                int c5 = bitmap.getPixel(t * 8 + 5, i);
                byte p5;
                if(c5 == -1) {
                    p5 = 0;
                } else {
                    p5 = 1;
                }

                int c6 = bitmap.getPixel(t * 8 + 6, i);
                byte p6;
                if(c6 == -1) {
                    p6 = 0;
                } else {
                    p6 = 1;
                }

                int c7 = bitmap.getPixel(t * 8 + 7, i);
                byte p7;
                if(c7 == -1) {
                    p7 = 0;
                } else {
                    p7 = 1;
                }

                int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7;
                bitbuf[t] = (byte)value;
            }

            for(t = 0; t < width / 8; ++t) {
                imgbuf[s] = bitbuf[t];
                ++s;
            }
        }

        return imgbuf;
    }


    public static String splitArabic(String input) {
        StringBuilder sb = new StringBuilder(256);
        String[] arabics = input.split("\\n");
        // 阿拉伯文大于纸宽，去做分割并加上换行符，返回给递归调用处
        if (arabics.length == 1 && arabics[0].length() > sPaperWidth) {
            int insertWrapNumber = arabics[0].length() / sPaperWidth;
            for (int i = 1, j = 0; i <= insertWrapNumber; i++) {
                sb.append(arabics[0].substring(j, sPaperWidth * i));
                j += sPaperWidth;
            }
            if (sb.length() >= 0)
                sb.append('\n');
            // 多出来的阿拉伯文，在后面加上空格。
            int lastArabic = arabics[0].length() % sPaperWidth;
            sb.append(arabics[0].substring(arabics[0].length() - lastArabic, arabics[0].length()));
            return splitArabic(sb.toString());
        }

        for (int i = 0; i < arabics.length; i++) {
            int childStringLength = arabics[i].length();
            if (childStringLength > sPaperWidth) {
                sb.append(splitArabic(arabics[i]));
            } else {
                sb.append(addSpaceAfterArabicString(arabics[i], sPaperWidth - childStringLength));
            }
        }
        return sb.toString();
    }

    public    static String addSpaceAfterArabicString(String arabic, int number) {
        StringBuilder sb = new StringBuilder(65);
        sb.append(arabic);
        for (int i = 0; i < number; i++) {
            sb.append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * 反转字母和数字
     *
     * @param input
     * @return
     */
    public static String reverseLetterAndNumber(String input) {
        StringBuilder sb = new StringBuilder(input);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String matcherString = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            sb.replace(matcherStart, matcherEnd, new StringBuilder(matcherString).reverse().toString());
            // System.out.println(matcherString);
            // System.out.println(matcherStart);
            // System.out.println(matcherEnd);
        }
        return sb.toString();
    }

    public static byte[] string2Cp864(String arabicString) {
        Integer[] originUnicode = new Integer[arabicString.length()];
        Integer[] outputUnicode = new Integer[arabicString.length()];

		/* 将字符串转换为原始Unicode */
        Integer[] outputChars = new Integer[originUnicode.length];

        copy(arabicString.toCharArray(), originUnicode, arabicString.length());

        List<Integer> list = new ArrayList<Integer>(Arrays.asList(originUnicode)); /* 初始化list */
        list = Hyphen(list); /* 处理连字符 */
        list = Deformation(list); /* 处理形态变化 */
        Collections.reverse(list); /* 反转顺序 */
        list.toArray(outputUnicode); /* 输出处理后的Unicode */

        char[] chs = integer2Character(outputUnicode);
        // for (int i = 0; i < chs.length; i++) {
        // if (chs[i] == 0x25) {
        // chs[i] = 1642;
        // }
        // }
        // copy(chs, outputChars, outputUnicode.length);
        byte[] cp864bytes = new byte[0];
        try {
            cp864bytes = new String(chs).getBytes("cp864");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return (cp864bytes);

    }

    static char[] integer2Character(Integer[] integers) {
        char[] chs = new char[integers.length];
        for (int i = 0; i < integers.length; i++) {
            if (integers[i] != null)
                chs[i] = (char) ((int) integers[i]);
            else
                chs[i] = ' ';
        }
        return chs;
    }

    static void copy(char[] array, Integer[] originUnicode, int length) {

        for (int i = 0; i < length; i++) {
            originUnicode[i] = (int) array[i];
        }
    }

    static List<Integer> Hyphen(List<Integer> list) {
        int i;
        rerun:

        for (i = 0; i < list.size(); i++) /* list长度是动态变化，此处易出bug */ {
            if (list.get(i) == 0x0644)

			/*
             * 字符是0x0644，根据下一个字符0x0622,0x0623,0x0625,0x0627进行合并
			 * 暂时转换为0x4422,0x4423,0x4425,0x4427等待变形处理
			 */ {
                switch (list.get(i + 1)) {
                    case 0x0622:
                        list.set(i, 0x4422);
                        list.remove(i + 1);
                        break;
                    case 0x0623:
                        list.set(i, 0x4423);
                        list.remove(i + 1);
                        break;
                    case 0x0625:
                        list.set(i, 0x4425);
                        list.remove(i + 1);
                        break;
                    case 0x0627:
                        list.set(i, 0x4427);
                        list.remove(i + 1);
                        break;
                }
            }
        }
        return (list);
    }

    static List<Integer> Deformation(List<Integer> inputlist) {
        int flag = 0;
		/* 字符形态标识符 */
		/*
		 * 0:isolated（默认） 1:final 2:initial 3:medial
		 */
        List<Integer> outputlist = new ArrayList<Integer>();

        int i;
        Integer[] a;
        boolean inSet1, inSet2;
        Map<Integer, Integer[]> formHashTable = new HashMap<Integer, Integer[]>(40); /* 创建哈希表 */

        for (i = 0; i < 40; i++) /* 初始化哈希表 */ {
            formHashTable.put(theSet0[i], FormatTable[i]);
        }
        ;
        for (i = 0; i < inputlist.size(); i++) {
            if (compare(inputlist.get(i), 0)) /* 字符属于Set0 */ {
                /**
                 *
                 * 判断前连 后连 中连与单独
                 */
                if (i == 0) {
                    inSet1 = false;
                    inSet2 = compare(inputlist.get(i + 1), 2);
                    flag = Flag(inSet1, inSet2);
                } else if (i == inputlist.size() - 1) {
                    inSet1 = compare(inputlist.get(i - 1), 1);
                    inSet2 = false;
                    flag = Flag(inSet1, inSet2);
                } else {
                    inSet1 = compare(inputlist.get(i - 1), 1);
                    inSet2 = compare(inputlist.get(i + 1), 2);
                    flag = Flag(inSet1, inSet2);
                }

                a = formHashTable
                        .get(inputlist.get(i)); /* 提取哈希表中的值，该值为存储有4种形态的数组 */
                outputlist.add(a[flag]); /* 根据flag的值，重新赋值list中的当前字符 */
            } else {
                outputlist.add(inputlist.get(i));
            }
        }

        return (outputlist);
    }

    static boolean compare(Integer input, int i) {
		/* i为1或2 */
        List<Integer[]> list = new ArrayList<Integer[]>(3);
        list.add(theSet0);
        list.add(theSet1);
        list.add(theSet2);
        return findInArray(list.get(i), input);
    }

    static boolean findInArray(Integer[] integer, int input) {
        for (int j = 0; j < integer.length; j++) {
            if (integer[j] == input) {
                return true;
            }
        }
        return false;
    }

    static int Flag(boolean set1, boolean set2) {
		/*
		 * 0:isolated（默认） 1:final 2:initial 3:medial
		 */
        if (set1 && set2)
            return (3);
        else if (!set1 && set2)
            return (2);
        else if (set1 && !set2)
            return (1);
        else
            return (0);
    }

    public static void setPaperWidth(int paperWidth) {
        sPaperWidth = paperWidth;
    }

    public static byte[] ByteTo_byte(Vector<Byte> vector) {
        int len = vector.size();
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = vector.get(i);
        }
        return data;
    }
    public static byte[] ZlibpixToLabelCmd(byte[] src) {
        byte[] data = new byte[src.length / 8];
        int k = 0;

        for(int j = 0; k < data.length; ++k) {
            byte temp = (byte)(p0[src[j]] + p1[src[j + 1]] + p2[src[j + 2]] + p3[src[j + 3]] + p4[src[j + 4]] + p5[src[j + 5]] + p6[src[j + 6]] + src[j + 7]);
            data[k] = temp;
            j += 8;
        }

        return data;
    }
    private static int method = 1;
    public final static int FLOYD_STEINBERG_DITHER = 1;
    public final static int ATKINSON_DITHER = 2;

    public int getMethod() {
        return method;
    }

    public static void setMethod(int method) {
        GpUtils.method = method;
    }

}
