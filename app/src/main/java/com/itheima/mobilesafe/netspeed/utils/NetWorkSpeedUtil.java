package com.itheima.mobilesafe.netspeed.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class NetWorkSpeedUtil {
    private Context context;
    private Handler mHandler;
    public NetWorkSpeedUtil(Context context, Handler mHandler){
        this.context = context;
        this.mHandler = mHandler;
    }

    private long getTotalRxBytes() {
        boolean  isSupported  = TrafficStats.getUidRxBytes(context.getApplicationInfo().
                uid) == TrafficStats.UNSUPPORTED;
        return isSupported ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
    }
    private long getTotalTxBytes() {
        boolean isSupported = TrafficStats.getUidTxBytes(context.getApplicationInfo().
                uid) == TrafficStats.UNSUPPORTED;
        return isSupported ? 0 :(TrafficStats.getTotalTxBytes()/1024);//转为KB
    }

    private long lastTotalRxBytes = 0;
    private long lastTimeRx = 0;
    private long lastTotalTxBytes = 0;
    private long lastTimeTx = 0;
    private  Timer txTimer;
    private Timer rxTimer;


    TimerTask rxTask = new TimerTask() {
        @Override
        public void run() {
            showRxSpeed();//更新下载文件的流量速度，该方法在后续创建
        }
    };
    TimerTask txTask = new TimerTask() {
        @Override
        public void run() {
            showTxSpeed();//更新上传文件的流量速度，该方法在后续创建
        }
    };
    public void startShowRxSpeed(){
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeRx = System.currentTimeMillis();
        rxTimer = new Timer();
        rxTimer.schedule(rxTask, 1000, 1000);// 1s后启动任务，每2s执行一次
    }
    public void startShowTxSpeed(){
        lastTotalTxBytes = getTotalTxBytes();
        lastTimeTx = System.currentTimeMillis();
        txTimer = new Timer();
        txTimer.schedule(txTask, 1000, 1000); // 1s后启动任务，每1s执行一次
    }
    private static final int MSG_RX_SPEED = 100;        //下载速度信息
    private static final int MSG_TX_SPEED = 101;       //上传速度信息
    //下载的速度
    private void showRxSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 /
                (nowTimeStamp - lastTimeRx));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 %
                (nowTimeStamp - lastTimeRx));//毫秒转换
        lastTimeRx = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_RX_SPEED;
        msg.obj = String.valueOf(speed) + "." + String.valueOf(speed2) ;//下载的网速
        mHandler.sendMessage(msg);//更新界面
    }
    //上传的速度
    private void showTxSpeed() {
        long nowTotalTxBytes = getTotalTxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalTxBytes - lastTotalTxBytes) * 1000 /
                (nowTimeStamp - lastTimeTx));//毫秒转换
        long speed2 = ((nowTotalTxBytes - lastTotalTxBytes) * 1000 %
                (nowTimeStamp - lastTimeTx));//毫秒转换
        lastTimeTx = nowTimeStamp;
        lastTotalTxBytes = nowTotalTxBytes;
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_TX_SPEED;
        msg.obj = String.valueOf(speed) + "." + String.valueOf(speed2) ; //上传的网速
        mHandler.sendMessage(msg);//更新界面
    }

    public void cancelRxSpeed(){
        rxTimer.cancel();
    }
    public void cancelTxSpeed(){
        txTimer.cancel();
    }


}
