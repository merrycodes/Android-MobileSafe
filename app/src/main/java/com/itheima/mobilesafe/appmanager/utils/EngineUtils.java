package com.itheima.mobilesafe.appmanager.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.itheima.mobilesafe.appmanager.entity.AppInfo;
import com.stericson.RootTools.RootTools;

public class EngineUtils  {
    public static void startApplication(Context context, AppInfo appInfo) {
        PackageManager pm = context.getPackageManager();//获取包管理器
        //获取安装包的启动意图
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    public static void uninstallApplication(Context context, AppInfo appInfo) {
        if (appInfo.isUserApp) {//通过隐式意图卸载应用程序
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE); //当前Activity执行的卸载程序的动作
            intent.setData(Uri.parse("package:" + appInfo.packageName));
            context.startActivity(intent);
        } else {
            //系统应用，需要Root权限 利用linux命令删除文件
            if (!RootTools.isRootAvailable()) {
                Toast.makeText(context, "卸载系统应用，必须要Root权限",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                if (!RootTools.isAccessGiven()) {//判断是否授权Root权限
                    Toast.makeText(context, "请授权黑马小护卫Root权限",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                RootTools.sendShell("mount -o remount ,rw /system", 3000);
                RootTools.sendShell("rm -r " + appInfo.apkPath, 30000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void shareApplication(Context context, AppInfo appInfo) {
        Intent intent = new Intent("android.intent.action.SEND"); //启动发送短信的功能
        intent.addCategory("android.intent.category.DEFAULT");  //指定当前动作被执行的环境
        intent.setType("text/plain"); //分享的类型为文本类型
        intent.putExtra(Intent.EXTRA_TEXT,
                "推荐您使用一款软件，名称叫：" + appInfo.appName
                        + "下载路径：https://play.google.com/store/apps/details?id="
                        + appInfo.packageName); //分享的内容
        context.startActivity(intent);//跳转到发送短信的界面
    }


}
