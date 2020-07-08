package com.itheima.mobilesafe.netspeed.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.netspeed.view.CommonDialog;
import com.itheima.mobilesafe.netspeed.view.DashboardView;
import com.itheima.mobilesafe.netspeed.utils.NetWorkSpeedUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class NetDetectionActivity extends Activity implements View.OnClickListener{
    public TextView tv_txspeed,tv_rxspeed;
    private DashboardView dv_speed;

    private NetWorkSpeedUtil netWorkSpeedUtil;
    private float txspeed =0.0f;                       //上传速度
    private float rxspeed =0.0f;                       //下载速度
    private static final int MSG_RX_SPEED = 100;        //下载速度信息
    private static final int MSG_TX_SPEED = 101;       //上传速度信息
    private static final int MSG_FAIL = 10001;        //连接失败
    private static final int MSG_RXOK = 10002;       //下载成功
    private static final int MSG_TXOK = 10003;
    private Handler mHandler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RX_SPEED :                        //更新网络下载速度
                    if (msg.obj != null){
                        float value = Float.valueOf(msg.obj.toString().trim());
                        dv_speed.setInternetSpeed((int) value *1024);
                        if (rxspeed < value){
                            rxspeed = value;
                        }
                        tv_rxspeed.setText(getFormetSpeed(rxspeed));
                    }
                    break;
                case MSG_TX_SPEED :                       //更新网络上传速度
                    if (msg.obj != null){
                        float txValue = Float.valueOf(msg.obj.toString().trim());
                        dv_speed.setInternetSpeed((int) txValue *1024);
                        if (txspeed < txValue){
                            txspeed = txValue;
                        }
                        tv_txspeed.setText(getFormetSpeed(txValue));
                    }
                    break;
                case MSG_RXOK:
                    tv_rxspeed.setText(getFormetSpeed(rxspeed));
                    netWorkSpeedUtil.startShowTxSpeed();
                    fileupload();
                    break;
                case MSG_FAIL:
                    Toast.makeText(NetDetectionActivity.this,
                            "请检测网络",Toast.LENGTH_SHORT).show();
                    break;
                case MSG_TXOK:
                    //上传速度测试结束
                    tv_txspeed.setText(getFormetSpeed(txspeed));
                    //跳转到测试结果界面
                    break;

            }
            super.handleMessage(msg);
        }
    };
    public void checkNetSpeed() {
        //在下载过程中测试下载的速度
        netWorkSpeedUtil = new NetWorkSpeedUtil(this,mHandler);
        netWorkSpeedUtil.startShowRxSpeed();
        downloadFile();//下载文件
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_detection);
        initView();
        checkNetworkAvalible();
    }
    private void initView() {
        RelativeLayout title_bar = findViewById(R.id.title_bar);
        title_bar.setBackgroundColor(getResources().getColor(R.color.blue_color));
        TextView title = findViewById(R.id.tv_main_title);
        title.setText("网速测试");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        tv_txspeed = findViewById(R.id.tx_speed);        //上传速度文本
        tv_rxspeed = findViewById(R.id.rx_speed);        //下载速度文本
        dv_speed = findViewById(R.id.dv_speed);          //圆盘速度
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void createDialog(String message) {
        final CommonDialog dialog = new CommonDialog(NetDetectionActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage(message);
        dialog.setNegtive("取消");
        dialog.setPositive("确定");
        dialog.setOnClickBottomListener(new CommonDialog.
                OnClickBottomListener() {
            @Override
            public void onPositiveClick() { //确定按钮的点击事件
                dialog.dismiss();
                //测试网络速度
                checkNetSpeed();
            }
            @Override
            public void onNegtiveClick() { //取消按钮的点击事件
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void checkNetworkAvalible() {
        if (isNetworkAvalible()){//判断是否有可用的网络
            if (isWifi()){          //判断当前网络是否为Wi-Fi
                //检测网络速度
                checkNetSpeed();
            }else{
                //弹出对话框、询问用户是否使用流量检测网络速度
                String message = "网速测试需要使用流量，是否继续测试";
                createDialog(message);
            }
        }else{
            //提示用户请打开网络连接的弹框
            String message = "连接不到网络，请打开网络继续测试";
            Toast.makeText(NetDetectionActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isNetworkAvalible() {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager)this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();
            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private void downloadFile(){
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url = "http://172.16.43.110:8080/filemanage/download?filename=" +
                "python-3.5.2-amd64.exe";
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()            //默认就是GET请求，可以不写
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                netWorkSpeedUtil.cancelRxSpeed();//停止测试下载网速定时器
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_FAIL;
                mHandler.sendMessage(msg);//更新界面
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath= Environment.getExternalStorageDirectory().
                            getAbsolutePath();
                    File dest = new File(mSDCardPath,url.substring(url.lastIndexOf("=") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_RXOK;
                    mHandler.sendMessage(msg);         //更新界面
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_FAIL;
                    mHandler.sendMessage(msg);         //更新界面
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }
                    netWorkSpeedUtil.cancelRxSpeed();
                }
            }
        });
    }

    public  String getFormetSpeed(float flow) {
        String strSpeed = "";
        if (flow < 1024) {
            strSpeed = String.format("%.2f", flow)+"K/s";
        } else if (flow < 1048576) {
            strSpeed = String.format("%.2f", flow*1.0 / 1024)+"M/s";
        } else if (flow > 1073741824){
            strSpeed = String.format("%.2f", flow*1.0 / 1048576)+ "G/s";
        }
        return strSpeed;
    }
    //上传的限制为30M以内的文件
    public void fileupload() {
        // 获得下载的文件路径
        File file = new File(Environment.getExternalStorageDirectory(),
                "python-3.5.2-amd64.exe");
        OkHttpClient client = new OkHttpClient();
        // 上传文件使用MultipartBody.Builder
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                // 提交文件，第1个参数是键（name="fileUpload"），第2个参数是文件名，第3个参数是请求体
                .addFormDataPart("fileUpload", file.getName(),
                        RequestBody.create(null, file))
                .build();
        // POST请求
        Request request = new Request.Builder()
                .url("http://3s.dkys.org:28698/filemanage/uploadFile")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(NetDetectionActivity.this,"测试失败，请重新测试",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_TXOK;
                mHandler.sendMessage(msg);        //将上传成功的信息传递到主线程中
                netWorkSpeedUtil.cancelTxSpeed();//停止测试上传网速的定时器
            }
        });
    }

}

