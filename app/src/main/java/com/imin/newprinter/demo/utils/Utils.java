package com.imin.newprinter.demo.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.imin.newprinter.demo.IminApplication;
import com.imin.newprinter.demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static String TAG = "Utils";

    public static final int PRINTER_NORMAL = 0;
    public static final int PRINTER_UNCAP = 3;
    public static final int PRINTER_LOWER_POWER = 4;
    public static final int PRINTER_OVER_HEAT = 5;
    public static final int PRINTER_CUTTING_ERROR = 6;
    public static final int PRINTER_PAPER_OUT = 7;

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.v("onClick", "isFastDoubleClick短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }

    //二维码块的大小
    public static List getQRSizeList() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        return list;
    }

    //二维码块的大小
    public static List getDoubleQRSizeList() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        return list;
    }

    public static List getQRLevList() {
        List<String> list = new ArrayList<>();
        list.add("7%");
        list.add("15%");
        list.add("25%");
        list.add("30%");
        return list;
    }

    public static List<String> getAlignmentList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.alignment);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getCutList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.cut);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getTextTypeList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.text_font);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getBarcodeTextList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.barcode_position);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getBarSymbologyList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.barcode_type);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getBarSymbologyListTip() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.barcode_type_tips);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getBarContentList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.barcode_content);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getRotationList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.rotation);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getDiretionList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.direction);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getUnderLineList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.under_line);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getTextModelList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.text_model);
        list = Arrays.asList(strings);
        return list;
    }

    public static List customizePicList() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.customize_pic);
        list = Arrays.asList(strings);
        return list;
    }

    //获取浓度列表
    public static List getDensity() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_density);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getDensityValue() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_density_value);
        list = Arrays.asList(strings);
        return list;
    }

    public static List getPrintWidth() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_width);
        list = Arrays.asList(strings);
        return list;
    }

    @NonNull
    private static List<String> getDataList(String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings == null || strings.length == 0) {
            list.add("0");
            Log.e(TAG, "getDataList: ");
            return list;
        }

        return list;
    }

    public static List<String> getPrintSpeedValue() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_speed_value);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getCodePage() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.codepage);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getCountryCode() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.country_code);
        list = Arrays.asList(strings);
        return list;
    }

    public static List<String> getPrintMode() {
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_mode);
        return getDataList(strings);
    }

    public static List<String> getPrintType() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.print_api);
        list = Arrays.asList(strings);
        return list;
    }

    /**
     *  > Android 10
     *
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static List getMoreSelect() {
        List<String> list = new ArrayList<>();
        String[] strings = IminApplication.mContext.getResources().getStringArray(R.array.more_button);
        list = Arrays.asList(strings);
        return list;
    }

    /**
     * get path
     *
     * @param context
     * @param uri
     * @return String
     */
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @TargetApi(19)
    public static String getPath1(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId2 = DocumentsContract.getDocumentId(uri);
                String[] split2 = docId2.split(":");
                String type2 = split2[0];
                Uri contentUri2 = null;
                if ("image".equals(type2)) {
                    contentUri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                String[] selectionArgs = {split2[1]};
                return getDataColumn(context, contentUri2, "_id=?", selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0 || s.equals("")) {
            return true;
        }
        return false;
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @TargetApi(19)
    public static String getPathPrintBin(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId2 = DocumentsContract.getDocumentId(uri);
                String[] split2 = docId2.split(":");
                String type2 = split2[0];
                Uri contentUri2 = null;
                if ("image".equals(type2)) {
                    contentUri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                String[] selectionArgs = {split2[1]};
                return getDataColumn(context, contentUri2, "_id=?", selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {"_data"};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow("_data");
                return cursor.getString(column_index);
            } else if (cursor == null) {
                cursor.close();
                return null;
            } else {
                return null;
            }
        } finally {
            if (cursor == null) {
                cursor.close();
            }
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getStoragePath(Context mContext, boolean is_removale) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList", new Class[0]);
            Method getPath = storageVolumeClazz.getMethod("getPath", new Class[0]);
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable", new Class[0]);
            Object result = getVolumeList.invoke(mStorageManager, new Object[0]);
            int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement, new Object[0]);
                boolean removable = ((Boolean) isRemovable.invoke(storageVolumeElement, new Object[0])).booleanValue();
                if (is_removale == removable) {
                    return path;
                }
            }
            return null;
        } catch (Exception e) {
            Log.e("onActivityResult", "" + e.getMessage());
            return null;
        }
    }

    public static String getProviderUriPath(Context context, Uri uri) {
        String filePath = "";
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs != null) {
            String fileProviderClassName = FileProvider.class.getName();
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (uri.getAuthority().equals(provider.authority)) {
                            if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                Class<FileProvider> fileProviderClass = FileProvider.class;
                                try {
                                    Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                    getPathStrategy.setAccessible(true);
                                    Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                    if (invoke != null) {
                                        String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                        Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                        Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                        getFileForUri.setAccessible(true);
                                        Object invoke1 = getFileForUri.invoke(invoke, uri);
                                        if (invoke1 instanceof File) {
                                            filePath = ((File) invoke1).getAbsolutePath();

                                        }
                                    }

                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                break;
                            }
                            break;
                        }
                    }

                }

            }

        }
        return filePath;
    }


    public static String getRealPathFromUri(Context context, Uri uri) {
        String filePath = "";
        String scheme = uri.getScheme();
        if (scheme == null)
            filePath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            if (TextUtils.isEmpty(filePath)) {
                filePath = getFilePathForNonMediaUri(context, uri);
            }
        }
        return filePath;
    }

    //非媒体文件中查找
    private static String getFilePathForNonMediaUri(Context context, Uri uri) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow("_data");
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    public static boolean isCN() {
        String language = Locale.getDefault().getLanguage();

        if (language == null || language.length() == 0 || language.equals("null")) {
            return false;
        }

        if (language.equals("zh")) {
            return true;
        }else {
            return false;
        }
    }

    public static String getPrinterStatusTip(Context context, int status) {
        int resStatus;
        if (status == PRINTER_NORMAL) {
            resStatus = R.string.normal;
        } else if (status == PRINTER_UNCAP) {
            resStatus = R.string.printer_uncap;

        } else if (status == PRINTER_LOWER_POWER) {
            resStatus = R.string.printer_lower_power;

        } else if (status == PRINTER_OVER_HEAT) {
            resStatus = R.string.printer_over_heat;

        } else if (status == PRINTER_CUTTING_ERROR) {
            resStatus = R.string.printer_cutting_error;

        } else if (status == PRINTER_PAPER_OUT) {
            resStatus = R.string.printer_paper_out;

        } else {
            resStatus = R.string.printer_unknown;
        }

        return IminApplication.mContext.getString(resStatus);
    }
}
