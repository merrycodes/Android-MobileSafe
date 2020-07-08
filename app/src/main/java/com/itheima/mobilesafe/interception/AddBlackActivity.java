package com.itheima.mobilesafe.interception;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.interception.db.dao.NumBelongtoDao;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;
import com.itheima.mobilesafe.interception.utils.CopyDbUtils;

public class AddBlackActivity extends Activity  implements View.OnClickListener{
    private EditText phoneNum,phoneName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black);
        CopyDbUtils copyDbUtils = new CopyDbUtils(this);
        copyDbUtils.copyDB("phone.db");
        initView();
    }
    public void initView(){
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("添加黑名单");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        phoneNum = findViewById(R.id.phoneNum);
        phoneName = findViewById(R.id.phoneName);
        tv_back.setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.add:
                String num = phoneNum.getText().toString();
                String name = phoneName.getText().toString();
                BlackNumberDao dao = new BlackNumberDao(AddBlackActivity.this);
                if (num.trim().isEmpty()) {
                    Toast.makeText(this, "请输入需要拦截的号码", Toast.LENGTH_SHORT).show();
                }else if(dao.isNumberExist(num)) {
                    Toast.makeText(this, "黑名单中已存在该号码", Toast.LENGTH_SHORT).show();
                }else{
                    String location = NumBelongtoDao.getLocation(num);
                    BlackContactInfo info = new BlackContactInfo();
                    if(location !=null && !location.isEmpty()){
                        info.setPlace(location);
                    }
                    if (!name.isEmpty()) {
                        info.setContactName(name);
                    }
                    info.setPhoneNumber(num);
                    boolean flag= dao.add(info);
                    if (flag){
                        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }
}



