package com.itheima.mobilesafe.netspeed.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

import java.util.Date;

public class TestReportActivity extends Activity implements View.OnClickListener  {


    private TextView tv_rxspeed,tv_txspeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_report);
        initView();
        initData();
    }
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("测试报告");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_rxspeed = findViewById(R.id.tv_rxspeed);
        tv_txspeed = findViewById(R.id.tv_txspeed);
        tv_back.setOnClickListener(this);
        findViewById(R.id.restart).setOnClickListener(this);

    }
    private void  initData(){
        Intent intent = getIntent();
        float rxspeed = intent.getFloatExtra("rxspeed",0.0f);  //下载速度
        float txspeed = intent.getFloatExtra("txspeed",0.0f);  //上传速度
        tv_rxspeed.setText(getFormetSpeed(rxspeed));
        tv_txspeed.setText(getFormetSpeed(txspeed));
        TextView tv_time = findViewById(R.id.tv_time);
        Date dt = new Date();
        String time = dt.toLocaleString();
        tv_time.setText(time);
    }
    public  String getFormetSpeed(float flow) {
        String strSpeed = "";
        if (flow < 1024) {
            strSpeed = String.format("%.2f", flow)+"K/s";
        } else if (flow < 1048576) {
            strSpeed = String.format("%.2f", flow*1.0 / 1024)+"M/s";
        } else if (flow > 1073741824){
            strSpeed = String.format("%.2f", flow*1.0 / 1048576)+ "G/s";
        }
        return strSpeed;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:          //返回键
                finish();
                break;
            case R.id.restart:          //重新测试
                Intent intent = new Intent(this,NetDetectionActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


}
