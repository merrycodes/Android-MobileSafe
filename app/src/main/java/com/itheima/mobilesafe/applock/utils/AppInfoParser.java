package com.itheima.mobilesafe.applock.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itheima.mobilesafe.applock.entity.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoParser{
    /**
     *  获取手机中所有的应用程序
     */
    public static List<ApplicationInfo> getAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        List<ApplicationInfo> appinfos = new ArrayList<ApplicationInfo>();
        for(PackageInfo packInfo:packInfos){
            ApplicationInfo appinfo = new ApplicationInfo();
            String packname = packInfo.packageName;
            appinfo.packageName = packname;
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            appinfo. icon = icon;
            String appname = packInfo.applicationInfo.loadLabel(pm).toString();
            appinfo.appName = appname;
            appinfos.add(appinfo);
            appinfo = null;
        }
        return appinfos;
    }
}
