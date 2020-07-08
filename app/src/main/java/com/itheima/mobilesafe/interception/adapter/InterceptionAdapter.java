package com.itheima.mobilesafe.interception.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interception.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.interception.entity.BlackContactInfo;

import java.util.List;

public class InterceptionAdapter extends BaseAdapter {
    private List<BlackContactInfo> contactInfos;
    private Context context;
    private int sign = -1;

    public InterceptionAdapter(List<BlackContactInfo> contacts,Context context,int sign){
        super();
        this.contactInfos = contacts;
        this.context = context;
        this.sign = sign;
    }
    @Override
    public int getCount() {
        return contactInfos.size();
    }
    @Override
    public Object getItem(int position) {
        return contactInfos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final BlackContactInfo info = contactInfos.get(position);
        if (convertView == null) {
            convertView = View.inflate(context,R.layout.item_harassmentlist, null);
            holder = new ViewHolder();
            holder.mPhoneName = convertView.findViewById(R.id.phone_name);//姓名
            holder.mPhoneNum = convertView.findViewById(R.id.phone_num); //电话号码
            holder.mTimes = convertView.findViewById(R.id.times);         //拦截次数
            holder.delete = convertView.findViewById(R.id.delete);
            if (sign == 2){//黑名单界面
                holder.mTimes.setVisibility(View.GONE);          //拦截次数
                holder.delete.setVisibility(View.VISIBLE);      //删除按钮
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BlackNumberDao dao = new BlackNumberDao(context);
                        boolean flag = dao.detele(info);
                        if (flag){
                            Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                            contactInfos =  dao.getBlackList();
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if (sign == 3){  //选择联系人界面
                holder.mTimes.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String contactName = info.getContactName();
        String phoneNum = info.getPhoneNumber();
        String place = info.getPlace();
        int times = info.getTimes();
        if(contactName == null || contactName.isEmpty()){
            //如果没有查询到姓名，则将电话号码写在此处
            holder.mPhoneName.setText(phoneNum); //在显示姓名的地方写上电话号码
            if (place != null){
                holder.mPhoneNum.setText(place);
            }else{
                holder.mPhoneNum.setText("未知");
            }
        }else{
            holder.mPhoneName.setText(contactName);
            if (place != null){
                holder.mPhoneNum.setText(phoneNum+"("+place+")");
            }else{
                holder.mPhoneNum.setText(phoneNum);
            }
        }
        holder.mTimes.setText("已拦截"+times+"次");
        return convertView;
    }
    class ViewHolder {
        TextView mPhoneName;
        TextView mPhoneNum;
        TextView mTimes;
        ImageView delete;
    }
}
