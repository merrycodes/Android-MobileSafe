package com.itheima.mobilesafe.clean.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.entity.RubbishInfo;

import java.text.DecimalFormat;
import java.util.List;

public class RubbishListAdapter extends BaseAdapter {
    private Context context;
    private List<RubbishInfo> rubbishInfos;

    public RubbishListAdapter(Context context, List<RubbishInfo> rubbishInfos) {
        super();
        this.context = context;
        this.rubbishInfos = rubbishInfos;//接收传递过来的列表数据
    }

    @Override
    public int getCount() {
        return rubbishInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return rubbishInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_list_rubbish_clean,
                    null);
            holder.mAppIconImgv = convertView.findViewById(R.id.
                    iv_appicon_rubbishclean);
            holder.mAppNameTV = convertView.findViewById(R.id.
                    tv_appname_rubbishclean);
            holder.mRubbishSizeTV = convertView.findViewById(R.id.
                    tv_appsize_rubbishclean);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RubbishInfo rubbishInfo = rubbishInfos.get(position); //获取每个条目的数据
        holder.mAppIconImgv.setImageDrawable(rubbishInfo.appIcon);//设置软件图标
        holder.mAppNameTV.setText(rubbishInfo.appName);            //设置软件名称
        //设置软件垃圾大小，此处使用的FormatFileSize()方法在后续创建
        holder.mRubbishSizeTV.setText(FormatFileSize(rubbishInfo.rubbishSize));
        return convertView;
    }

    static class ViewHolder {
        ImageView mAppIconImgv;
        TextView mAppNameTV;
        TextView mRubbishSizeTV;
    }

    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "b";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "kb";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
