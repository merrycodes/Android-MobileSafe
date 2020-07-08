package com.itheima.mobilesafe.applock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.applock.utils.MD5Utils;

public class EnterPswActivity extends Activity implements View.OnClickListener{
    private ImageView mAppIcon;
    private TextView mAppNameTV;
    private EditText mPswET;
    private ImageView mGoImgv;
    private LinearLayout mEnterPswLL;
    private SharedPreferences sp;
    private String password;
    private String packagename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_psw);
        initView();
        initData();
        Log.e("111","onCreate");
    }
    //初始化控件
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("输入密码");
        findViewById(R.id.tv_back).setVisibility(View.GONE);
        mAppIcon = (ImageView) findViewById(R.id.imgv_appicon_enterpsw);
        mAppNameTV = (TextView) findViewById(R.id.tv_appname_enterpsw);
        mPswET = (EditText) findViewById(R.id.et_psw_enterpsw);
        mGoImgv = (ImageView) findViewById(R.id.imgv_go_enterpsw);
        mEnterPswLL = (LinearLayout) findViewById(R.id.ll_enterpsw);
        mGoImgv.setOnClickListener(this);
    }
    private void initData() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        password = sp.getString("password", null);
        Intent intent = getIntent();
        packagename = intent.getStringExtra("packagename");
        Log.e("111","packagename---"+packagename);
        PackageManager pm = getPackageManager();
        try {
            mAppIcon.setImageDrawable(pm.getApplicationInfo(packagename, 0).loadIcon(pm));
            mAppNameTV.setText(pm.getApplicationInfo(packagename, 0).
                    loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_go_enterpsw:
                //比较密码
                String inputpsw = mPswET.getText().toString().trim();
                if(TextUtils.isEmpty(inputpsw)){
                    startAnim();
                    Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(!TextUtils.isEmpty(password)){
                        if(MD5Utils.md5(inputpsw).equals(password)){
                            //发送自定义的广播消息，通知程序锁服务，对该应用暂停保护
                            Intent intent = new Intent();
                            intent.setAction("com.itheima.mobliesafe.applock");
                            intent.putExtra("packagename",packagename);
                            sendBroadcast(intent);
                            finish();
                        }else{
                            startAnim();
                            Toast.makeText(this, "密码不正确！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                break;
        }
    }
    private void startAnim() {
        Animation animation =AnimationUtils.loadAnimation(this, R.anim.shake);
        mEnterPswLL.startAnimation(animation);
    }
}