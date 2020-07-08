package com.itheima.mobilesafe.netspeed.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.mobilesafe.R;

import java.text.DecimalFormat;

public class DashboardView extends View {
    private int bWidth = 3;       //线宽
    private int width;
    private int height;
    private int radius;
    private int smallline = 20;    //小刻度长
    private String[] outSpeed = {"0","256k","512k","1M","5M","10M","20M"};  //显示的速度值
    private int degree = 45; //偏移度数
    private int lineleng = 40;  //长刻度

    public DashboardView(Context context) {
        super(context);
    }
    public DashboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth()-bWidth*2;
        height = getHeight()-bWidth*2;
        radius = width/2;
        canvas.translate(radius + bWidth,radius + bWidth); //偏移中心点

        //绘制大圆弧
        drawBArc(canvas, radius);
        //绘制小圆弧
        drawBArc(canvas, radius - smallline);
        //绘制圆盘刻度值
        drawFont(canvas, radius);
        //绘制中心圆点
        drawcenterBPaint(canvas);
        //绘制指针
        drawPoint(canvas, radius);
        //绘制改变的弧度
        drawChangArc(canvas,radius);
        //绘制长刻度
        drawScaleLeng(canvas, radius);
        //绘制小刻度
        drawScale(canvas, radius);
    }

    private int blue_color;

    //绘制大圆弧
    private void drawBArc(Canvas canvas, int radius) {
        blue_color = getResources().getColor(R.color.blue_color);
        Paint bPaint = new Paint();
        bPaint.setColor(blue_color);
        bPaint.setAntiAlias(true);//消除锯齿
        bPaint.setStrokeWidth(bWidth);
        bPaint.setStyle(Paint.Style.STROKE);//设置空心
        RectF arcRectF = new RectF(-radius,-radius,radius,radius);
        canvas.drawArc(arcRectF,135,270,false,bPaint);  //画圆弧
        canvas.save();
        canvas.restore();
    }



    private void drawFont(Canvas canvas, int radius) {
        //数字字体
        Paint outSpeedPaint = new Paint();
        outSpeedPaint.setColor(Color.parseColor("#FFBABABA"));
        outSpeedPaint.setStrokeWidth(0);
        outSpeedPaint.setTypeface(Typeface.DEFAULT_BOLD);
        outSpeedPaint.setTextSize(20);
        //计算字体的宽度
        float width0 = outSpeedPaint.measureText(outSpeed[0]);
        float width1 = outSpeedPaint.measureText(outSpeed[1]);
        float width2 = outSpeedPaint.measureText(outSpeed[2]);
        float width3 = outSpeedPaint.measureText(outSpeed[3]);
        float width4 = outSpeedPaint.measureText(outSpeed[4]);
        float width5 = outSpeedPaint.measureText(outSpeed[5]);
        float width6 = outSpeedPaint.measureText(outSpeed[6]);
        //计算字体显示位置的半径
        radius = radius - lineleng * 2;
        //绘制字体
        canvas.drawText(outSpeed[0],-(int)(radius *Math.sin(degree)) + width0/2,
                (int)(radius*Math.sin(degree))- width0/2,outSpeedPaint);
        canvas.drawText(outSpeed[1],-radius -  width1/2 ,0,outSpeedPaint);
        canvas.drawText(outSpeed[2],-(int)(radius*Math.sin(degree))+width2/2,
                -(int)(radius*Math.sin(degree))+width2/2,outSpeedPaint);
        canvas.drawText(outSpeed[3],-width3/2,-radius+width3/2,outSpeedPaint);
        canvas.drawText(outSpeed[4],(int)(radius*Math.sin(degree))-width4,
                -(int)(radius*Math.sin(degree))+ width4,outSpeedPaint);
        canvas.drawText(outSpeed[5],radius-width5,0,outSpeedPaint);
        canvas.drawText(outSpeed[6],(int)(radius*Math.sin(degree))-width6,
                (int)(radius*Math.sin(degree))-width6,outSpeedPaint);
        //绘制显示圆环中的速度
        outSpeedPaint.setColor(blue_color);
        int centerSize = 80;
        outSpeedPaint.setTextSize(centerSize);
        float textWidth = outSpeedPaint.measureText(centerSpeed);   //中间字体的宽度
        canvas.drawText(centerSpeed,-textWidth/2,radius,outSpeedPaint);
        //绘制速度单位
        outSpeedPaint.setTextSize(30);
        float speedTextWidth = outSpeedPaint.measureText(speedFont);
        canvas.drawText(speedFont,-speedTextWidth/2,radius+lineleng+centerSize/2,
                outSpeedPaint);
    }

    //绘制中心圆点
    private void drawcenterBPaint(Canvas canvas) {
        Paint centerBPaint = new Paint();
        centerBPaint.setColor(blue_color);
        centerBPaint.setAntiAlias(true);           //消除锯齿
        centerBPaint.setStrokeWidth(30);
        centerBPaint.setStyle(Paint.Style.FILL);  //设置实心
        RectF arcRectF = new RectF(-30,-30,30,30);
        canvas.drawArc(arcRectF,0,360,false,centerBPaint);
        canvas.save();
    }
    private int progress = 0;   //当前进度

    //绘制指针
    private void drawPoint(Canvas canvas, int radius) {
        Paint arrowsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowsPaint.setColor(blue_color);
        arrowsPaint.setStyle(Paint.Style.FILL_AND_STROKE);      //填充内部和描边
        arrowsPaint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(10, -10);// 此点为多边形的起点
        path.lineTo(10,10);
        path.lineTo(-radius + smallline*4,2);
        path.lineTo(-radius + smallline*4,-2);
        path.close();
        canvas.save(); // 保存canvas状态
        canvas.rotate(progress-45);
        canvas.drawPath(path,arrowsPaint);
        canvas.restore();
    }


    //绘制改变的弧度
    private void drawChangArc(Canvas canvas, int radius) {
        //改变圆环
        Paint changePain = new Paint();
        changePain.setColor(blue_color);
        changePain.setAntiAlias(true);//消除锯齿
        changePain.setStrokeWidth(smallline-bWidth);
        changePain.setStyle(Paint.Style.STROKE);  //设置空心
        RectF arcRectF = new RectF(-radius+smallline/2+bWidth/4,
                -radius+smallline/2+bWidth/4,
                radius-smallline/2-bWidth/4,
                radius-smallline/2-bWidth/4);
        canvas.drawArc(arcRectF,135,progress,false, changePain);     //画圆弧
    }

    private void drawScaleLeng(Canvas canvas, int radius) {
        //刻度长线
        Paint linePaint = new Paint();
        linePaint.setColor(blue_color);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(bWidth);
        linePaint.setAntiAlias(true);
        canvas.save();      // 保存canvas状态
        canvas.rotate(135);//从左下角开始 偏移以右边开始旋转135度
        //7个长刻度
        for (int i = 0; i < 7; i++){
            canvas.drawLine(radius,0,radius-lineleng,0,linePaint);
            canvas.rotate(degree);
        }
        canvas.save();      // 保存canvas状态
        canvas.restore();
    }
    private int sdegree = 9; //小刻度偏移度数

    private void drawScale(Canvas canvas, int radius) {
        //刻度短线
        Paint linesPaint = new Paint();
        linesPaint.setColor(blue_color);
        linesPaint.setStyle(Paint.Style.STROKE);
        linesPaint.setStrokeWidth(bWidth);
        linesPaint.setAntiAlias(true);
        canvas.restore();
        canvas.save(); // 保存canvas状态
        canvas.rotate(135);//从左下角开始 偏移以右边开始旋转135度
        //30个刻度
        for (int i = 0; i < 30; i++){
            canvas.drawLine(radius,0,radius-smallline,0,linesPaint);
            canvas.rotate(sdegree);
        }
        canvas.save(); // 保存canvas状态
        canvas.restore();
    }
    private long internetSpeed = 0;                       // 当前网络速度，单位为kb/s
    private String centerSpeed = "0";                    //显示的速度
    private String speedFont = "m/s";                    //速度单位，kb/s m/s
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setInternetSpeed(long internetSpeed) {
        this.internetSpeed = internetSpeed;
        calcSpeedNumber(internetSpeed);          //换算成字符串
        changearc();
        draw();
    }
    //换算速度为字符串
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void calcSpeedNumber(long internetSpeed) {
        double templ = 0;
        long speed = internetSpeed;
        DecimalFormat df = new DecimalFormat("#.#");
        if (speed >= 1048576) {
            templ = (double) internetSpeed / (double)1048576;
            centerSpeed = "" + df.format(templ);
            speedFont = "m/s";
        } else {
            templ = (double) internetSpeed / (double)1024;
            centerSpeed = "" + df.format(templ);
            speedFont = "kb/s";
        }
    }
    private int blackArc = 0;                                  //改变的度数

    private void changearc() {
        int speedDegree = (int)internetSpeed/1024;           //将速度的单位换算成kb
        if (speedDegree >= 0 && speedDegree < 1024){
            if (speedDegree == 0){
                blackArc = 0;
            }else if (speedDegree >0 && speedDegree < 256){
                blackArc = speedDegree * 45 / 256;
            }else if(speedDegree == 256){
                blackArc = 45;
            }else if(speedDegree > 256 && speedDegree <512){
                blackArc = (speedDegree-256)*45/256 + 45;
            }else if (speedDegree == 512){
                blackArc = 90;
            }else if(speedDegree > 512 && speedDegree <1024){
                blackArc = (speedDegree-512)*45/512+90;
            }
        }else {
            if(speedDegree == 1024){
                blackArc = 135;
            }else if(speedDegree > 1024 && speedDegree < 5120){
                blackArc = (speedDegree-1024)*45/4096+135;
            }else if(speedDegree == 5120){
                blackArc = 180;
            }else if(speedDegree > 5120 && speedDegree < 10240){
                blackArc = (speedDegree-5120)*45/5120+180;
            }else if(speedDegree == 10240){
                blackArc = 225;
            }else if (speedDegree > 10240 && speedDegree < 20480){
                blackArc = (speedDegree-10240)*45/10240+225;
            }else if(speedDegree == 20480){
                blackArc = 270;
            }
        }
    }

    private void draw() {
        for (int i = 0; i <= blackArc; i++){
            progress = i;
            postInvalidate();//重新绘制
        }
    }



}
