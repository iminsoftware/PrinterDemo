package com.imin.newprinterdemo.blue;

import android.util.Log;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;


public class BluetoothPrintUtil {

    private OutputStreamWriter mWriter = null;
    private OutputStream mOutputStream = null;


    public BluetoothPrintUtil(OutputStream outputStream, String encoding) throws IOException {
        mWriter = new OutputStreamWriter(outputStream, encoding);
        mOutputStream = outputStream;
    }

    public void initPrinter() throws IOException {
        mWriter.write(0x1B);
        mWriter.write(0x40);
        mWriter.flush();
    }

    public void printRawBytes(byte[] bytes) {

        try {
            mOutputStream.write(bytes);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void print(byte[] bs) {
        Log.d("PrintDemoBlueActivity","binding.print1=== 》"+(bs != null));
        try {
            mOutputStream.write(bs);
        } catch (Exception e) {
            Log.d("PrintDemoBlueActivity","binding.print1=== 》 e.getMessage "+e.getMessage());
            e.printStackTrace();
        }
    }


    public void printText(String text) throws IOException {
        mWriter.write(text);
        mWriter.flush();
    }


    public static void showLogCompletion(String log,int showCount){
        if(log.length() >showCount){
            String show = log.substring(0, showCount);
            Log.i("imin_print_cmd_1111", "==print==log=====>  "+show+"");
            if((log.length() - showCount)>showCount){//剩下的文本还是大于规定长度
                String partLog = log.substring(showCount,log.length());
                showLogCompletion(partLog, showCount);
            }else{
                String surplusLog = log.substring(showCount, log.length());
                Log.i("imin_print_cmd_1111", "==print==log==eeee===>  "+surplusLog+"");
            }

        }else{
            Log.i("imin_print_cmd_1111", "==print==log=====>  "+log+"");
        }
    }
    //字节流转16进制字符串
    public static String getHexStringFromBytes(byte[] data) {
        if (data != null && data.length > 0) {
            String hexString = "0123456789ABCDEF";
            int size = data.length * 2;
            StringBuilder sb = new StringBuilder(size);

            for(int i = 0; i < data.length; ++i) {
                sb.append(hexString.charAt((data[i] & 240) >> 4));
                sb.append(hexString.charAt((data[i] & 15) >> 0));
            }

            return sb.toString();
        } else {
            return null;
        }
    }
}