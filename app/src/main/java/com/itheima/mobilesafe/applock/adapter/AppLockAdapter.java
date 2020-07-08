package com.itheima.mobilesafe.applock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.applock.entity.ApplicationInfo;

import java.util.List;

public class AppLockAdapter extends BaseAdapter {
    private List<ApplicationInfo> appInfos;
    private Context context;
    public AppLockAdapter(List<ApplicationInfo> appInfos, Context context) {
        super();
        this.appInfos = appInfos;
        this.context = context;
    }
    @Override
    public int getCount() {
        return appInfos.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView instanceof RelativeLayout){
            holder = (ViewHolder) convertView.getTag();
        }else{
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_list_virus_killing, null);
            holder.mAppIconImgv = convertView.findViewById(
                    R.id.iv_app_icon);
            holder.mAppNameTV = convertView.findViewById(R.id.tv_app_name);
            holder.mLockIcon = convertView.findViewById(R.id.iv_right_img);
            convertView.setTag(holder);
        }
        final ApplicationInfo appInfo = appInfos.get(position);
        holder.mAppIconImgv.setImageDrawable(appInfo.icon);
        holder.mAppNameTV.setText(appInfo.appName);
        if(appInfo.isLock){
            //表示当前应用已加锁
            holder.mLockIcon.setBackgroundResource(R.drawable.applock_icon);
        }else{
            //当前应用未加锁
            holder.mLockIcon.setBackgroundResource(R.drawable.appunlock_icon);
        }
        return convertView;
    }
    static class ViewHolder{
        TextView mAppNameTV;
        ImageView mAppIconImgv;
        ImageView mLockIcon;
    }
}
