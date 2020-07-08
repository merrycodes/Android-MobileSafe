package com.itheima.mobilesafe.viruskilling.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
    /**
     * 检查某个md5是否是病毒
     * @param md5
     * @return null 代表扫描安全
     */
    public static String checkVirus(String md5) {
        String desc = null;
        // 打开病毒数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                "/data/data/com.itheima.mobilesafe/files/antivirus.db", null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select desc from datable where md5=?",
                new String[] { md5 });
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }
}
