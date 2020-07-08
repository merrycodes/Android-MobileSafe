package com.itheima.mobilesafe.monitor.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.monitor.entity.TrafficInfo;

import java.util.List;

import static com.itheima.mobilesafe.monitor.activity.TrafficMonitorActivity.getFormetFlow;

public class FlowDetilsAdapter extends BaseAdapter {
    private List<TrafficInfo> infos;
    private Context context;
    public FlowDetilsAdapter(List<TrafficInfo> systemContacts,
                             Context context) {
        super();
        this.infos = systemContacts;
        this.context = context;
    }
    @Override
    public int getCount() {
        return infos.size();
    }
    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TrafficInfo info = infos.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_flow, null);
            holder = new ViewHolder();
            holder.tv_date = (TextView) convertView
                    .findViewById(R.id.tv_date);
            holder.tv_mobileNet = (TextView) convertView
                    .findViewById(R.id.tv_mobileNet);
            holder.tv_wifi = convertView.findViewById(R.id.tv_wifi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_date.setText(info.getDate());
        Long mobile = Long.parseLong(info.getMobiletraffic());
        holder.tv_mobileNet.setText(getFormetFlow(mobile));
        Long wifi = Long.parseLong(info.getWifi());
        if (wifi<0){
            wifi = 0l;
        }
        holder.tv_wifi.setText(getFormetFlow(wifi));
        return convertView;
    }
    class ViewHolder {
        TextView tv_date;
        TextView tv_mobileNet;
        TextView tv_wifi;
    }
}
