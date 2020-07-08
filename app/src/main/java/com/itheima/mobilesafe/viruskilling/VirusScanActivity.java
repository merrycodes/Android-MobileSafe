package com.itheima.mobilesafe.viruskilling;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VirusScanActivity extends Activity implements View.OnClickListener{
    private TextView mLastTimeTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus_scan);
        initView();
        copyDB("antivirus.db");
    }
    /**
     * 初始化界面控件
     */
    private void initView() {
        TextView tv_back = findViewById(R.id.tv_back);//获取返回键
        ((TextView) findViewById(R.id.tv_main_title)).setText("病毒查杀");//设置界面标题
        tv_back.setVisibility(View.VISIBLE);//显示返回键
        mLastTimeTV = findViewById(R.id.tv_last_scan_time);//获取显示上次查杀时间的控件
        tv_back.setOnClickListener(this);//设置返回键的点击事件的监听器
        //设置“全盘扫描”条目的点击事件的监听器
        findViewById(R.id.rl_all_scan_virus).setOnClickListener(this);
    }
    /**
     * 拷贝病毒数据库
     * @param dbname
     */
    private void copyDB(final String dbname) {
        new Thread(){
            public void run() {
                try {
                    File file = new File(getFilesDir(),dbname);
                    if(file.exists()&&file.length()>0){
                        Log.i("VirusScanActivity","数据库已存在！");
                        return ;
                    }
                    InputStream is = getAssets().open(dbname);
                    FileOutputStream fos  = openFileOutput(dbname, MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while((len = is.read(buffer))!=-1){
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
    /**
     *  实现界面上控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back: //返回键点击事件
                finish();
                break;
            case R.id.rl_all_scan_virus: //“全盘扫描”条目点击事件
                //跳转到病毒查杀进度界面
                Intent intent=new Intent(this,VirusScanSpeedActivity.class);
                startActivity(intent);
                break;
        }
    }

}

