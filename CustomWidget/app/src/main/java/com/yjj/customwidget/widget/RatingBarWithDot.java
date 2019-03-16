package com.yjj.customwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yjj.customwidget.R;

import java.util.logging.Logger;

/**
 *
 * 支持自定义item图片、item宽高、item间隔
 * ratingbar小数点支持，精确到double
 *
 * **/
public class RatingBarWithDot extends View {

    // item 选中个数
    private double mStars;
    // item 总数
    private int max;
    // item 间隔
    private int padding;
    // item 宽高
    private int itemWidth,itemHeight;
    // item 选中和默认图片
    private Bitmap filledStar,emptyStar;
    private boolean isMiddle = true;

    public void setStars(double mStars) {
        this.mStars = mStars;
        invalidate();
    }

    public double getStars() {
        return mStars;
    }

    public RatingBarWithDot(Context context) {
        super(context);
        initData();
    }

    public RatingBarWithDot(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.RatingBarWithDot);
        max = array.getInteger(R.styleable.RatingBarWithDot_max,5);
        padding = array.getDimensionPixelSize(R.styleable.RatingBarWithDot_padding, 10);
        itemWidth = array.getDimensionPixelSize(R.styleable.RatingBarWithDot_itemWidth,25);
        itemHeight = array.getDimensionPixelSize(R.styleable.RatingBarWithDot_itemHeight,25);

        filledStar = BitmapFactory.decodeResource(context.getResources(),
                array.getResourceId(R.styleable.RatingBarWithDot_fill, -1));
        filledStar = Bitmap.createScaledBitmap(filledStar,itemWidth,itemHeight,true);
        emptyStar = BitmapFactory.decodeResource(context.getResources(),
                array.getResourceId(R.styleable.RatingBarWithDot_empty, -1));
        emptyStar = Bitmap.createScaledBitmap(emptyStar,itemWidth,itemHeight,true);

        if (filledStar == null || emptyStar == null){
            Logger.getLogger("info").info("null");
        }

        mStars = array.getFloat(R.styleable.RatingBarWithDot_stars, 0);

        setStars(mStars);

    }

    private void initData(){
        max = 5;
        padding = 10;
        itemWidth = 25;
        itemHeight = 25;
        mStars = 0;
        filledStar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), -1),
                itemWidth,itemHeight,false);
        emptyStar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), -1),
                itemWidth,itemHeight,false);

        setStars(mStars);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = max * (itemWidth + padding) - padding;
        int height = itemHeight;
        setMeasuredDimension(width, height);

    }

    int drawTop,drawBottom;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTop = (getHeight() - itemHeight) / 2;
        drawBottom = drawTop + itemHeight;

        int position = 1;
        while(position <= max){
            if (position <= mStars){
                drawLeft(canvas, position);
            }else{
                drawMiddleAndRight(canvas, position);
            }
            position ++;
        }
    }

    /**
     * draw middle and right stars
     * **/
    private void drawLeft(Canvas canvas, int position) {
        int drawLeft = (position - 1) * (padding + itemWidth);
        int drawRight = drawLeft + itemWidth;

        Logger.getLogger("info").info("drawLeft----------------------");
        // draw bitmap
        canvas.drawBitmap(filledStar, null, new RectF(drawLeft,drawTop,drawRight,drawBottom), null);
    }

    /**
     * draw left stars which is filled
     * */
    private void drawMiddleAndRight(Canvas canvas, int position) {
        double remain = mStars - Math.floor(mStars);
        int remainPix = (int) (remain * itemWidth);
        if (remainPix == 0){
            drawRight(canvas,position);
            isMiddle = false;
        }else{
            if (isMiddle){
                drawMiddleLeft(canvas,remainPix,position);
                drawMiddleRight(canvas,remainPix, position);
                isMiddle = false;
            }else{
                drawRight(canvas, position);
            }

        }
    }

    private int drawMiddleLeft(Canvas canvas, int leftSize, int position){
        Bitmap bitmap = Bitmap.createBitmap(filledStar, 0 ,0 ,leftSize, itemHeight);
        int drawLeft = (position - 1) * (itemWidth + padding);
        int drawRight = drawLeft + leftSize;

        canvas.drawBitmap(bitmap,drawLeft,drawTop,null);
        return drawRight;
    }
    private void drawMiddleRight(Canvas canvas, int leftSize, int position){
        Bitmap bitmap = Bitmap.createBitmap(emptyStar,leftSize,0, itemWidth - leftSize ,itemHeight);
        int drawLeft = leftSize + (position - 1) * (itemWidth + padding);
        canvas.drawBitmap(bitmap,drawLeft,drawTop,null);
    }


    /**
     * draw right stars which is empty
     * **/
    private void drawRight(Canvas canvas, int position){
        int drawLeft = (position - 1) * (padding + itemWidth);
        int drawTop = (getHeight() - itemHeight) / 2;
        // draw bitmap
        canvas.drawBitmap(emptyStar, drawLeft,drawTop, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int position;
        switch (action){
            case MotionEvent.ACTION_DOWN:

                position = getPosition(event.getX());
                if (mStars == position && mStars != 0){
                    setStars(0);
                }else {
                    setStars(position);
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (listener != null){
                    listener.onChange(mStars);
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private int getPosition(float x) {
        return (int) (x*max/getWidth()) + 1;
    }

    OnChangeListener listener;

    public void setOnChangeListener(OnChangeListener listener){
        this.listener = listener;
    }
    OnChangeListener getOnChangeListener(){
        return listener;
    }

    public interface OnChangeListener{
        void onChange(double stars);
    }

}
