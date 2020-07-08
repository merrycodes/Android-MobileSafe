package com.itheima.mobilesafe.monitor.utils;

import android.Manifest;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.Calendar;

import static android.content.Context.TELEPHONY_SERVICE;
@RequiresApi(api = Build.VERSION_CODES.M)
public class NetworkStatsHelper{
    private Context context;
    NetworkStatsManager networkStatsManager;
    public NetworkStatsHelper(NetworkStatsManager networkStatsManager,Context context) {
        this.networkStatsManager = networkStatsManager;
        this.context = context;
    }
    public long getAllMonthMobile(Context context) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,              //网络类型
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),  //唯一的用户ID
                    getTimesMonthmorning(),                         //开始时间
                    System.currentTimeMillis());                   //结束时间
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getRxBytes() + bucket.getTxBytes())/1024;
    }
    private String getSubscriberId(Context context, int networkType) {
        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            return "";
        }
        return tm.getSubscriberId();
    }
    //获得本月第一天0点时间
    public static long getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,1);         //设置为1日,当前日期即为本月第一天
        cal.set(Calendar.HOUR_OF_DAY,0);          //将小时设置为0
        cal.set(Calendar. MINUTE, 0);              //将分钟设置为0
        cal.set(Calendar.SECOND, 0);               //将秒设置为0
        cal.set(Calendar.MILLISECOND, 0);         //将毫秒设置为0
        return (cal.getTimeInMillis());            //返回时间的毫秒值
    }
    public long getAllTodayMobile(Context context){
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,      //网络类型
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE), //唯一用户ID
                    getTodayTimesmorning(),                 //开始时间
                    System.currentTimeMillis());           //结束时间
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getTxBytes() + bucket.getRxBytes())/1024;
    }
    //获取当天的零点时间
    public static long getTodayTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis());
    }
    public long getTodayMobile(Context context,Long startTime) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    startTime,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getTxBytes() + bucket.getRxBytes())/1024;
    }
    public long getMonthMobile(Context context,Long startTime) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    startTime,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getRxBytes() + bucket.getTxBytes())/1024;
    }
    public long getAllTodayMobile(Context context,int year,int month,int day) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    getSpecificTimesMorning(year,month-1,day),
                    getSpecificTimesNight(year,month-1,day));
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getTxBytes() + bucket.getRxBytes())/1024;
    }
    //获取指定时间的零点时间
    public static long getSpecificTimesMorning(int year,int month,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year,month,day);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        long millis = calendar.getTimeInMillis();
        return millis;
    }
    //获取当天的23：59:59的毫秒时间
    public static long getSpecificTimesNight(int year,int month,int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year,month,day);
        cal.set(Calendar.DAY_OF_MONTH,day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MINUTE, 59);
        return (cal.getTimeInMillis());
    }

    public long getAllTodayWIFI(Context context,int year,int month,int day) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_WIFI,
                    getSubscriberId(context, ConnectivityManager.TYPE_WIFI),
                    getSpecificTimesMorning(year,month-1,day),
                    getSpecificTimesNight(year,month-1,day));
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getTxBytes() + bucket.getRxBytes())/1024;
    }
    public long getTodayWIFI(Context context,Long startTime) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    startTime,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return (bucket.getTxBytes() + bucket.getRxBytes())/1024;
    }

}
