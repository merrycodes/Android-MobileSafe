-ignorewarnings                # 抑制警告
-keep class com.itheima.mobilesafe.applock.entity.** { *; } #保持实体类不被混淆
-keep class com.itheima.mobilesafe.appmanager.entity.** { *; } #保持实体类不被混淆
-keep class com.itheima.mobilesafe.clean.entity.** { *; } #保持实体类不被混淆
-keep class com.itheima.mobilesafe.interception.entity.** { *; } #保持实体类不被混淆
-keep class com.itheima.mobilesafe.network.monitor.entity.** { *; } #保持实体类不被混淆
-keep class com.itheima.mobilesafe.viruskilling.entity.** { *; } #保持实体类不被混淆
-keep class com.android.internal.telephony.** { *; }
-optimizationpasses 5               # 指定代码的压缩级别
-dontusemixedcaseclassnames        # 是否使用大小写混合
-dontpreverify                       # 混淆时是否做预校验
-verbose                              # 混淆时是否记录日志
 # 指定混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 对于继承Android的四大组件等系统类，保持不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keepclasseswithmembernames class * { #保持 native方法不被混淆
    native <methods>;
}
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
#保持枚举类enum不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保持 Parcelable的类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#保持继承自View对象中的set/get方法以及初始化方法的方法名不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#对所有类的初始化方法的方法名不进行混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#对于R(资源)下的所有类及其方法，都不能被混淆
-keep class **.R$* {
 *;
}
#对于带有回调函数onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}
