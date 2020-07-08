package com.itheima.mobilesafe.clean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.clean.entity.RubbishInfo;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CleanRubbishActivity extends Activity implements View.
        OnClickListener {
    protected static final int CLEANNING = 100;
    protected static final int CLEANNING_FAIL = 101;
    private AnimationDrawable animation;
    private long rubbishSize;
    private TextView tv_rubbish_size, tv_rubbish_unit, tv_clean_size;
    private FrameLayout fl_cleaning, fl_finish_clean;
    private List<RubbishInfo> mRubbishInfos = new ArrayList<RubbishInfo>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLEANNING:
                    long size = (Long) msg.obj;
                    formatSize(size); //格式化垃圾的大小
                    if (size == rubbishSize) {
                        animation.stop(); //停止动画
                        fl_cleaning.setVisibility(View.GONE);        //隐藏正在清理的帧布局
                        fl_finish_clean.setVisibility(View.VISIBLE);//显示完成清理的帧布局
                        tv_clean_size.setText("成功清理：" + Formatter.formatFileSize(CleanRubbishActivity.this, rubbishSize));
                    }
                    break;
                case CLEANNING_FAIL:
                    animation.stop();
                    Toast.makeText(CleanRubbishActivity.this, "清理垃圾失败",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_rubbish);
        initView();
        Intent intent = getIntent();
        rubbishSize = intent.getLongExtra("rubbishSize", 0); //获取传递过来的垃圾大小
        //获取传递过来的带有垃圾信息的软件集合数据
        mRubbishInfos = (List<RubbishInfo>) intent.getSerializableExtra("rubbishInfos");
        initData();
    }

    /**
     * 初始化控件
     **/
    private void initView() {
        ((TextView) findViewById(R.id.tv_main_title)).setText("清理垃圾");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        animation = (AnimationDrawable) findViewById(R.id.iv_clean_rubbish).getBackground();
        animation.setOneShot(false); //动画是否只运行一次
        animation.start();             //开始动画
        tv_rubbish_size = findViewById(R.id.tv_rubbish_size);
        tv_rubbish_unit = findViewById(R.id.tv_rubbish_unit);
        fl_cleaning = findViewById(R.id.fl_cleaning);
        fl_finish_clean = findViewById(R.id.fl_finish_clean);
        tv_clean_size = findViewById(R.id.tv_clean_size);
        tv_back.setOnClickListener(this);
        findViewById(R.id.btn_finish).setOnClickListener(this);

    }

    private void formatSize(long size) {
        String rubbishSizeStr = Formatter.formatFileSize(this, size);
        String sizeStr;
        String sizeUnit;
        //根据大小判定单位
        if (size > 900) {
            //大于900则单位两位
            sizeStr = rubbishSizeStr.substring(0, rubbishSizeStr.length() - 2);
            sizeUnit = rubbishSizeStr.substring(rubbishSizeStr.length() - 2,
                    rubbishSizeStr.length());
        } else {
            //单位是一位
            sizeStr = rubbishSizeStr.substring(0, rubbishSizeStr.length() - 1);
            sizeUnit = rubbishSizeStr.substring(rubbishSizeStr.length() - 1,
                    rubbishSizeStr.length());
        }
        tv_rubbish_size.setText(sizeStr);
        tv_rubbish_unit.setText(sizeUnit);
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void initData() {
        new Thread() {
            public void run() {
                long size = 0;
                File filePath = getExternalCacheDir().getParentFile().getParentFile();
                for (RubbishInfo info : mRubbishInfos) {
                    String filesPath = filePath + "/" + info.packagename + "/files";
                    File file = new File(filesPath);
                    boolean success = deleteDir(file); //删除files文件夹下的文件
                    if (success) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        size += info.rubbishSize;
                        if (size > rubbishSize) {
                            size = rubbishSize;
                        }
                        Message message = Message.obtain();
                        message.what = CLEANNING;
                        message.obj = size;
                        mHandler.sendMessageDelayed(message, 200);
                    } else {
                        Message message = Message.obtain();
                        message.what = CLEANNING_FAIL;
                        mHandler.sendMessageDelayed(message, 200);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:     //返回键点击事件
                finish();            //关闭当前界面
                break;
            case R.id.btn_finish: //“完成”按钮点击事件
                finish();            //关闭当前界面
                break;
        }
    }

}
