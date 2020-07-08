package com.itheima.mobilesafe.interception.reciever;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class InterceptCallReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BlackNumberDao dao = new BlackNumberDao(context);
        Log.e("111","收到广播");
        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Log.e("111","ACTION_NEW_OUTGOING_CALL");
            String mIncomingNumber = "";
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:  //来电响铃
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Log.e("111","mIncomingNumber---"+mIncomingNumber);
                    if (mIncomingNumber == null){
                        return;
                    }
                    if(dao.isNumberExist(mIncomingNumber)){
                        //注册内容观察者
                        Uri uri = Uri.parse("content://call_log/calls");
                        context.getContentResolver().registerContentObserver(
                                uri,
                                true,
                                new CallLogObserver(new Handler(), mIncomingNumber,
                                        context));
                        //挂断电话
                        endCall(context);
                        dao.update(mIncomingNumber);
                    }
                    break;
            }
        }
    }

    //挂断电话
    public void endCall(Context context) {
        try {
            Class<?> clazz = context.getClassLoader().loadClass(
                    "android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null,
                    Context.TELEPHONY_SERVICE);
            ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
            itelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;
        private Context context;
        public CallLogObserver(Handler handler, String incomingNumber,
                               Context context) {
            super(handler);
            this.incomingNumber = incomingNumber;
            this.context = context;
        }
        // 观察到数据库内容变化调用的方法
        @Override
        public void onChange(boolean selfChange) {
            context.getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber, context);
            super.onChange(selfChange);
        }
    }
    //清除呼叫记录
    public void deleteCallLog(String incomingNumber, Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");  //通话记录
        Cursor cursor = resolver.query(uri, new String[] { "_id" }, "number=?",
                new String[] { incomingNumber }, "_id desc limit 1");
        if (cursor.moveToNext()) {
            String id = cursor.getString(0);
            int flag = resolver.delete(uri, "_id=?", new String[] { id });
        }
    }

}

