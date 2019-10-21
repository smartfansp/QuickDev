package com.fansp.quickdev.jpush;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Created by kang on 2018/3/26.
 */

public class MyPushService extends Service {
    private String mPackageName;
    private String mServiceName2 = "com.shanlian.yz365.ProtectService";
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent _intent) {
            // 调用系统广播，每一分钟回接收一次，如果service停止，在这里重启service
            Log.i("myservice", "调用广播...");
            if (_intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                keepService2();
            }
            mPackageName = SystemUtil.getPackageName(_context);
            if (!SystemUtil.isAppAlive(_context, mPackageName)) {
                // 根据包名打开app
                Intent launchIntent = _context.getPackageManager().getLaunchIntentForPackage(mPackageName);
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                _context.startActivity(launchIntent);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter _intentFilter = new IntentFilter();
        _intentFilter.setPriority(1000);
        _intentFilter.addAction(Intent.ACTION_TIME_TICK);
        _intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        _intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBroadcastReceiver, _intentFilter);
        keepService2();
        myServiceConnection=new MyServiceConnection();
    }
    MyServiceConnection myServiceConnection;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("myservice", "远程服务连接成功");
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，RemoteService被杀掉了
            Toast.makeText(MyPushService.this, "远程服务Remote被干掉", Toast.LENGTH_LONG).show();
            // 启动RemoteCastielService
            MyPushService.this.startService(new Intent(MyPushService.this, ProtectService.class));
            MyPushService.this.bindService(new Intent(MyPushService.this, ProtectService.class),
                    myServiceConnection, Context.BIND_IMPORTANT);
        }
    }
    /**
     * 清理内存时，调用的方法
     *
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("myservice", "this process is onTrimMemory...");
        keepService2();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent _intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        startService(new Intent(MyPushService.this, ProtectService.class));
    }
    /**
     * 监听服务是否停止，若停止，则重启service
     */
    private void keepService2() {
        if (!SystemUtil.isServiceAlive(this, mServiceName2)) {
            Log.i("myservice", "重新启动 ProtectService...");
            Intent intent = new Intent(MyPushService.this, ProtectService.class);
            this.startService(intent);
            Intent intent2 = new Intent(MyPushService.this, cn.jpush.android.service.PushService.class);
            this.startService(intent2);
        }
    }
}

