package com.itheima.mobilesafe.clean.entity;
import android.graphics.drawable.Drawable;
import java.io.Serializable;
public class RubbishInfo implements Serializable {
    //序列化时为了保持版本的兼容性，即在版本升级时反序列化仍保持对象的唯一性。
    private static final long serialVersionUID = 1L;
    public String packagename; //软件包名
    public long rubbishSize;   //软件垃圾大小
    //软件图标 ，添加transient关键字修饰可使Drawable类型的数据在两个Activity之间传递
    public transient Drawable appIcon;
    public String appName;     //软件名称
}
