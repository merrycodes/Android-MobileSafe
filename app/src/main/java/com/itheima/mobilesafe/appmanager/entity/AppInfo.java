package com.itheima.mobilesafe.appmanager.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String packageName; //应用包名
    public Drawable icon;       //应用图片
    public String appName;     //应用名称
    public String apkPath;     //应用路径
    public long appSize;       //应用大小
    public boolean isInRoom;  //是否在手机内存中
    public boolean isUserApp; //是否是用户应用
    public boolean isSelected = false;//是否选中，默认都为false
    /**获取应用在手机中的位置*/
    public String getAppLocation(boolean isInRoom) {
        if (isInRoom) {
            return "手机内存";
        } else {
            return "外部存储";
        }
    }
}
