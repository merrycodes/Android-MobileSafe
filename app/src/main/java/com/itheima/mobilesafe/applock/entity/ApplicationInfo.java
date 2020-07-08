package com.itheima.mobilesafe.applock.entity;
import android.graphics.drawable.Drawable;
public class ApplicationInfo {
    public String packageName; //应用程序包名
    public Drawable icon;      //应用程序图标
    public String appName;     //应用程序名称
    public boolean isLock;      //应用程序是否加锁
}
