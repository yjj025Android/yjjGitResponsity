package com.yjj.customwidget.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.List;

public class Banner extends ViewPager {

    private int mScrollSpeed;
    private int mDuration;
    private int mCount;
    private int mCurrent;
    private List<ImageView>mViews;
    private Adapter mAdapter;
    Context context;

    public Banner(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int mScrollSpeed) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context = context;
        setListener();
        mCount = 0;
        mDuration = 3*1000;
    }

    /**
     * 滑动监听
     * **/
    public void setListener(){
        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mCurrent = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }
    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 修改viewpager滑动时间
     * **/
    private void changeScrollSpeed(){
        try {
            Field mScroll = ViewPager.class.getDeclaredField("mScroller");
            mScroll.setAccessible(true);
            SpeedScroller scroller = new SpeedScroller(getContext());
            scroller.setmDuration(getmScrollSpeed());
            mScroll.set(this, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFinish = true;
    // 线程死循环+线程休眠
    public void startScrollAuto(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    try {
                        Thread.sleep(mDuration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (isFinish){
                        isFinish = false;
                        next();
                    }
                }
            }
        }).start();
    }

    // handler延时发送
    public void startByHandler(){
        nextHandler();
    }

    public void nextHandler(){
        mCurrent++;
        mCurrent = mCurrent % mCount;
        handler.sendEmptyMessageDelayed(0001, mDuration);
    }

    private void next() {

        mCurrent++;
        mCurrent = mCurrent % mCount;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                isFinish = true;
                setCurrentItem(mCurrent, true);
            }
        });
    }

    MyHandler handler = new MyHandler();
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurrentItem(mCurrent, true);
            nextHandler();
        }
    }

    private class SpeedScroller extends Scroller{

        private int mDuration;

        public SpeedScroller(Context context) {
            super(context);
        }

        public int getmDuration() {
            return mDuration;
        }

        public void setmDuration(int mDuration) {
            this.mDuration = mDuration;
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    private class Adapter extends PagerAdapter {

        private List<ImageView>views;

        public Adapter(List<ImageView> views){
            this.views = views;
        }

        @Override
        public int getCount() {
            return views == null ? 0 : views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }
    }

    public void setmViews(List<ImageView> mViews) {
        this.mViews = mViews;
        mCount = mViews == null ? 0 : mViews.size();
        mAdapter = new Adapter(mViews);
        setAdapter(mAdapter);
    }

    public List<ImageView> getmViews() {
        return mViews;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getmDuration() {
        return mDuration;
    }

    public int getmScrollSpeed() {
        return mScrollSpeed;
    }

    public void setmScrollSpeed(int mScrollSpeed) {
        this.mScrollSpeed = mScrollSpeed;
        changeScrollSpeed();
    }
}
