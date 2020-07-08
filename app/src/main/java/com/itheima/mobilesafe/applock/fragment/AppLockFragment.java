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

public class AppLockFragment extends Fragment {
    private ListView mLockLV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_applock, null);
        mLockLV = view.findViewById(R.id.lv_lock);
        return view;
    }

    List<ApplicationInfo> unlockApps = new ArrayList<ApplicationInfo>();
    private AppLockDao dao;
    private List<ApplicationInfo> appInfos;
    List<ApplicationInfo> mLockApps = new ArrayList<ApplicationInfo>();
    private AppLockAdapter adapter;
    private Uri uri = Uri.parse("content://com.itheima.mobilesafe.applock");
    @Override
    public void onResume(){
        dao = new AppLockDao(getActivity());
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
    private void fillData() {
        mLockApps.clear();
        for (ApplicationInfo appInfo : appInfos) {
            if(dao.find(appInfo.packageName)){
                //已加锁
                appInfo.isLock = true;
                mLockApps.add(appInfo);
            }
        }
        if(adapter == null){
            adapter = new AppLockAdapter(mLockApps, getActivity());
            mLockLV.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private void initListener() {
        mLockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //播放一个动画效果
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                        0, Animation.RELATIVE_TO_SELF, -1.0f,
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
                                //删除数据库的包名
                                dao.delete(mLockApps.get(position).packageName);
                                //更新界面
                                mLockApps.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    };
                }.start();
            }
        });
    }


}
