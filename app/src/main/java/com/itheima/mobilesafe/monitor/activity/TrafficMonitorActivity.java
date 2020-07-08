package com.itheima.mobilesafe.monitor.activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.monitor.utils.NetworkStatsHelper;
import com.itheima.mobilesafe.monitor.view.SportProgressView;

import java.util.ArrayList;
import java.util.Calendar;

public class TrafficMonitorActivity extends Activity implements
        View.OnClickListener {
    private TextView setting_meal;
    private SportProgressView sportview;   //自定义控件
    private TextView month_used,today_used,upper_limit,tv_date;
    private BarChart mBarChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_monitor);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){  //判断当前API是否大于24
            if (checkUsagePermission()){   //是否获取使用记录访问的权限
                initData();                //初始化数据
            }
        }
    }
    public void initView(){
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);   //标题
        title.setText("流量监控");
        TextView tv_back = findViewById(R.id.tv_back);        //返回键
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        sportview = findViewById(R.id.sportview);              //圆形进度条
        month_used = findViewById(R.id.month_used);             //本月已用
        today_used = findViewById(R.id.today_used);             //本日已用
        upper_limit = findViewById(R.id.upper_limit);           //本日流量上限
        mBarChart = findViewById(R.id.bar_chart);                //第三方柱状图
        findViewById(R.id.view_vdetails).setOnClickListener(this);    //本月详情
        findViewById(R.id.calibration).setOnClickListener(this);      //流量校准
        tv_date = findViewById(R.id.tv_date);                       //本月详情文本
        setting_meal = findViewById(R.id.setting_meal);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:            //返回键
                finish();
                break;
            case R.id.calibration:       //流量校准，点击该按钮跳转到设置套餐流量界面
                Intent settingMealIntent = new Intent(this,SettingMealActivity.class);
                startActivityForResult(settingMealIntent,1000);
                break;
            case R.id.view_vdetails:     //本月流量详情
                Intent FlowDetailsIntent = new Intent(this,FlowDetailsActivity.class);
                startActivity(FlowDetailsIntent);

                break;
        }
    }
    private boolean checkUsagePermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        if (!granted) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, 1);
            return false;
        }
        return true;
    }
    private int remainingDay = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            AppOpsManager appOps = (AppOpsManager)
                    getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Toast.makeText(this, "请开启android:get_usage_stats权限，" +
                        "否则无法获取程序的应用列表", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "已开启android:get_usage_stats权限",
                        Toast.LENGTH_SHORT).show();
                initData();   //初始化数据
            }
        }else if (requestCode == 1000 & resultCode == 1001){
            String totalFlow =  data.getStringExtra("totalFlow");
            if (!totalFlow.isEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sportview.setFlow(Long.parseLong(totalFlow),
                            Long.parseLong(monthMobile+""));
                }
                //当year年month月有多少天
                Long remainingFlow = Long.parseLong(totalFlow) * 1024 - monthMobile;
                int totalDays = getMonthOfDay(year,month);
                remainingDay = totalDays - day +1;
                upper_limit.setText(getFormetFlow(remainingFlow/remainingDay));
            }
        }
    }
    private int month;
    private int year;
    private int day;
    private NetworkStatsHelper networkStatsHelper;
    Long startTime;
    int startMonth;
    int startYear;
    int startDay;
    long monthMobile;
    long todyMobile;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initData(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);             //年份
        month = calendar.get(Calendar.MONTH)+1;        //月份
        day = calendar.get(Calendar.DAY_OF_MONTH);    //月份中的天数
        tv_date.setText("本月详情"+"("+month+"月)");    //设置本月详情中的月份
        NetworkStatsManager networkStatsManager = (NetworkStatsManager)
                getSystemService(NETWORK_STATS_SERVICE);
        networkStatsHelper = new NetworkStatsHelper(networkStatsManager,this);
        SharedPreferences sp = getSharedPreferences("traffic", 0);
        startTime = sp.getLong("startTime",-1);  //获取第一次开始记录的时间
        if (startTime == -1){             //第一次登录等于-1，则将当前第一次登陆的时间记录到sp中
            SharedPreferences.Editor editor = sp.edit();
            startTime = System.currentTimeMillis();
            editor.putLong("startTime",startTime);
            startYear = year;
            startMonth = month;
            startDay = day;
            editor.putInt("startYear",startYear);
            editor.putInt("startMonth",startMonth);
            editor.putInt("startDay",startDay);
            editor.commit();
            month_used.setText("0Kb");
            today_used.setText("0Kb");
        }else{             //不是第一次登陆
            startYear = sp.getInt("startYear",-1);
            startMonth = sp.getInt("startMonth",-1);
            startDay = sp.getInt("startDay",-1);
            if (year != startYear){   //是否是同一年
                //获取从月初到现在的流量
                monthMobile  = networkStatsHelper.getAllMonthMobile(this);
                //获取今日从零点到现在的流量
                todyMobile = networkStatsHelper.getAllTodayMobile(this);
            }else {
                if (month == startMonth) {   //是否为同一个月份
                    //获取从开始检测时间到现在流量
                    monthMobile = networkStatsHelper.getMonthMobile(this, startTime);
                    if (day == startDay) {    //是否为同一天
                        //获取当天开始监测时间到现在消耗的流量
                        todyMobile = networkStatsHelper.getTodayMobile(this, startTime);
                    }else{
                        todyMobile = networkStatsHelper.getAllTodayMobile(this);
                    }
                }
            }
            //设置已使用的流量
            month_used.setText(getFormetFlow(monthMobile));
            //  查询当天的流量，将流量设置到today_used中
            today_used.setText(getFormetFlow(todyMobile));
        }

        setBarChart();

        String totalFlow = sp.getString("totalFlow","");  //总流量
        if (!totalFlow.isEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Float.parseFloat(totalFlow) >= monthMobile/1024){
                    sportview.setFlow(Long.parseLong(totalFlow),
                            Long.parseLong(monthMobile+""));
                    //当year年month月有多少天
                    Long remainingFlow = Long.parseLong(totalFlow) * 1024 - monthMobile;
                    int totalDays = getMonthOfDay(year,month);
                    remainingDay = totalDays - day +1;
                    upper_limit.setText(getFormetFlow(remainingFlow/remainingDay));
                }else{
                    sportview.setFlow(Long.parseLong(totalFlow),
                            Long.parseLong(totalFlow+""));
                    upper_limit.setText("0K");
                }
            }
        }

    }
    public static String getFormetFlow(long flow) {
        String strSpeed = "";
        if (flow < 1024) {
            strSpeed =  flow+"K";
        } else if (flow < 1048576) {
            strSpeed = flow / 1024+"M";
        } else if (flow < 1073741824){
            strSpeed = String.format("%.3f", flow *1.0f/ 1048576) + "G";
        }
        return strSpeed;
    }
    String company = "M";         //流量的单位
    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<BarEntry> setData(){
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int x = 1; x <= getMonthOfDay(year, month); x++){
            Long mobile = 0l;
            if (startTime == -1){
                yValues.add(new BarEntry(x, 0));
                continue;
            }
            if (year != startYear){
                if (x <= day){
                    mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                }
            }else{
                if (month != startMonth){
                    if (x <= day){
                        mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                    }
                }else{
                    if (day != startDay){
                        if (startDay <= x && x <= day){
                            mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                        }
                    }else{
                        if (x == day){
                            mobile = networkStatsHelper.getTodayMobile(this,startTime);
                        }
                    }
                }
            }
            yValues.add(new BarEntry(x, mobile/1024));
        }
        return yValues;
    }
    public static int getMonthOfDay(int year,int month){
        int day = 0;
        if(year%4==0&&year%100!=0||year%400==0){
            day = 29;
        }else{
            day = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
        }
        return 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setBarChart() {
        ArrayList<BarEntry> yValues = setData();      //y 轴数据集
        // y 轴数据集
        BarDataSet barDataSet = new BarDataSet(yValues, "单位("+company+")");
        int color = getResources().getColor(R.color.blue_color);
        barDataSet.setColor(color);                      //设置柱状图的颜色
        BarData mBarData = new BarData(barDataSet);
        mBarChart.setData(mBarData);                     //设置柱状图的数据
        mBarChart.getDescription().setEnabled(false); //不显示标题
        mBarChart.setDrawBorders(false);                 //不显示边框阴影
        mBarChart.setEnabled(false);
        mBarChart.setDoubleTapToZoomEnabled(false);    //关闭双击缩放
        mBarChart.animateXY(800, 800);                    //图表数据显示动画
        XAxis xAxis = mBarChart.getXAxis();              // 获取 x 轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置 x 轴显示位置
        xAxis.setDrawGridLines(false);                     // 取消 垂直 网格线
        xAxis.setLabelRotationAngle(0f);               // 设置 x 轴 坐标旋转角度
        xAxis.setTextSize(10f);                          // 设置 x 轴 坐标字体大小
        xAxis.setAxisLineColor(Color.GRAY);            // 设置 x 坐标轴 颜色
        xAxis.setAxisLineWidth(1f);                     // 设置 x 坐标轴 宽度
        xAxis.setLabelCount(10);                         // 设置 x轴 的刻度数量
        YAxis mRAxis = mBarChart.getAxisRight();      // 获取 右边 y 轴
        mRAxis.setEnabled(false);                        // 隐藏 右边 Y 轴
        YAxis mLAxis = mBarChart.getAxisLeft();       // 获取 左边 Y轴
        mLAxis.setDrawAxisLine(false);                  // 取消 左边 Y轴 坐标线
        mLAxis.setDrawGridLines(false);                 // 取消 横向 网格线
        mLAxis.setEnabled(false);
        mLAxis.setLabelCount(10);                         // 设置 Y轴 的刻度数量
        mLAxis.setAxisMinimum(1);
        mRAxis.setDrawAxisLine(false);
        mRAxis.setLabelCount(1, false);
    }
}

