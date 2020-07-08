package com.itheima.mobilesafe.applock.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.itheima.mobilesafe.applock.EnterPswActivity;
import com.itheima.mobilesafe.applock.db.dao.AppLockDao;
import com.itheima.mobilesafe.applock.utils.ForegroundAppUtil;

import java.util.List;

public class AppLockService extends Service {
    private AppLockDao dao;
    private List<String> packagenames;
    private Uri uri = Uri.parse("content://com.itheima.mobilesafe.applock");
    private MyObserver observer;
    private AppLockReceiver receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        dao = new AppLockDao(this);      // 创建AppLockDao实例
        packagenames = dao.findAll();
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri,true,observer);
        startApplockService();
        receiver = new AppLockReceiver();
        IntentFilter filter = new IntentFilter("com.itheima.mobliesafe.applock");
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    // 内容观察者
    class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            packagenames = dao.findAll();
            super.onChange(selfChange);
        }
    }
    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    private String packageName;
    private String tempStopProtectPackname;

    private void startApplockService() {
        new Thread() {
            public void run() {
                while (true) {
                    // 监视任务栈的情况。 最近使用的打开的任务栈在集合的最前面
                    packageName =
                            ForegroundAppUtil.getForegroundActivityName(AppLockService.this);
                    // 判断这个包名是否需要被保护。
                    if (packagenames.contains(packageName)) {
                        // 判断当前应用程序是否需要临时停止保护（输入了正确的密码）
                        if (!packageName.equals(tempStopProtectPackname)){
                            // 需要保护，弹出输入密码界面
                            Intent intent = new Intent(AppLockService.this, EnterPswActivity.class);
                            intent.putExtra("packagename", packageName);
                            Log.e("111","page---"+packageName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }
    // 广播接收者
    class AppLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.itheima.mobliesafe.applock".equals(intent.getAction())) {
                tempStopProtectPackname = intent.getStringExtra("packagename");
            }
        }
    }

}
