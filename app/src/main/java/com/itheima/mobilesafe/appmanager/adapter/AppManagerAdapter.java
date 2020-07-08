package com.itheima.mobilesafe.appmanager.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.appmanager.entity.AppInfo;
import com.itheima.mobilesafe.appmanager.utils.EngineUtils;

import java.util.List;

public class AppManagerAdapter extends BaseAdapter {
    private List<AppInfo> UserAppInfos;    //用户程序集合
    private List<AppInfo> SystemAppInfos; //系统程序集合
    private Context context;
    public AppManagerAdapter(List<AppInfo> userAppInfos, List<AppInfo>
            systemAppInfos, Context context) {
        super();
        UserAppInfos = userAppInfos;
        SystemAppInfos = systemAppInfos;
        this.context = context;
    }
    @Override
    public int getCount() {
        //因为有两个条目需要用于显示用户程序与系统程序，因此返回值需要加2
        return UserAppInfos.size() + SystemAppInfos.size() + 2;
    }
    @Override
    public Object getItem(int position) {
        if (position == 0) {
            //第0个位置显示的应该是用户程序的个数的标签
            return null;
        } else if (position == (UserAppInfos.size() + 1)) {
            return null;
        }
        AppInfo appInfo;
        if (position < (UserAppInfos.size() + 1)) {
            //用户程序，多了一个textview的标签，位置position需要-1
            appInfo = UserAppInfos.get(position - 1);
        } else {
            //系统程序
            int location = position - UserAppInfos.size() - 2;
            appInfo = SystemAppInfos.get(location);
        }
        return appInfo;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) { //如果position为0，则为TextView
            TextView tv = getTextView();
            tv.setText("用户程序：" + UserAppInfos.size() + "个");  //用户程序个数信息
            return tv;
        } else if (position == (UserAppInfos.size() + 1)) {
            TextView tv = getTextView();
            tv.setText("系统程序：" + SystemAppInfos.size() + "个");//系统程序个数信息
            return tv;
        }
        AppInfo appInfo;
        if (position < (UserAppInfos.size() + 1)) {
            appInfo = UserAppInfos.get(position - 1);//获取用户程序信息
        } else {
            //获取系统程序信息
            appInfo = SystemAppInfos.get(position - UserAppInfos.size() - 2);
        }
        ViewHolder viewHolder = null;
        if (convertView != null & convertView instanceof LinearLayout) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_appmanager_list,null);
            viewHolder.mAppIconImgv = (ImageView) convertView.findViewById(R.
                    id.iv_icon);
            viewHolder.mAppLocationTV = (TextView) convertView.findViewById(R.
                    id.tv_appisroom);
            viewHolder.mAppSizeTV = (TextView) convertView.findViewById(R.
                    id.tv_size);
            viewHolder.mAppNameTV = (TextView) convertView.findViewById(R.
                    id.tv_name);
            viewHolder.mLuanchAppTV = (TextView) convertView.findViewById(R.
                    id.tv_launch);
            viewHolder.mShareAppTV = (TextView) convertView.findViewById(R.
                    id.tv_share);
            viewHolder.mUninstallTV = (TextView) convertView.findViewById(R.
                    id.tv_uninstall);
            viewHolder.mAppOptionLL = (LinearLayout) convertView.findViewById(R.
                    id.ll_option_app);
            convertView.setTag(viewHolder);
        }
        if (appInfo != null) {
            //设置应用存放在手机中的位置信息
            viewHolder.mAppLocationTV.setText(appInfo.getAppLocation(
                    appInfo.isInRoom));
            viewHolder.mAppIconImgv.setImageDrawable(appInfo.icon);//设置应用图标
            //设置应用大小
            viewHolder.mAppSizeTV.setText(Formatter.formatFileSize(context,
                    appInfo.appSize));
            viewHolder.mAppNameTV.setText(appInfo.appName);//设置应用名称
            if (appInfo.isSelected) { //如果条目被选中
                viewHolder.mAppOptionLL.setVisibility(View.VISIBLE);//显示操作应用的布局
            } else {
                viewHolder.mAppOptionLL.setVisibility(View.GONE); //隐藏操作应用的布局
            }
        }
        //分别设置“启动”按钮、“分享”按钮、“卸载”按钮的点击监听事件
        MyClickListener listener = new MyClickListener(appInfo);
        viewHolder.mLuanchAppTV.setOnClickListener(listener);
        viewHolder.mShareAppTV.setOnClickListener(listener);
        viewHolder.mUninstallTV.setOnClickListener(listener);
        return convertView;
    }


    /**
     * dip转换为像素px
     */
    public static int dip2px(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) dpValue;
    }
    /**
     * 创建一个TextView
     */
    private TextView getTextView() {
        TextView tv = new TextView(context); //创建一个TextView对象
        //设置TextView控件的背景颜色为灰色
        tv.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        //设置TextView控件的内边距
        tv.setPadding(dip2px(context, 5), dip2px(context, 5), dip2px(context, 5),
                dip2px(context, 5));
        //设置TextView控件的文本颜色为黑色
        tv.setTextColor(context.getResources().getColor(R.color.black));
        return tv;
    }
    static class ViewHolder {
        TextView mLuanchAppTV;     //启动App
        TextView mUninstallTV;     //卸载App
        TextView mShareAppTV;      //分享app
        ImageView mAppIconImgv;    //app图标
        TextView mAppLocationTV;   //app位置
        TextView mAppSizeTV;       //app大小
        TextView mAppNameTV;       //app名称
        LinearLayout mAppOptionLL; //操作App的线性布局
    }
    class MyClickListener implements View.OnClickListener {
        private AppInfo appInfo;
    public MyClickListener(AppInfo appInfo) {
            super();
            this.appInfo = appInfo;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_launch:
                    // 启动应用
                    EngineUtils.startApplication(context, appInfo);
                    break;
                case R.id.tv_share:
                    // 分享应用
                    EngineUtils.shareApplication(context, appInfo);
                    break;
                case R.id.tv_uninstall:
                    // 卸载应用,需要注册广播接收者
                    if (appInfo.packageName.equals(context.getPackageName())) {
                        Toast.makeText(context, "您没有权限卸载此应用！",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    EngineUtils.uninstallApplication(context, appInfo);
                    break;
            }
        }
    }

}

