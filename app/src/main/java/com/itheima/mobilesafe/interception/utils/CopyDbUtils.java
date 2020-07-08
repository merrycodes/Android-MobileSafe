package com.itheima.mobilesafe.interception.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class CopyDbUtils {
    private Context context;
    public CopyDbUtils(Context context){
        this.context = context;
    }
    //拷贝资产目录下的数据库文件
    public void copyDB(final String dbName) {
        new Thread(){
            public void run() {
                try {
                    File file = new File(context.getFilesDir(),dbName);
                    if(file.exists()&&file.length()>0){
                        return ;
                    }
                    InputStream is = context.getAssets().open(dbName);
                    FileOutputStream fos  = context.openFileOutput(dbName, MODE_PRIVATE);
                    byte[] buffer = new byte[512];
                    int len = 0;
                    while((len = is.read(buffer))!=-1){
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}

