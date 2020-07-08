package com.itheima.mobilesafe.viruskilling;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.viruskilling.adapter.ScanVirusAdapter;
import com.itheima.mobilesafe.viruskilling.dao.AntiVirusDao;
import com.itheima.mobilesafe.viruskilling.entity.ScanAppInfo;
import com.itheima.mobilesafe.viruskilling.utils.MD5Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VirusScanSpeedActivity extends Activity implements View.
        OnClickListener{
    private int total,process;
    private TextView mProcessTV,mScanAppTV;
    private PackageManager pm;
    private boolean flag,isStop;
    private Button mCancelBtn;
    private ImageView mScanningIcon;
    private RotateAnimation rani;
    private ListView mScanListView;
    private ScanVirusAdapter adapter;
    private List<ScanAppInfo> mScanAppInfos = new ArrayList<ScanAppInfo>();
    private SharedPreferences mSP;
    protected static final int SCAN_BENGIN = 100;
    protected static final int SCANNING = 101;
    protected static final int SCAN_FINISH = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_scan_speed);
        pm = getPackageManager();
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        initView();
        scanVirus();
    }
    private void initView() {
        TextView tv_back = findViewById(R.id.tv_back);
        ((TextView) findViewById(R.id.tv_main_title)).setText("病毒查杀进度");
        tv_back.setVisibility(View.VISIBLE);
        mProcessTV =  findViewById(R.id.tv_scan_process);
        mScanAppTV =  findViewById(R.id.tv_scan_app);
        mCancelBtn = findViewById(R.id.btn_cancel_scan);
        mScanListView =  findViewById(R.id.lv_scan_apps);
        adapter = new ScanVirusAdapter(mScanAppInfos, this);
        mScanListView.setAdapter(adapter);
        mScanningIcon = findViewById(R.id.iv_scanning_icon);
        startAnim();//开始扫描动画
        tv_back.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }
    private void startAnim() {
        if (rani == null) {
            //设置动画沿着顺时针方向旋转
            rani = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rani.setRepeatCount(Animation.INFINITE); //动画重复次数
        rani.setDuration(2000);//动画间隔2000毫秒
        mScanningIcon.startAnimation(rani); //开始动画
    }


    private void scanVirus() {
        flag = true;
        isStop = false;
        process = 0;
        mScanAppInfos.clear();
        new Thread() {
            public void run() {
                Message msg = Message.obtain();
                msg.what = SCAN_BENGIN;
                mHandler.sendMessage(msg);
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                total = installedPackages.size();
                for (PackageInfo info : installedPackages) {
                    if (!flag) {
                        isStop = true;
                        return;
                    }
                    String apkpath = info.applicationInfo.sourceDir;
                    //检查获取这个文件的MD5码
                    String md5info = MD5Utils.getFileMd5(apkpath);
                    String result = AntiVirusDao.checkVirus(md5info);
                    msg = Message.obtain();
                    msg.what = SCANNING;
                    ScanAppInfo scanInfo = new ScanAppInfo();
                    if (result == null) {
                        scanInfo.description = "扫描安全";
                        scanInfo.isVirus = false;
                    } else {
                        scanInfo.description = result;
                        scanInfo.isVirus = true;
                    }
                    process++;
                    scanInfo.packagename = info.packageName;
                    scanInfo.appName = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.appicon = info.applicationInfo.loadIcon(pm);
                    msg.obj = scanInfo;
                    msg.arg1 = process;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            };
        }.start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_BENGIN:
                    mScanAppTV.setText("初始化杀毒引擎中...");
                    break;
                case SCANNING:
                    ScanAppInfo info = (ScanAppInfo) msg.obj;
                    mScanAppTV.setText("正在扫描: " + info.appName);
                    int speed = msg.arg1;
                    mProcessTV.setText((speed * 100 / total) + "%");
                    mScanAppInfos.add(info);
                    adapter.notifyDataSetChanged();
                    mScanListView.setSelection(mScanAppInfos.size());
                    break;
                case SCAN_FINISH:
                    mScanAppTV.setText("扫描完成！");
                    mScanningIcon.clearAnimation();
                    mCancelBtn.setBackgroundResource(R.drawable.btn_finish_selector);
                    saveScanTime();
                    break;
            }
        }
    };
    private void saveScanTime() {
        SharedPreferences.Editor edit = mSP.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String currentTime = sdf.format(new Date());
        currentTime = "上次查杀： " + currentTime;
        edit.putString("lastVirusScan", currentTime);
        edit.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish(); //关闭当前界面
                break;
            case R.id.btn_cancel_scan:
                if (process == total & process > 0) {//扫描已完成
                    finish(); //关闭当前界面
                } else if (process > 0 & process < total & isStop == false) {
                    mScanningIcon.clearAnimation();//清除扫描动画
                    flag = false;//取消扫描
                    //更换重新扫描按钮的背景图片
                    mCancelBtn.setBackgroundResource(R.drawable.
                            btn_restart_scan_selector);
                } else if (isStop) {
                    startAnim(); //开始扫描动画
                    scanVirus(); //重新扫描
                    //更换取消扫描按钮背景图片
                    mCancelBtn.setBackgroundResource(R.drawable.
                            btn_cancel_scan_selector);
                }
                break;
        }
    }

}
