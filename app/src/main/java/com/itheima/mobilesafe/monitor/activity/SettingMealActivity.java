package com.itheima.mobilesafe.monitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;

public class SettingMealActivity extends Activity implements View.OnClickListener {
    private EditText total_flow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_meal);
        initView();
    }
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("设置套餐流量");
        TextView tv_right =  findViewById(R.id.tv_right);
        tv_right.setText("完成");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        total_flow = findViewById(R.id.total_flow);
        tv_right.setOnClickListener(this);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right:
                String flow = total_flow.getText().toString();
                if (flow.isEmpty()){
                    Toast.makeText(SettingMealActivity.this,"请输入套餐总流量",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences("traffic", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("totalFlow",flow);
                editor.commit();
                Intent intent = new Intent();
                intent.putExtra("totalFlow",flow);
                setResult(1001,intent);
                finish();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

}
