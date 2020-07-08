package com.itheima.mobilesafe.monitor.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.mobilesafe.R;

public class SportProgressView extends View {
    private Context mContext;
    private Paint mPaint;                //圆形进度条的画笔
    private Paint mTextPaint;            //剩余流量值的画笔
    private int mProgressWidth = 14;    //圆形进度条的宽度
    private int mStepTextSize = 10;     //字体的大小
    private int colorEmpty = getResources().getColor(R.color.dark_gray);//空白进度条的颜色
    private int colorStart = getResources().getColor(R.color.color_start);
    public SportProgressView(Context context) {
        super(context);
    }
    public SportProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }
    //初始化画笔
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setStyle(Paint.Style.STROKE);   //绘制边
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(colorEmpty);
        mTextPaint.setTextSize(mStepTextSize);
    }
    private int mWidth;     //控件的宽度
    private int mHeight;    //控件的高度
    private int mProgressR;//圆形进度条的半径
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY) {   //完全，父元素决定子元素的确切大小
            mWidth = widthSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {  //至多，子元素至多达到指定大小的值
            mWidth = 500;
        }
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = heightSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            mHeight = 500;
        }
        //以给定的高度为限制条件，计算半径
        mProgressR = mHeight - mProgressWidth / 2;
        mWidth = mProgressR * 2 + mProgressWidth + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(mWidth, mHeight);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int left = mProgressWidth / 2 + getPaddingLeft();
        int right = left + 2 * mProgressR;
        int top = mHeight  - mProgressR;
        int bottom = mHeight  + mProgressR;
        RectF rect = new RectF(left, top, right, bottom);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(colorEmpty);
        canvas.drawArc(rect, 180, 180, false, mPaint);

        mPaint.setColor(colorStart);
        canvas.drawArc(rect, 180, mProgress, false, mPaint);
        drawProgressText(canvas, surplus);
    }

    private  Long total = 0l;
    private Long used = 0l;
    private Long surplus = 0l;
    private float mProgress;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFlow(Long total, Long used) {
        this.total = total;                                      //本月总共的套餐流量
        this.used = used/1024;   //本月已经使用的流量
        surplus = total - this.used;                                      //本月剩余的套餐流量
        startAnimation();                                                 //开启圆形进度条的动画
    }
    public void startAnimation() {
        mProgress = 0f;
        if (total== 0){
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0, used * 180 /total);
        animator.setDuration(1600).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }
    //绘制圆形中间现实的剩余套餐流量值的文本
    private void drawProgressText(Canvas canvas, Long surplus) {
        mTextPaint.setColor(colorStart);
        mTextPaint.setTextSize(60);
        String text = surplus+"M";
        float stringWidth = mTextPaint.measureText(text);
        float baseline = mHeight / 2 + stringWidth/2 ;   //绘制文本的基准线
        canvas.drawText(text, mWidth / 2 - stringWidth / 2, baseline, mTextPaint);
    }

}
