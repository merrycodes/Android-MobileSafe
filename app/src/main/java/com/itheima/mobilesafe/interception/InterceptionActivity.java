package com.itheima.mobilesafe.interception;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interception.adapter.InterceptionAdapter;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;
import com.itheima.mobilesafe.interception.service.InterceptionService;

import java.util.ArrayList;
import java.util.List;

public class InterceptionActivity extends Activity implements View.OnClickListener {

    String[] permissionList;
    private ListView listView;
    private TextView no_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interception);
        getPermissions();
        initView();
    }
    public void getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList = new String[]{"android.permission.CALL_PHONE",
                    "android.permission.WRITE_CALL_LOG","android.permission.READ_CALL_LOG"};
            ArrayList<String> list = new ArrayList<String>();
            // 循环判断所需权限中有哪个尚未被授权
            for (int i = 0; i < permissionList.length; i++){
                if (ActivityCompat.checkSelfPermission(this, permissionList[i]) !=
                        PackageManager.PERMISSION_GRANTED){
                    list.add(permissionList[i]);
                }
            }
            if (list.size()>0){
                ActivityCompat.requestPermissions(this,
                        list.toArray(new String[list.size()]), 1);
            }else{
                //1、不需要申请权限，开启骚扰拦截服务
                Intent intent = new Intent(this,InterceptionService.class);
                startService(intent);
            }
        }else{
            //2、不需要申请权限，打开拦截电话的服务
            Intent intent = new Intent(this,InterceptionService.class);
            startService(intent);;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String  grantPermissions  = "";
        String  noPermissions = "";
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.CALL_PHONE")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantPermissions = grantPermissions+" 拨打电话 ";
                } else if (permissions[i].equals("android.permission.CALL_PHONE")
                        && grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    noPermissions = noPermissions+"打电话";
                }else if (permissions[i].equals("android.permission.WRITE_CALL_LOG")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantPermissions = grantPermissions+" 写入/删除通话记录 ";
                }else if (permissions[i].equals("android.permission.WRITE_CALL_LOG")
                        && grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    noPermissions = noPermissions+" 写入/删除通话记录 ";
                }else if (permissions[i].equals("android.permission.READ_CALL_LOG")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    grantPermissions = grantPermissions +" 读取通话记录 ";
                }else if (permissions[i].equals("android.permission.READ_CALL_LOG")
                        && grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    noPermissions = noPermissions +" 读取通话记录 ";
                }
            }
            if (noPermissions.isEmpty()){
                Toast.makeText(this,"获取"+grantPermissions+"的权限",
                        Toast.LENGTH_SHORT).show();
                //3、开启骚扰拦截的服务
                Intent intent = new Intent(this,InterceptionService.class);
                startService(intent);
            }else{
                if (grantPermissions.length() >0){
                    Toast.makeText(this,"获取"+grantPermissions+"的权限",
                            Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this,"没有获取"+noPermissions+"的权限",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color. blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("骚扰拦截");
        TextView tv_right = findViewById(R.id.tv_right); //黑名单按钮
        tv_right.setOnClickListener(this);
        tv_right.setText("黑名单");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        no_message = findViewById(R.id.no_message);
        //骚扰拦截列表
        listView = (ListView) findViewById(R.id.listView);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right://跳转到黑名单界面
                Intent intent = new Intent(this, BlackListActivity.class);
                startActivity(intent);
                break;
        }
    }
    private void notifyChanged() {
        BlackNumberDao dao = new BlackNumberDao(this);
        List<BlackContactInfo> infos = dao. getInterceptionTimes();
        if (infos.size()>0){
            listView.setVisibility(View.VISIBLE);
            no_message.setVisibility(View.GONE);
            InterceptionAdapter adapter = new InterceptionAdapter(infos, this,1);
            listView.setAdapter(adapter);
        }else{
            listView.setVisibility(View.GONE);
            no_message.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        notifyChanged();
    }

}




