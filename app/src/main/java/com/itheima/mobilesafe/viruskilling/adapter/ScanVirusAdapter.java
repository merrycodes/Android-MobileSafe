package com.itheima.mobilesafe.viruskilling.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.viruskilling.entity.ScanAppInfo;

import java.util.List;

public class ScanVirusAdapter extends BaseAdapter {
    private List<ScanAppInfo> mScanAppInfos;
    private Context context;
    public ScanVirusAdapter(List<ScanAppInfo> scanAppInfo, Context context) {
        super();
        mScanAppInfos = scanAppInfo;
        this.context = context;
    }
    @Override
    public int getCount() {
        return mScanAppInfos.size();
    }
    @Override
    public Object getItem(int position) {
        return mScanAppInfos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_virus_killing,
                    null);
            holder = new ViewHolder();
            holder.mAppIconImgv = convertView.findViewById(R.id.iv_app_icon);
            holder.mAppNameTV = convertView.findViewById(R.id.tv_app_name);
            holder.mScanIconImgv = convertView.findViewById(R.id.iv_right_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanAppInfo scanAppInfo = mScanAppInfos.get(position);
        if (!scanAppInfo.isVirus) {
            holder.mScanIconImgv.setBackgroundResource(R.drawable.blue_right_icon);
            holder.mAppNameTV.setTextColor(context.getResources().getColor(R.color.black));
            holder.mAppNameTV.setText(scanAppInfo.appName);
        } else {
            holder.mAppNameTV.setTextColor(context.getResources().getColor(R.color.bright_red));
            holder.mAppNameTV.setText(scanAppInfo.appName + "(" +
                    scanAppInfo.description + ")");
        }
        holder.mAppIconImgv.setImageDrawable(scanAppInfo.appicon);
        return convertView;
    }
    static class ViewHolder {
        ImageView mAppIconImgv;
        TextView mAppNameTV;
        ImageView mScanIconImgv;
    }
}
