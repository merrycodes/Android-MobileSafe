package com.itheima.mobilesafe.interception.db.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.interception.db.BlackNumberOpenHelper;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private Context context;
    private BlackNumberOpenHelper blackNumberOpenHelper;
    public BlackNumberDao(Context context) {
        super();
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
        context = context;
    }
    /*
    * 查询电话拦截次数大于0的数据
    */
    public List<BlackContactInfo> getInterceptionTimes(){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        List<BlackContactInfo> infos = new ArrayList<>();
        //查询数据库中是否骚扰拦截次数大于1的，如果有则添加到集合中
        Cursor cursor = db.query("blacknumber", null, "times>?", new String[]{"0"},
                null, null, null);
        while(cursor.moveToNext()) {
            //如果查询到该数据，则将该数据添加到集合中
            BlackContactInfo info = new BlackContactInfo();
            int times = cursor.getInt(cursor.getColumnIndex("times"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String contactName = cursor.getString(cursor.getColumnIndex("phoneName"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNum"));
            info.setTimes(times);
            info.setPlace(place);
            info.setContactName(contactName);
            info.setPhoneNumber(phoneNumber);
            infos.add(info);
        }
        return infos;
    }

    /*
     * 删除数据
     */
    public boolean detele(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int rownumber = db.delete("blacknumber", "phoneNum=?",
                new String[] { blackContactInfo.getPhoneNumber()});
        if (rownumber == 0){
            return false; // 删除数据不成功
        }else{
            return true; // 删除数据成功
        }
    }
    /*
    * 查询数据库中的所有数据
    */
    public List<BlackContactInfo> getBlackList(){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        List<BlackContactInfo> infos = new ArrayList<>();
        Cursor cursor = db.query("blacknumber", null, null,null,null,null, null);
        while (cursor.moveToNext()){                  //移动光标到下一行
            //如果查询到该数据，则将该数据添加到集合中
            BlackContactInfo info = new BlackContactInfo();
            int times = cursor.getInt(cursor.getColumnIndex("times"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            String contactName = cursor.getString(cursor.getColumnIndex("phoneName"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNum"));
            info.setTimes(times);
            info.setPlace(place);
            info.setContactName(contactName);
            info.setPhoneNumber(phoneNumber);
            infos.add(info);
        }
        return infos;
    }
    /*
     * 判断号码是否在黑名单数据库中存在
     */
    public boolean isNumberExist(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", null, "phoneNum=?",
                new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
    /*
     * 添加数据
     */
    public boolean add(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (blackContactInfo.getPhoneNumber().startsWith("+86")) {
            blackContactInfo.setPhoneNumber(blackContactInfo.getPhoneNumber()
                    .substring(3, blackContactInfo.getPhoneNumber().length()));
        }
        values.put("phoneNum", blackContactInfo.getPhoneNumber());
        values.put("phoneName", blackContactInfo.getContactName());
        values.put("place",blackContactInfo.getPlace());
        values.put("times", blackContactInfo.getTimes());
        long rowid = db.insert("blacknumber", null, values);
        if (rowid == -1){ // 插入数据不成功
            return false;
        }else{
            return true;
        }
    }
    /*
     * 更新来电电话的拦截次数
     */
    public boolean update(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", null, "phoneNum=?",
                new String[] { number }, null, null, null);
        if (cursor.moveToNext()) {
            int times = cursor.getInt(cursor.getColumnIndex("times"));
            if (String.valueOf(times) == null){
                times = 1;
            }else{
                times = times +1;
            }
            ContentValues values = new ContentValues();
            values.put("times", times);
            db.update("blacknumber",values,"phoneNum=?",new String[] {number});
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

}
