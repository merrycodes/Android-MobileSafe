package com.itheima.mobilesafe.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.applock.SetPasswordActivity;
import com.itheima.mobilesafe.appmanager.AppManagerActivity;
import com.itheima.mobilesafe.clean.CleanRubbishListActivity;
import com.itheima.mobilesafe.home.view.ArcProgressBar;
import com.itheima.mobilesafe.interception.InterceptionActivity;
import com.itheima.mobilesafe.monitor.activity.TrafficMonitorActivity;
import com.itheima.mobilesafe.netspeed.activity.NetDetectionActivity;
import com.itheima.mobilesafe.viruskilling.VirusScanActivity;

import java.io.File;

public class HomeActivity extends Activity implements View.OnClickListener {
    private ArcProgressBar pb_sd;
    private ArcProgressBar pb_rom;
    private int sd_used;
    private int rom_used;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getMemoryFromPhone();
        init();
    }

    /**
     * 初始化界面控件
     */
    private void init() {
        // 权限请求 允许访问设备上的图片、媒体内容和文件
        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        // 设置 main_title_bar
        //设置标题栏背景颜色
        findViewById(R.id.title_bar).setBackgroundResource(R.color.blue_color);
        //界面标题
        TextView tv_title = findViewById(R.id.tv_main_title); //获取界面标题控件
        tv_title.setText("手机安全卫士");        //设置界面标题

        //显示手机清理、骚扰拦截、病毒查杀、软件管理信息对应的布局
        LinearLayout ll_clean = findViewById(R.id.ll_clean); //获取手机清理布局
        LinearLayout ll_interception = findViewById(R.id.ll_interception); //获取骚扰拦截布局
        LinearLayout ll_security = findViewById(R.id.ll_security);          //获取病毒查杀布局
        LinearLayout ll_software_manager = findViewById(R.id.ll_software_manager);//获取软件管理布局


        //显示程序锁、网速测试、流量监控信息对应的布局
        RelativeLayout rl_app_lock = findViewById(R.id.rl_app_lock);     //获取程序锁布局
        RelativeLayout rl_speed_test = findViewById(R.id.rl_speed_test);//获取网速测试布局
        RelativeLayout rl_netraffic = findViewById(R.id.rl_netraffic);   //获取流量监控布局

        // 设置 ArcProgressBar
        pb_sd = findViewById(R.id.pb_sd);                 //获取显示SD卡信息的进度条控件
        pb_rom = findViewById(R.id.pb_rom);              //获取显示内存信息的进度条控件
        pb_sd.setMax(100);                //设置进度条的最大值为100
        pb_sd.setTitle("存储空间");      //设置进度条标题
        new MyAsyncSDTask().execute(0);   //实现存储空间信息显示（后续创建）
        pb_rom.setMax(100);              //设置进度条的最大值为100
        pb_rom.setTitle("内存");         //设置进度条标题
        new MyAsyncRomTask().execute(0); //实现内存空间信息显示（后续创建）

        ll_clean.setOnClickListener(this);             //设置手机清理布局的点击事件监听器
        ll_interception.setOnClickListener(this);    //设置骚扰拦截布局的点击事件监听器
        ll_security.setOnClickListener(this);         //设置病毒查杀布局的点击事件监听器
        ll_software_manager.setOnClickListener(this);//设置软件管理布局的点击事件监听器
        rl_app_lock.setOnClickListener(this);         //设置程序锁布局的点击事件监听器
        rl_speed_test.setOnClickListener(this);      //设置网速测试布局的点击事件监听器
        rl_netraffic.setOnClickListener(this);       //设置流量监控布局的点击事件监听器
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (!(permissions[i].equals("android.permission.WRITE_EXTERNAL_STORAGE")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 获取手机内置SD卡与系统内存使用量与总量占比信息
     */
    private void getMemoryFromPhone() {
        //获取内置SD卡路径/storage/emulated/0
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());              //获取内置SD卡的存储信息
        long blockSize = stat.getBlockSizeLong();              //获取存储块的大小
        long totalBlocks = stat.getBlockCountLong();          //获取存储块的数量
        long total_sd = blockSize * totalBlocks;                     //获取内置SD卡总存储空间
        long availableBlocks = stat.getAvailableBlocksLong();//获取剩余存储块的数量
        long avail_sd = blockSize * availableBlocks;                //获取内置SD卡剩余存储空间
        //获取ActivityManager服务的对象
        ActivityManager mActivityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        //获得MemoryInfo对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo);
        long total_rom = memoryInfo.totalMem; //获取手机系统总内存
        long avail_rom = memoryInfo.availMem; //获取手机系统剩余内存
        //计算内置SD卡使用量与总量的占比
        sd_used = 100 - (int) (((double) avail_sd / (double) total_sd) * 100);
        //计算系统内存使用量与总量占比
        rom_used = 100 - (int) (((double) avail_rom / (double) total_rom) * 100);
    }


    /**
     * {@link MyAsyncRomTask#doInBackground(Integer...)} 中的 publishProgress 每次调用都会调用
     * {@link MyAsyncRomTask#onProgressUpdate(Integer...)}方法
     * 目的是进入应用中 {@link ArcProgressBar} 有ui动画
     */
    @SuppressLint("StaticFieldLeak")
    private class MyAsyncRomTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            Integer timer = 0;
            while (timer <= rom_used) {
                try {
                    publishProgress(timer);//更新进度
                    timer++;         //timer的值自增
                    Thread.sleep(5); //延迟5毫秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb_rom.setProgress(values[0]);//设置ArcProgressBar的进度
        }
    }

    /**
     * @see MyAsyncRomTask 注解
     */
    @SuppressLint("StaticFieldLeak")
    private class MyAsyncSDTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            Integer timer = 0;
            while (timer <= sd_used) {
                try {
                    publishProgress(timer); //更新进度
                    timer++;          //timer的值自增
                    Thread.sleep(5); //延迟5毫秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb_sd.setProgress(values[0]);//设置ArcProgressBar的进度
        }
    }

    /**
     * 实现界面上控件的点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_clean:           //手机清理
                Intent cleanIntent = new Intent(this, CleanRubbishListActivity.class);
                startActivity(cleanIntent);
                break;
            case R.id.ll_interception:   //骚扰拦截
                Intent interceptionIntent = new Intent(this, InterceptionActivity.class);
                startActivity(interceptionIntent);
                break;
            case R.id.ll_security:       //病毒查杀
                Intent virusIntent = new Intent(this, VirusScanActivity.class);
                startActivity(virusIntent);

                break;
            case R.id.ll_software_manager: //软件管理
                Intent appManagerIntent = new Intent(this, AppManagerActivity.class);
                startActivity(appManagerIntent);
            case R.id.rl_app_lock:         //程序锁
                Intent appLockIntent = new Intent(this, SetPasswordActivity.class);
                startActivity(appLockIntent);
                break;
            case R.id.rl_speed_test:      //网速测试
                Intent speedIntent = new Intent(this, NetDetectionActivity.class);
                startActivity(speedIntent);
                break;
            case R.id.rl_netraffic:       //流量监控
                Intent netrafficIntent = new Intent(this, TrafficMonitorActivity.class);
                startActivity(netrafficIntent);
                break;
        }
    }
}