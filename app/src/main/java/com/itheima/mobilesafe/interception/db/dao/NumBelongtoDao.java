package com.itheima.mobilesafe.interception.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumBelongtoDao {
    //返回电话号码的归属地
    public static String getLocation(String phonenumber) {
        String location = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.itheima.mobilesafe/files/phone.db", null,
                SQLiteDatabase.OPEN_READONLY);
        //通过正则表达式匹配号段, 13X  14X  15X  17X  18X,
        // 130 131 132 133 134 135 136 137 138 139
        if (phonenumber.matches("^1[34578]\\d{9}$")) {
            // 手机号码的查询
            Cursor cursor = db.query("phones", null, "number=?",
                    new String[]{phonenumber.substring(0,7)}, null, null, null);
            String region_id = null;
            if (cursor.moveToNext()) {
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                region_id = cursor.getString(cursor.getColumnIndex("region_id"));
                switch (type){
                    case 1:
                        location = "移动";
                        break;
                    case 2:
                        location = "联通";
                        break;
                    case 3:
                        location = "电信";
                        break;
                    case 4:
                        location = "电信虚拟运营商";
                        break;
                    case 5:
                        location = "联通虚拟运营商";
                        break;
                    case 6:
                        location = "移动虚拟运营商";
                        break;
                }
            }
            if (region_id !=null || region_id.isEmpty()){
                Cursor cursor1 = db.rawQuery("select * from regions where id = ?",
                        new String[]{region_id});
                if (cursor1.moveToNext()) {
                    String city = cursor1.getString(cursor1.getColumnIndex("city"));
                    String province = cursor1.getString(cursor1.getColumnIndex(
                            "province"));
                    if(city.equals(province)){
                        location = city +" | "+ location;
                    }else{
                        location = province + city +" | "+ location;
                    }
                }
                cursor1.close();
            }
        }else {// 其他电话
            switch (phonenumber.length()) {// 判断电话号码的长度
                case 3: // 110 120 119 121 999
                    if ("110".equals(phonenumber)) {
                        location = "匪警";
                    } else if ("120".equals(phonenumber)) {
                        location = "急救";
                    } else {
                        location = "报警号码";
                    }
                    break;
                case 4:
                    location = "模拟器";
                    break;
                case 5:
                    location = "客服电话";
                    break;
                case 7:
                    location = "本地电话";
                    break;
                case 8:
                    location = "本地电话";
                    break;
                default:
                    break;
            }
        }
        db.close();
        return location;
    }
}
