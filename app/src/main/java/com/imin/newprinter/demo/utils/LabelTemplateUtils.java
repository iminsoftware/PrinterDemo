package com.imin.newprinter.demo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.R;
import com.imin.printer.PrinterHelper;
import com.imin.printer.enums.Align;
import com.imin.printer.enums.ErrorLevel;
import com.imin.printer.enums.HumanReadable;
import com.imin.printer.enums.Shape;
import com.imin.printer.enums.Symbology;
import com.imin.printer.label.LabelAreaStyle;
import com.imin.printer.label.LabelBarCodeStyle;
import com.imin.printer.label.LabelBitmapStyle;
import com.imin.printer.label.LabelCanvasStyle;
import com.imin.printer.label.LabelQrCodeStyle;
import com.imin.printer.label.LabelTextStyle;

public class LabelTemplateUtils {

    static int posX = 48;
    static int labelLength = 56;//标签有效点
    private static final class LabelUtilsHolder {
        static final LabelTemplateUtils serialHelper = new LabelTemplateUtils();
    }

    public static LabelTemplateUtils getInstance() {
        posX = (labelLength - 50) * 8;
        return LabelUtilsHolder.serialHelper;
    }

    public static Bitmap printLabelSize40x30_CN1(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title1), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content1), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(100)
                .setAlign(Align.CENTER)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content2), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(0)
                .setPosY(130)
                .setAlign(Align.CENTER)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(60)
                .setReadable(HumanReadable.POS_TWO));
        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize40x30_CN2(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title2), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content21), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(100)
                .setAlign(Align.CENTER)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content22), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(140)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content23), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(50)
                .setPosY(150)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(50)
                .setReadable(HumanReadable.POS_TWO));
        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize40x30_CN3(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title3), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content31), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(100)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content32), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(150)
                .setTextSize(22)
                .setAlign(Align.CENTER));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content33), LabelTextStyle.getTextStyle()
                .setPosX(100)
                .setPosY(200)
                .setEnableBold(true)
                .setTextSize(26));

        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize40x30_CN4(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title4), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content41), LabelTextStyle.getTextStyle()
                .setPosX(20)
                .setPosY(100)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content42), LabelTextStyle.getTextStyle()
                .setPosX(20)
                .setPosY(140)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content43), LabelTextStyle.getTextStyle()
                .setPosX(20)
                .setPosY(180)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content44), LabelTextStyle.getTextStyle()
                .setPosX(20)
                .setPosY(220)
                .setTextSize(22));
        PrinterHelper.getInstance().labelAddQrCode(IminApplication.mContext.getString(R.string.content45), LabelQrCodeStyle.getQrCodeStyle()
                .setPosX(220)
                .setPosY(150)
                .setSize(3)
                .setErrorLevel(ErrorLevel.H));
        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize40x50_CN1(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(50 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title5), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content51), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(100)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content52), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(140)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content53), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(180)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content54), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(220)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content55), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(260)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content56), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(300)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content57), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(30)
                .setPosY(310)
                .setAlign(Align.CENTER)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(60)
                .setReadable(HumanReadable.POS_TWO));

        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize40x60_CN1(boolean isPrint){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(40 * 8)
                .setHeight(60 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 40) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title6), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content61), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(100)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content62), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(140)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content63), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(180)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content64), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(220)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content65), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(260)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content66), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(300)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content67), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(340)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content68), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(30)
                .setPosY(370)
                .setAlign(Align.CENTER)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(60)
                .setReadable(HumanReadable.POS_TWO));

        if (isPrint){
            PrinterHelper.getInstance().labelPrintCanvas(1,null);
        }

        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize50x30_CN1(){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(50 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 50) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title7), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content71), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(100)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content72), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(130)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content73), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(160)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content74), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(190)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content75), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(220)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddQrCode(IminApplication.mContext.getString(R.string.content76), LabelQrCodeStyle.getQrCodeStyle()
                .setPosX(290)
                .setPosY(145)
                .setSize(3)
                .setErrorLevel(ErrorLevel.H));

//        PrinterHelper.getInstance().labelPrintCanvas(1,null);
        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize50x30_CN2(){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(50 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 50) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title8), LabelTextStyle.getTextStyle()
                .setPosX(0)
                .setPosY(60)
                .setAlign(Align.CENTER)
                .setEnableBold(true)
                .setTextSize(30));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content81), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(100)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content82), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(130)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content83), LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(160)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content84), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(20)
                .setPosY(180)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(40)
                .setReadable(HumanReadable.HIDE));

//        PrinterHelper.getInstance().labelPrintCanvas(1,null);
        return PrinterHelper.getInstance().getLabelBitmap();
    }

    public static Bitmap printLabelSize50x30_CN3(){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(50 * 8)
                .setHeight(30 * 8)
                .setPosX(Utils.isPortrait()?48:((labelLength - 50) * 8)));
        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.title9), LabelTextStyle.getTextStyle()
                .setPosX(40)
                .setPosY(60)
                .setEnableBold(true)
                .setTextSize(24));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content91), LabelTextStyle.getTextStyle()
                .setPosX(250)
                .setPosY(55)
                .setEnableBold(true)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBarCode(IminApplication.mContext.getString(R.string.content92), LabelBarCodeStyle.getBarCodeStyle()
                .setPosX(40)
                .setPosY(80)
                .setSymbology(Symbology.CODABAR)
                .setDotWidth(2)
                .setBarHeight(60)
                .setReadable(HumanReadable.POS_TWO));

        PrinterHelper.getInstance().labelAddText(IminApplication.mContext.getString(R.string.content93), LabelTextStyle.getTextStyle()
                .setPosX(40)
                .setPosY(220)
                .setEnableBold(true)
                .setTextSize(24));

//        PrinterHelper.getInstance().labelPrintCanvas(1,null);
        return PrinterHelper.getInstance().getLabelBitmap();
    }
//    public static Bitmap printLabelSize50x30_CN21(){
//        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
//                .setWidth(50 * 8)
//                .setHeight(30 * 8)
//                .setPosX(48));
//        PrinterHelper.getInstance().labelAddText("标签打印测试", LabelTextStyle.getTextStyle()
//                .setPosX(20)
//                .setPosY(40)
//                .setTextSize(28));
//
//        PrinterHelper.getInstance().labelAddText("富士苹果", LabelTextStyle.getTextStyle()
//                .setPosX(240)
//                .setPosY(35)
//                .setEnableBold(true)
//                .setTextSize(25));
//
//        PrinterHelper.getInstance().labelAddText("Fuji Apple", LabelTextStyle.getTextStyle()
//                .setPosX(240)
//                .setPosY(60)
//                .setTextSize(25));
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(65)
//                .setEndX(50 * 8 - 1)
//                .setEndY(65)
//                .setThick(2));
//
//        PrinterHelper.getInstance().labelAddText("包装日期 2024.12.25", LabelTextStyle.getTextStyle()
//                .setPosX(20)
//                .setPosY(80)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddText("重量（kg）/个     1.70", LabelTextStyle.getTextStyle()
//                .setPosX(205)
//                .setPosY(80)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(88)
//                .setEndX(50 * 8 - 1)
//                .setEndY(88)
//                .setThick(2));
//        PrinterHelper.getInstance().labelAddText("单价（元/kg）     9.98", LabelTextStyle.getTextStyle()
//                .setPosX(205)
//                .setPosY(105)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddText("总价（元）", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(120)
//                .setTextSize(24));
//
//        PrinterHelper.getInstance().labelAddText("16.98", LabelTextStyle.getTextStyle()
//                .setPosX(40)
//                .setPosY(160)
//                .setEnableBold(true)
//                .setTextSize(32));
//
//        PrinterHelper.getInstance().labelAddText("溯源码：101", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(195)
//                .setTextSize(19));
//
//        PrinterHelper.getInstance().labelAddBarCode("{B123456", LabelBarCodeStyle.getBarCodeStyle()
//                .setPosX(160)
//                .setPosY(120)
//                .setSymbology(Symbology.CODE128)
//                .setDotWidth(2)
//                .setBarHeight(50)
//                .setReadable(HumanReadable.POS_TWO));
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(205)
//                .setEndX(50 * 8 - 1)
//                .setEndY(205)
//                .setThick(2));
//
//        PrinterHelper.getInstance().labelAddText("地址：XX市XX区XX大道测试超市", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(225)
//                .setTextSize(14));
//
////        PrinterHelper.getInstance().labelPrintCanvas(1,null);
//        return PrinterHelper.getInstance().getLabelBitmap();
//    }
//
//
//    public static Bitmap printLabelSize50x30EN21(){
//        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
//                .setWidth(50 * 8)
//                .setHeight(30 * 8)
//                .setPosX(48));
//        PrinterHelper.getInstance().labelAddText("Label Test Fuji Apple", LabelTextStyle.getTextStyle()
//                .setPosX(0)
//                .setPosY(40)
//                .setAlign(Align.CENTER)
//                .setTextSize(26));
//
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(60)
//                .setEndX(50 * 8 - 1)
//                .setEndY(60)
//                .setThick(2));
//
//        PrinterHelper.getInstance().labelAddText("PACKED 2024.12.25", LabelTextStyle.getTextStyle()
//                .setPosX(20)
//                .setPosY(78)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddText("WT/lb                1.70", LabelTextStyle.getTextStyle()
//                .setPosX(195)
//                .setPosY(78)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(88)
//                .setEndX(50 * 8 - 1)
//                .setEndY(88)
//                .setThick(2));
//        PrinterHelper.getInstance().labelAddText("PRICE PER lb    9.98", LabelTextStyle.getTextStyle()
//                .setPosX(195)
//                .setPosY(105)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddText("TOTAL PRICE", LabelTextStyle.getTextStyle()
//                .setPosX(20)
//                .setPosY(120)
//                .setTextSize(22));
//
//        PrinterHelper.getInstance().labelAddText("$16.98", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(160)
//                .setEnableBold(true)
//                .setTextSize(26));
//
//        PrinterHelper.getInstance().labelAddText("TRACE NO：101", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(195)
//                .setTextSize(15));
//
//        PrinterHelper.getInstance().labelAddBarCode("{B123456", LabelBarCodeStyle.getBarCodeStyle()
//                .setPosX(160)
//                .setPosY(120)
//                .setSymbology(Symbology.CODE128)
//                .setDotWidth(2)
//                .setBarHeight(50)
//                .setReadable(HumanReadable.POS_TWO));
//
//        PrinterHelper.getInstance().labelAddArea(LabelAreaStyle.getAreaStyle()
//                .setStyle(Shape.PATH)
//                .setPosX(1)
//                .setPosY(205)
//                .setEndX(50 * 8 - 1)
//                .setEndY(205)
//                .setThick(2));
//
//        PrinterHelper.getInstance().labelAddText("Addr:Label Test Supermarket", LabelTextStyle.getTextStyle()
//                .setPosX(30)
//                .setPosY(225)
//                .setTextSize(14));
//
////        PrinterHelper.getInstance().labelPrintCanvas(1,null);
//        return PrinterHelper.getInstance().getLabelBitmap();
//    }

    public static Bitmap printLabel50x301(){
        PrinterHelper.getInstance().labelInitCanvas(LabelCanvasStyle.getCanvasStyle()
                .setWidth(50 * 8)
                .setHeight(30 * 8)
                .setPosX(0));
        PrinterHelper.getInstance().labelAddText("Hi，iMin 先生", LabelTextStyle.getTextStyle()
                .setPosX(20)
                .setPosY(40)
                .setTextSize(28));

        PrinterHelper.getInstance().labelAddText("335", LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(85)
                .setEnableBold(true)
                .setTextSize(35));

        PrinterHelper.getInstance().labelAddText("[热]：", LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(113)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("第100/102 杯", LabelTextStyle.getTextStyle()
                .setPosX(120)
                .setPosY(85)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("陨石生椰拿铁", LabelTextStyle.getTextStyle()
                .setPosX(120)
                .setPosY(113)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("不另外加糖", LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(148)
                .setEnableBold(true)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("日期： ", LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(195)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("2024/12/30   17：20 ", LabelTextStyle.getTextStyle()
                .setPosX(120)
                .setPosY(195)
                .setEnableBold(true)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddText("建议尽快饮用，风味更佳 ", LabelTextStyle.getTextStyle()
                .setPosX(30)
                .setPosY(225)
                .setTextSize(22));

        PrinterHelper.getInstance().labelAddBitmap(BitmapFactory.decodeResource(IminApplication.mContext.getResources(), R.drawable.luckin_coffee), LabelBitmapStyle.getBitmapStyle()
                .setPosX(290)
                .setPosY(30)
                .setWidth(80)
                .setHeight(80));

        return PrinterHelper.getInstance().getLabelBitmap();
    }

}
