package com.itheima.mobilesafe.appmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.appmanager.adapter.AppManagerAdapter;
import com.itheima.mobilesafe.appmanager.entity.AppInfo;
import com.itheima.mobilesafe.appmanager.utils.GetAppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView mPhoneMemoryTV; //剩余手机内存
    private TextView mSDMemoryTV;     //剩余SD卡内存
    private ListView mListView;
    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos = new ArrayList<AppInfo>();
    private List<AppInfo> systemAppInfos = new ArrayList<AppInfo>();
    private AppManagerAdapter adapter;
    private TextView mAppNumTV; //应用程序个数
    private UninstallRececiver receciver;//接收卸载应用程序成功的广播
    protected static final int APP_LIST_DATA = 10; //获取应用列表数据
    protected static final int REFRESH_DATA = 15;  //刷新界面数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        //注册卸载应用程序的广播
        receciver = new UninstallRececiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receciver, intentFilter);

        initView();
        getMemoryFromPhone(); //获取剩余手机内存与SD卡内存大小
    }
    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.title_bar).setBackgroundColor(
                getResources().getColor(R.color.blue_color));
        TextView tv_back = findViewById(R.id.tv_back);
        TextView tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("软件管理");
        tv_back.setOnClickListener(this);
        tv_back.setVisibility(View.VISIBLE);
        mPhoneMemoryTV = findViewById(R.id.tv_phone_memory);
        mSDMemoryTV = findViewById(R.id.tv_sd_memory);
        mAppNumTV = findViewById(R.id.tv_app_number);
        mListView = findViewById(R.id.lv_app);
        getMemoryFromPhone();
        initData();
        initListener();

    }
    /**
     * 获取手机和SD卡剩余内存
     */
    private void getMemoryFromPhone() {
        long avail_sd = Environment.getExternalStorageDirectory().getFreeSpace();
        long avail_rom = Environment.getDataDirectory().getFreeSpace();
        //格式化内存
        String str_avail_sd = Formatter.formatFileSize(this, avail_sd);
        String str_avail_rom = Formatter.formatFileSize(this, avail_rom);
        mPhoneMemoryTV.setText("剩余手机内存：" + str_avail_rom);
        mSDMemoryTV.setText("剩余SD卡内存：" + str_avail_sd);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back: //实 现返回键的点击事件
                finish();
                break;
        }
    }
    /**
     * 卸载应用程序的广播接收者
     */
    class UninstallRececiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {//接收广播
            initData();
        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(receciver);//注销注册的广播
        receciver = null;
        super.onDestroy();
    }

    private void initData() {
        appInfos = new ArrayList<AppInfo>();
        new Thread() {
            public void run() {
                appInfos.clear();
                userAppInfos.clear();
                systemAppInfos.clear();
                appInfos.addAll(GetAppInfos.getAppInfos(AppManagerActivity.this));
                for (AppInfo appInfo : appInfos) {
                    //如果是用户App
                    if (appInfo.isUserApp) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(APP_LIST_DATA);
            };
        }.start();
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                if (adapter != null) {
                    new Thread() {
                        public void run() {
                            AppInfo mappInfo = (AppInfo) adapter.getItem(position);
                            //记录当前条目的状态
                            boolean flag = mappInfo.isSelected;
                            //将集合中所有条目的AppInfo变+为未选中状态
                            for (AppInfo appInfo : userAppInfos) {
                                appInfo.isSelected = false;
                            }
                            for (AppInfo appInfo : systemAppInfos) {
                                appInfo.isSelected = false;
                            }
                            if (mappInfo != null) {
                                //如果已经选中，则变为未选中
                                if (flag) {
                                    mappInfo.isSelected = false;
                                } else {
                                    mappInfo.isSelected = true;
                                }
                                mHandler.sendEmptyMessage(REFRESH_DATA);
                            }
                        };
                    }.start();
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= userAppInfos.size() + 1) {
                    mAppNumTV.setText("系统程序：" + systemAppInfos.size() + "个");
                } else {
                    mAppNumTV.setText("用户程序：" + userAppInfos.size() + "个");
                }
            }
        });
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case APP_LIST_DATA: //接收获取到的手机应用信息
                    if (adapter == null) {
                        adapter = new AppManagerAdapter(userAppInfos, systemAppInfos,
                                AppManagerActivity.this);
                    }
                    mListView.setAdapter(adapter);   //将添加完数据的adapter设置给列表控件
                    adapter.notifyDataSetChanged();  //刷新界面数据信息
                    break;
                case REFRESH_DATA: //接收条目是否被选中的信息
                    adapter.notifyDataSetChanged(); //刷新界面数据信息
                    break;
            }
        }
    };


}
