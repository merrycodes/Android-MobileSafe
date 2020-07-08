package com.itheima.mobilesafe.clean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.adapter.RubbishListAdapter;
import com.itheima.mobilesafe.clean.entity.RubbishInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法运行顺序
 * {@link CleanRubbishListActivity#onCreate(Bundle)} -> {@link CleanRubbishListActivity#onRequestPermissionsResult}（有权限开始扫描）
 */
public class CleanRubbishListActivity extends Activity implements View.OnClickListener {

    private TextView tv_sacnning, tv_scanned;
    private long rubbishMemory = 0; //可清理的垃圾
    private List<RubbishInfo> rubbishInfos = new ArrayList<>();
    private List<RubbishInfo> mRubbishInfos = new ArrayList<>();
    private PackageManager pm;
    private RubbishListAdapter adapter;
    private ListView mRubbishLV;
    private Button mRubbishBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_rubbish_list);//加载布局文件
        pm = getPackageManager();
        initView();//初始化界面控件
    }

    /**
     * 初始化控件
     **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        // 权限请求
        ActivityCompat.requestPermissions(CleanRubbishListActivity.this,
                new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_main_title)).setText("扫描垃圾");
        tv_sacnning = findViewById(R.id.tv_scanning);
        mRubbishLV = findViewById(R.id.lv_rubbish);
        mRubbishBtn = findViewById(R.id.btn_cleanall);
        tv_scanned = findViewById(R.id.tv_scanned);
        adapter = new RubbishListAdapter(this, mRubbishInfos);
        mRubbishLV.setAdapter(adapter);
        tv_back.setOnClickListener(this);
        mRubbishBtn.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    RubbishInfo info = (RubbishInfo) msg.obj;
                    if (info.packagename != null)
                        tv_sacnning.setText("正在扫描： " + info.packagename);
                    tv_scanned.setText(RubbishListAdapter.FormatFileSize(rubbishMemory));
                    mRubbishInfos.clear(); //清理集合mRubbishInfos中的数据
                    //将垃圾信息添加到集合rubbishInfos中
                    mRubbishInfos.addAll(rubbishInfos);
                    adapter.notifyDataSetChanged();//刷新ListView列表
                    //将列表滚动到扫描的位置
                    mRubbishLV.setSelection(mRubbishInfos.size());
                    break;
                case FINISH:
                    tv_sacnning.setText("扫描完成！");
                    if (rubbishMemory > 0) {
                        mRubbishBtn.setEnabled(true);
                    } else {
                        mRubbishBtn.setEnabled(false);
                        Toast.makeText(CleanRubbishListActivity.this, "您的手机洁净如新",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 遍历外置SD卡中files文件夹中的所有文件
     */
    public long filePath(File file) {
        long memory = 0;
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();//获取file目录下所有文件和目录的绝对路径
            for (File file2 : files) {
                if (file2.getPath().contains("/files")) {//判断扫描的路径是否包含files文件夹
                    if (file2.listFiles() == null) {
                        memory += file2.length();//获取files文件夹中的文件大小
                    } else {
                        try {
                            memory += getFolderSize(file2);//遍历files文件夹中子文件夹中的文件
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    memory += filePath(file2); //如果没有扫描到files文件夹，则继续进行遍历
                }
            }
        }
        return memory;
    }

    /**
     * 遍历文件夹中的子文件夹
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();//获取file目录下所有文件和目录的绝对路径
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {//判断fileList[i]是否是文件夹
                    //获取子文件夹中的所有文件大小并添加到变量size中
                    size = size + getFolderSize(fileList[i]);
                } else {
                    //将文件fileList[i]的大小添加到size中
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size; //返回file目录下所有文件的大小
    }

    protected static final int SCANNING = 100;
    protected static final int FINISH = 101;

    private void fillData() {
        Thread thread = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                rubbishInfos.clear();
                String filesPath = "/sdcard/Android/data"; //外置SD卡中的data文件夹路径
                File ppFile = new File(filesPath);
                File[] files = ppFile.listFiles();
                if (files == null) return;
                PackageManager packageManager = getPackageManager();
                for (File file : files) { //遍历手机中所有软件的包信息
                    RubbishInfo rubbishInfo = new RubbishInfo();
                    try {
                        if (file.getName() == null) return;
                        PackageInfo packageInfo = packageManager.getPackageInfo(file.getName(), 0);
                        rubbishInfo.packagename = packageInfo.packageName;
                        rubbishInfo.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                        rubbishInfo.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                        // filePath 方法就是计算大小，递归
                        rubbishInfo.rubbishSize = filePath(file);
                        if (rubbishInfo.rubbishSize > 0 &&
                                rubbishInfo.packagename != null) {
                            rubbishInfos.add(rubbishInfo);
                            rubbishMemory += rubbishInfo.rubbishSize;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = rubbishInfo;
                    msg.what = SCANNING;
                    handler.sendMessage(msg);//将正在扫描的垃圾信息传递到主线程中
                }
                Message msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);//将扫描完成的信息传递到主线程中
            }
        };
        thread.start();//开启线程
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:       //返回键点击事件
                finish();
                break;
            case R.id.btn_cleanall: //“一键清理”按钮点击事件
                if (rubbishMemory > 0) {
                    //跳转到清理垃圾界面
                    // 这里后面添加的，应该是这样子写的
                    Intent intent = new Intent(this, CleanRubbishActivity.class);
                    intent.putExtra("rubbishSize", rubbishMemory);
                    intent.putExtra("rubbishInfos", ((Serializable) mRubbishInfos));
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.WRITE_EXTERNAL_STORAGE")
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限成功
                    fillData();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
