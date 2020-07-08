package com.itheima.mobilesafe.interception.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class BlackNumberOpenHelper extends SQLiteOpenHelper{
    public BlackNumberOpenHelper(Context context) {
        super(context, "blackNumber.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacknumber (id integer primary key autoincrement,"
                +"phoneNum varchar(20), phoneName varchar(255),place varchar(255)," +
                "times integer)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
