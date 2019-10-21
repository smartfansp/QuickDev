package com.fansp.quickdev.jpush;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class SystemUtil {
    /**
     * 判断app是否存活
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                Log.i("MeiDeNi", String.format("this %s is running", packageName));
                return true;
            }
        }
        Log.i("MeiDeNi", String.format("this %s is not running", packageName));
        return false;
    }
    /**
     * 判断app是否在后台运行
     *
     * @return
     */
    public static boolean isAppOnBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getPackageName(context);
        List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
        if (taskInfos != null && taskInfos.size() > 0) {
            // 应用程序是否位于栈堆顶层
            if (!taskInfos.get(0).topActivity.getPackageName().equals(packageName)) {
                Log.i("MeiDeNi", String.format("this %s is running onBackground", packageName));
                return true;
            }
        }
        Log.i("MeiDeNi", String.format("this %s is running onForeground", packageName));
        return false;
    }
    /**
     * 判断服务是否在运行
     *
     * @return
     */
    public static boolean isServiceAlive(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(30);
        if (serviceInfos == null || serviceInfos.size() < 1) {
            return false;
        }
        for (int i = 0; i < serviceInfos.size(); i++) {
            if (serviceInfos.get(i).service.getClassName().equals(serviceName)) {
                Log.i("MeiDeNi", String.format("this %s is running", serviceName));
                return true;
            }
        }
        Log.i("MeiDeNi", String.format("this %s is not running", serviceName));
        return false;
    }
    /**
     * 获取应用程序包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }
}
