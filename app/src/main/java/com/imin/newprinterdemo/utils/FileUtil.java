package com.imin.newprinterdemo.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * create by Mark on 2023/7/12 Time：17:06
 * tip:
 */
public class FileUtil {

    public static final String SELF_TEST_FILE_80 = "iminTest80.bin";
    public static final String SELF_TEST_FILE_58 = "iminTest58.bin";

    /**
     * 文件转字节流
     *
     * @param file
     * @return
     */
    public static byte[] fileToByte(File file) {
        if (file == null) {
            return null;
        }
        ByteArrayOutputStream outStream = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 1024];
            byte[] data = null;
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                return null;
            }
        }

    }

    /**
     * 获取Assets文件
     *
     * @param fileName
     * @return
     */
    public static byte[] getAssetsFile(Context context, String fileName) {
        InputStream in = null;
        ByteArrayOutputStream outStream = null;
        byte[] data = null;
        try {
            in = context.getResources().getAssets().open(fileName);
            outStream = new ByteArrayOutputStream();
            //创建byte数组
            byte[] buffer = new byte[1024 * 1024];
            //将文件中的数据读到byte数组中
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

}
