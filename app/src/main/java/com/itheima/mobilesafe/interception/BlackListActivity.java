package com.itheima.mobilesafe.interception;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interception.adapter.InterceptionAdapter;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;

import java.util.List;

public class BlackListActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private View inflate;
    private TextView add_manual,add_contacts;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        initView();
    }
    public void  initView(){
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        findViewById(R.id.add).setOnClickListener(this);
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("黑名单");
        listView = findViewById(R.id.listView);
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        //对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.add_blacklist_dialog, null);
        add_manual = (TextView) inflate.findViewById(R.id.add_manual);
        add_contacts = (TextView) inflate.findViewById(R.id.add_contacts);
        add_manual.setOnClickListener(this);
        add_contacts.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.add:    //显示Dialog对话框
                showDialog();
                break;
            case R.id.add_manual: //跳转到添加黑名单界面
                Intent intent = new Intent(this,AddBlackActivity.class);
                startActivity(intent);
                break;
            case R.id.add_contacts: //跳转选择联系人界面
                Intent intent1 = new Intent(this,ContactsActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void showDialog() {
        if (dialog == null){
            dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
            dialog.setContentView(inflate);               //将布局设置给Dialog对象
            Window dialogWindow = dialog.getWindow();   //获取当前Activity所在的窗体
            dialogWindow.setGravity(Gravity.BOTTOM);    //设置Dialog从窗体底部弹出
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();  //获得窗体的属性
            lp.y = 20;                                         //设置Dialog距离底部的距离
            dialogWindow.setAttributes(lp);                // 将窗体的属性设置给窗体
        }
        dialog.show();//显示对话框
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null){
            dialog.dismiss();
        }
    }

    private void notifyChanged(){
        BlackNumberDao dao = new BlackNumberDao(this);
        List<BlackContactInfo> infos =  dao.getBlackList();
        InterceptionAdapter adapter = new InterceptionAdapter(infos,BlackListActivity.this,2);
        listView.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        notifyChanged();
    }


}
