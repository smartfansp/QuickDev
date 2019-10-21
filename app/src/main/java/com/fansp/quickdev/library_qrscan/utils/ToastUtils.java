package com.fansp.quickdev.library_qrscan.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class ToastUtils {
    private static final long INTERVAL_TIME = 1000L;
    private static Toast sToast = null;
    private static Map<Object, Long> sLastMap = new HashMap();

    public static void showToast(final Context context, final String text, final int duration, final int location) {
        if (!TextUtils.isEmpty(text) && context != null) {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    if (ToastUtils.sLastMap.isEmpty() || !ToastUtils.sLastMap.containsKey(text) || currentTime - ((Long) ToastUtils.sLastMap.get(text)).longValue() > 1000L) {
                        if (ToastUtils.sToast != null) {
                            ToastUtils.sToast.setText(text);
                            ToastUtils.sToast.setDuration(duration);
                            ToastUtils.sToast.setGravity(location,0,0);
                        } else {
                            ToastUtils.sToast = Toast.makeText(context.getApplicationContext(), text, duration);
                        }

                        ToastUtils.sLastMap.put(text, Long.valueOf(currentTime + (long) duration));
                        ToastUtils.sToast.show();
                    }

                }
            });
        }
    }
    public static void showToast(Context context, int resId, int duration, int location) {
        showToast(context, context.getString(resId), duration, location);
    }
    public static void showToast(Context context, String text) {
        showToast(context, text, 1, Gravity.BOTTOM);
    }
    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration, Gravity.BOTTOM);
    }
    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId), 1, Gravity.BOTTOM);
    }
}
