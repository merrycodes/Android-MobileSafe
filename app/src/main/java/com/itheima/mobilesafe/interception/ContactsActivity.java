package com.itheima.mobilesafe.interception;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interception.adapter.InterceptionAdapter;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.interception.db.dao.NumBelongtoDao;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;
import com.itheima.mobilesafe.interception.utils.CopyDbUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    String[] permissionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        CopyDbUtils copyDbUtils = new CopyDbUtils(this);
        copyDbUtils.copyDB("phone.db");
        initView();
        getPermissions();
    }
    public void initView(){
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("选择联系人");
        listView = findViewById(R.id.listView);
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }
    public void getPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList = new String[]{"android.permission.READ_CONTACTS"};
            ArrayList<String> list = new ArrayList<String>();
            // 循环判断所需权限中有哪个尚未被授权
            for (int i = 0; i < permissionList.length; i++){
                if (ActivityCompat.checkSelfPermission(this, permissionList[i])
                        != PackageManager.PERMISSION_GRANTED)
                    list.add(permissionList[i]);
            }
            if (list.size()>0){
                ActivityCompat.requestPermissions(this,
                        list.toArray(new String[list.size()]), 1);
            }else{
                setData();
            }
        }else{
            setData();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if(permissions[i].equals("android.permission.READ_CONTACTS")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "读取联系人权限申请成功",
                            Toast.LENGTH_SHORT).show();
                    setData();
                }else{
                    Toast.makeText(this,"读取联系人权限申请失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public List<BlackContactInfo> getContacts() {
        List<BlackContactInfo> infos = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.
                Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString
                    (cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int isHas = Integer.parseInt(cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
            if (isHas > 0) {
                Cursor c = getContentResolver().query(ContactsContract.
                                CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
                                " = " + id, null, null);
                while (c.moveToNext()) {
                    BlackContactInfo info = new BlackContactInfo();
                    String number = c.getString(c.getColumnIndex(ContactsContract.
                            CommonDataKinds.Phone.NUMBER)).trim();
                    if (!name.equals(number)){
                        info.setContactName(name);
                    }
                    String location = NumBelongtoDao.getLocation(number.replace(" ",""));
                    info.setPhoneNumber(number);
                    info.setPlace(location);
                    infos.add(info);
                }
                c.close();
            }
        }
        cursor.close();
        return infos;
    }
    public void setData(){
        final List<BlackContactInfo> infos = getContacts();
        InterceptionAdapter adapter = new InterceptionAdapter(infos, this,3);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                BlackContactInfo info = infos.get(position);
                String num = info.getPhoneNumber();
                BlackNumberDao dao = new BlackNumberDao(ContactsActivity.this);
                if (dao.isNumberExist(num)){
                    Toast.makeText(ContactsActivity.this,"黑名单中已存在该号码",
                            Toast.LENGTH_SHORT).show();
                }else{
                    boolean flag = dao.add(info);
                    if (flag){
                        Toast.makeText(ContactsActivity.this,"添加黑名单成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

}



