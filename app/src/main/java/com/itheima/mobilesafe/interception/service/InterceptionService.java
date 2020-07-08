package com.itheima.mobilesafe.interception.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.itheima.mobilesafe.interception.reciever.InterceptCallReciever;

public class InterceptionService extends Service {
    private InterceptCallReciever receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Log.e("111","12345");
        IntentFilter recevierFilter = new IntentFilter();
        recevierFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        recevierFilter.addAction("android.intent.action.PHONE_STATE");
        receiver = new InterceptCallReciever();
        registerReceiver(receiver, recevierFilter);
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

