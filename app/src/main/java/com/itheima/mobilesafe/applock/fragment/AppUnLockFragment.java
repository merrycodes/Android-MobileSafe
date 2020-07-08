package com.itheima.mobilesafe.applock.fragment;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.applock.adapter.AppLockAdapter;
import com.itheima.mobilesafe.applock.db.dao.AppLockDao;
import com.itheima.mobilesafe.applock.entity.ApplicationInfo;
import com.itheima.mobilesafe.applock.utils.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

public class AppUnLockFragment extends Fragment {
    private ListView mUnLockLV;
    private AppLockAdapter adapter;
    private AppLockDao dao;
    private List<ApplicationInfo> appInfos;                                 //手机中所有应用程序的集合
    List<ApplicationInfo> unlockApps = new ArrayList<ApplicationInfo>();        //手机中未加锁的应用程序集合
    private Uri uri = Uri.parse("content://com.itheima.mobilesafe.applock");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_appunlock, null);
        mUnLockLV = (ListView) view.findViewById(R.id.lv_unlock);
        return view;
    }
    @Override
    public void onResume() {
        appInfos = AppInfoParser.getAppInfos(getActivity());
        fillData();
        initListener();
        super.onResume();
        getActivity().getContentResolver().registerContentObserver(uri, true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        fillData();
                    }
                });

    }
    public void fillData() {
        unlockApps.clear();
        dao = new AppLockDao(getActivity());
        for(ApplicationInfo info : appInfos){
            if(!dao.find(info.packageName)){
                //未加锁
                info.isLock = false;
                unlockApps.add(info);
            }
        }
        if(adapter == null){
            adapter = new AppLockAdapter(unlockApps, getActivity());
            mUnLockLV.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void initListener() {
        mUnLockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                if(unlockApps.get(position).packageName.equals("com.itheima.mobilesafe")){
                    return;
                }
                //给应用加锁
                //播放一个动画效果
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                        0, Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                ta.setDuration(300);
                view.startAnimation(ta);
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //程序锁信息被加入到数据库了
                                dao.insert(unlockApps.get(position).packageName);
                                unlockApps.remove(position);
                                adapter.notifyDataSetChanged();//通知界面更新
                            }
                        });
                    };
                }.start();
            }
        });
    }


}
