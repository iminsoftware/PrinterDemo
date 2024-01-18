package com.feature.tui.util;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil {

    public static void showShort(Context context, String text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int textID) {
        Toast.makeText(context.getApplicationContext(), textID, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String text) {
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int textID) {
        Toast.makeText(context.getApplicationContext(), textID, Toast.LENGTH_LONG).show();
    }

    private static void showMyToast(final Toast toast, final int cnt) {
        if (toast == null)
            return;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

    public static void showToastShorter(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        showMyToast(toast, 1500);
    }

    public static void showToastShortest(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        showMyToast(toast, 800);
    }

}
