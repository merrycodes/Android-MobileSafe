package com.itheima.mobilesafe.monitor.activity;

import android.app.Activity;
import android.app.usage.NetworkStatsManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.monitor.adapter.FlowDetilsAdapter;
import com.itheima.mobilesafe.monitor.entity.TrafficInfo;
import com.itheima.mobilesafe.monitor.utils.NetworkStatsHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FlowDetailsActivity extends Activity implements View.OnClickListener {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_details);
        initView();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("本月详情");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        listView = findViewById(R.id.listView);
        List<TrafficInfo> infos = setData();
        FlowDetilsAdapter adapter = new FlowDetilsAdapter(infos,this);
        listView.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<TrafficInfo> setData(){
        List<TrafficInfo> infos =  new ArrayList<>();
        NetworkStatsManager networkStatsManager = (NetworkStatsManager)
                getSystemService(NETWORK_STATS_SERVICE);
        NetworkStatsHelper networkStatsHelper = new
                NetworkStatsHelper(networkStatsManager,this);
        SharedPreferences sp = getSharedPreferences("traffic", 0);
        Long  startTime = sp.getLong("startTime",-1);
        int startYear = sp.getInt("startYear",-1);
        int startMonth = sp.getInt("startMonth",-1);
        int startDay = sp.getInt("startDay",-1);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int totalDay = 30;
        for (int x = 1; x <= totalDay; x++){
            if (startTime == -1){
                continue;
            }
            long mobile = 0l;
            long wifi = 0l;
            if (year != startYear){
                if (x <= day){
                    mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                    wifi =   networkStatsHelper.getAllTodayWIFI(this,year,month,x);
                    String date = year+"-"+month+"-"+x;
                    TrafficInfo info = new TrafficInfo();
                    info.setMobiletraffic(mobile+"");
                    info.setWifi(wifi+"");
                    info.setDate(date);
                    infos.add(info);
                }
            }else{
                if (month != startMonth){
                    if (x <= day){
                        mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                        wifi =   networkStatsHelper.getAllTodayWIFI(this,year,month,x);
                        String date = year+"-"+month+"-"+x;
                        TrafficInfo info = new TrafficInfo();
                        info.setMobiletraffic(mobile+"");
                        info.setWifi(wifi+"");
                        info.setDate(date);
                        infos.add(info);
                    }
                }else{
                    if (day != startDay){
                        if (startDay <= x && x <= day){
                            mobile = networkStatsHelper.getAllTodayMobile(this,year,month,x);
                            wifi =   networkStatsHelper.getAllTodayWIFI(this,year,month,x);
                            String date = year+"-"+month+"-"+x;
                            TrafficInfo info = new TrafficInfo();
                            info.setMobiletraffic(mobile+"");
                            info.setWifi(wifi+"");
                            info.setDate(date);
                            infos.add(info);
                        }
                    }else{
                        if (x == day){
                            mobile  = networkStatsHelper.getTodayMobile(this,startTime);
                            wifi = networkStatsHelper.getTodayWIFI(this,startTime);
                            String date = year+"-"+month+"-"+x;
                            TrafficInfo info = new TrafficInfo();
                            info.setMobiletraffic(mobile+"");
                            info.setWifi(wifi+"");
                            info.setDate(date);
                            infos.add(info);
                        }
                    }
                }
            }
        }
        return infos;
    }

}
