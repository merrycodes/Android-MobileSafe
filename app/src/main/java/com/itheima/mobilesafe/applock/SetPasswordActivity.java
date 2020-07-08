package com.itheima.mobilesafe.applock;

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
import com.itheima.mobilesafe.applock.utils.MD5Utils;

public class SetPasswordActivity extends Activity implements View.OnClickListener {
    private EditText enter_psw1,enter_psw2;
    private  String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String password = sp.getString("password","");
        if (!password.isEmpty()){
            //跳转到程序锁界面
            Intent intent = new Intent(this,AppLockActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("设置密码");
        enter_psw1 = findViewById(R.id.enter_psw1);
        enter_psw2 = findViewById(R.id.enter_psw2);
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        findViewById(R.id.bt_enter).setOnClickListener(this);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.bt_enter:
                String psw1 =  enter_psw1.getText().toString();
                String psw2 = enter_psw2.getText().toString();
                if (psw1!=null&& psw2 != null && !psw1.isEmpty()&& !psw2.isEmpty()){
                    if (psw1.equals(psw2)){
                        SharedPreferences sp = getSharedPreferences("config",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("password",MD5Utils.md5(psw1));
                        editor.commit();
                        //跳转到程序锁界面
                        Intent intent = new Intent(this,AppLockActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(this, "密码不一致，请重新输入",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

