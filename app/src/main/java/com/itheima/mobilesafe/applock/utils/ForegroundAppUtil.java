package com.itheima.mobilesafe.applock.utils;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

public class ForegroundAppUtil {
    private static final long END_TIME = System.currentTimeMillis();   //结束时间
    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;  //7天
    private static final long START_TIME = END_TIME - TIME_INTERVAL;   //开始时间
    /**
     *  //获取栈顶的应用包名
     */
    public static String getForegroundActivityName(Context context) {
        String currentClassName = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager manager = (ActivityManager) context.getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            currentClassName = manager.getRunningTasks(1).get(0).
                    topActivity.getPackageName();
        } else {
            UsageStats initStat = getForegroundUsageStats(context, START_TIME, END_TIME);
            if (initStat != null) {
                currentClassName = initStat.getPackageName();
            }
        }
        return currentClassName;
    }
    /**
     *  获取记录前台应用的UsageStats对象
     */
    private static UsageStats getForegroundUsageStats(Context context, long startTime,
                                                      long endTime) {
        UsageStats usageStatsResult = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) return null;
            for (UsageStats usageStats : usageStatses) {
                if (usageStatsResult == null || usageStatsResult.getLastTimeUsed() <
                        usageStats.getLastTimeUsed()) {
                    usageStatsResult = usageStats;
                }
            }
        }
        return usageStatsResult;
    }
    /**
     *  通过UsageStatsManager获取List<UsageStats>集合
     */
    public static List<UsageStats> getUsageStatsList(Context context, long startTime,
                                                     long endTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager manager = (UsageStatsManager) context.getApplicationContext().
                    getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStatses = manager.
                    queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStatses == null || usageStatses.size() == 0) {// 没有权限，获取不到数据
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                return null;
            }
            return usageStatses;
        }
        return null;
    }

}
