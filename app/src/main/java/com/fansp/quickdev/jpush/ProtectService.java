package com.fansp.quickdev.jpush;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Created by kang on 2018/3/26.
 */

public class ProtectService extends Service {
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            Log.i("myservice", "调用广播...");
            if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService1();
            }
            String mPackageName = SystemUtil.getPackageName(_context);
            if (!SystemUtil.isAppAlive(_context, mPackageName)) {
                // 根据包名打开app
                Intent launchIntent = _context.getPackageManager().getLaunchIntentForPackage(mPackageName);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                _context.startActivity(launchIntent);
            }
        }
    };
//    MyServiceConnection myServiceConnection;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mBroadcastReceiver, filter);
        keepService1();
//        myServiceConnection = new MyServiceConnection();
    }
//    class MyServiceConnection implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//            Log.i("myservice", "远程服务连接成功");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            // 连接出现了异常断开了，RemoteService被杀掉了
//            Toast.makeText(ProtectService.this, "远程服务Remote被干掉", Toast.LENGTH_LONG).show();
//            // 启动RemoteCastielService
//            ProtectService.this.startService(new Intent(ProtectService.this, ProtectService.class));
//            ProtectService.this.bindService(new Intent(ProtectService.this, ProtectService.class),
//                    myServiceConnection, Context.BIND_IMPORTANT);
//        }
//
//    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        this.bindService(new Intent(this, ProtectService.class), myServiceConnection, Context.BIND_IMPORTANT);
//        NotificationManager mNotifiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification.Builder(this)
//                .setContentText("服务启动中")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();
//        mNotifiManager.notify(startId,notification);
        keepService1();
        return Service.START_STICKY;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("fansp_protect", "this process is onTrimMemory...");
        keepService1();
    }
    /**
     * 判断服务是否还在运行，若是已经停止，则重启service
     */
    private void keepService1() {
        String mServiceName2 = "com.sl.movesurvey.jpush.MyPushService";
        if (!SystemUtil.isServiceAlive(this, mServiceName2)) {
            Log.i("fansp_protect", "重新启动 MyPushService...");
            Intent intent = new Intent(ProtectService.this, MyPushService.class);
            this.startService(intent);
            Intent intent2 = new Intent(ProtectService.this, cn.jpush.android.service.PushService.class);
            this.startService(intent2);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        startService(new Intent(ProtectService.this, MyPushService.class));
    }
    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return null;
    }
}
