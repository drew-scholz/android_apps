package com.scholz.andrew.ascholzlab5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import android.os.Handler;


/**
 * Created by Drew Scholz on 5/2/2017.
 */

public class Clock extends View{
    static final float mAspectRatio = 1;
    static final float width = 200;
    static final float height = 200;
    static final float[] mSecHand = new float[] {0,0,0,98};
    static final float[] mMinHand = new float[]{0,-20,5,0,0,91,-5,0,0,-20};
    static final float[] mHourHand = new float[] {0,-10,5,0,0,65,-5,0,0,-10};
    private Handler mHandler;
    private Runnable mTimer;
    private boolean mIsRunning = false;
    private int TIMER_MSEC = 500;
    private Paint pFill;
    private Paint pStroke;
    private String mTime;
    public TimeObserver time = new TimeObserver();

    public Clock(Context context) {
        super(context);
        init();
    }

    public Clock(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }
    public Clock(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mHandler = new Handler();
        mTimer = new Runnable() {
            @Override
            public void run() {
                onTimer();
                if (mIsRunning)
                    mHandler.postDelayed(this, TIMER_MSEC);
            }
        };

        pFill = new Paint();
        pFill.setColor(Color.BLACK);
        pFill.setStyle(Paint.Style.FILL);
        pStroke = new Paint();
        pStroke.setColor(Color.BLACK);
        pStroke.setStrokeWidth(2);
        pStroke.setStyle(Paint.Style.STROKE);

        mTime = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // clear canvas and center origin
        canvas.drawColor(Color.WHITE);
        canvas.translate(canvas.getWidth()/2, canvas.getHeight()/2);

        // compute scale
        float dimX = (float )0.98 * (canvas.getWidth() / width);
        float dimY = (float) 0.98 * (canvas.getHeight() / height);
        canvas.scale(dimX,-dimY);

        // compute time
        Calendar calendar = new GregorianCalendar();
        int seconds = calendar.get(Calendar.SECOND);
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        mTime = (hours + ":" + minutes + ":" + seconds);

        // compute degrees of rotation for clock hands
        int secHandRotation = seconds * 6;
        int minHandRotation = (minutes * 6) + (seconds / 10);
        int hourHandRotation = (hours * 30) + (minutes / 2);                                      ;

        // draw second hand
        canvas.save();
        canvas.rotate(360 - secHandRotation);
        canvas.drawLine(mSecHand[0],mSecHand[1],mSecHand[2],mSecHand[3],pStroke);
        canvas.restore();

        // draw minute hand
        canvas.save();
        canvas.rotate(360 - minHandRotation);
        Path minPath = new Path();
        minPath.lineTo(mMinHand[0],mMinHand[1]);
        minPath.lineTo(mMinHand[2],mMinHand[3]);
        minPath.lineTo(mMinHand[4],mMinHand[5]);
        minPath.lineTo(mMinHand[6],mMinHand[7]);
        minPath.lineTo(mMinHand[8],mMinHand[9]);
        minPath.close();
        canvas.drawPath(minPath,pFill);
        canvas.restore();

        //draw hour hand
        canvas.save();
        canvas.rotate(360 - hourHandRotation);
        Path hrPath = new Path();
        hrPath.lineTo(mHourHand[0],mHourHand[1]);
        hrPath.lineTo(mHourHand[2],mHourHand[3]);
        hrPath.lineTo(mHourHand[4],mHourHand[5]);
        hrPath.lineTo(mHourHand[6],mHourHand[7]);
        hrPath.lineTo(mHourHand[8],mHourHand[9]);
        hrPath.close();
        canvas.drawPath(hrPath,pFill);
        canvas.restore();

        // draw clock circle
        canvas.save();
        canvas.drawCircle(0,0,height / 2, pStroke);
        canvas.restore();

        // draw tick marks
        canvas.save();
        for (int i = 0; i < 60; i++) {
            // major tick marks
            canvas.drawLine(0,91,0,100,pStroke);
            canvas.rotate(6);
            for (int j = 0; j < 4; j++) {
                // minor tick marks
                canvas.drawLine(0,98,0,100,pStroke);
                canvas.rotate(6);
            }
        }
        canvas.restore();
    }

    public void resume() {
        mIsRunning = true;
        mHandler.postDelayed(mTimer, TIMER_MSEC);
    }

    public void pause() {
        mIsRunning = false;
        mHandler.removeCallbacks(mTimer);
    }

    private void onTimer() {
        time.setTime(mTime);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec) ;
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec) ;

        int calcWidth = (int)((float)parentHeight * mAspectRatio) ;
        int calcHeight = (int)((float)parentWidth / mAspectRatio) ;

        int finalWidth, finalHeight ;

        if (calcHeight > parentHeight) {
            finalWidth = calcWidth ;
            finalHeight = parentHeight ;
        } else {
            finalWidth = parentWidth ;
            finalHeight = calcHeight ;
        }

        setMeasuredDimension (finalWidth, finalHeight) ;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;

        state = bundle.getParcelable("instanceState");
        super.onRestoreInstanceState(state);
    }

    protected class TimeObserver extends Observable {
        private String mTime;

        private void setTime(String time) {
            mTime = time;
            setChanged();
            notifyObservers();
        }

        protected String getTime() {
            return mTime;
        }

    }
}
