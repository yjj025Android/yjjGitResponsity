package com.yjj.customwidget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yjj.customwidget.utils.MyLog;
import com.yjj.customwidget.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjj
 * Date : 2020/4/22
 * 扇形seekBar
 *
 * <com.farm.widget.RoundSeekBar
 *         android:layout_width="wrap_content"
 *         android:id="@+id/seekBar"
 *         app:sRadius="@dimen/dp_70"
 *         android:layout_gravity="center"
 *         app:iconVisible="true"
 *         android:layout_height="wrap_content"/>
 *
 **/
public class RoundSeekBar extends ViewGroup {

    // todo 自定义属性

    private LineView lineView;
    private ImageView iconView;
    private int bgWidth;
    private int iconRadius = 30;// 滑块半径
    private float paintWidth = 20; // 画笔宽度
    private double radiu;// 圆弧半径
    double parentCenterX, parentCenterY; // 圆弧中心点坐标
    private int[] colors;

    private double leftBottomX, leftBottomY;// progress为0的左下角坐标
    private double rightBottomX, rightBottomY;// progress为100的右下角坐标

    private double pi = Math.PI;
    private double radian; // 弧度

    private double angle; // 左下角的角度

    private double progress = 0f;
    private double max = 100f;

    public void setProgress(double progress) {
        this.progress = progress;
        layoutIcon();
    }

    private OnSeekBarChangeListener onSeekBarChangeListener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener{
        void onProgressChanged(RoundSeekBar seekBar, double progress);
        void onStartTrackingTouch(RoundSeekBar seekBar);
        void onStopTrackingTouch(RoundSeekBar seekBar);
    }

    public double getProgress() {
        return progress;
    }

    public double getMax() {
        return max;
    }

    private boolean iconVisible;// 滑块是否可见

    public void setIconVisible(boolean iconVisible) {
        this.iconVisible = iconVisible;
        requestLayout();
    }

    public RoundSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundSeekBar);
        float rad = typedArray.getDimension(R.styleable.RoundSeekBar_sRadius, 150);
        iconVisible = typedArray.getBoolean(R.styleable.RoundSeekBar_iconVisible, false);
        MyLog.e("rad:" + rad);
        initData(context, rad);
    }

    private void initData(Context context, float rad){
        colors = new int[]{R.color.color_f44336, R.color.them, R.color.color_ffa71c, R.color.color_01d3fd};
        radian = 240f / 360f * (2*pi);// 默认210度
        angle = (radian - pi) / 2f;

        // 滑道尺寸
        bgWidth = (int) (rad * ScreenUtil.getDeisity(context));
//        bgWidth = (int) (rad * ScreenUtil.getDeisity(context));
        radiu = bgWidth / 2f - paintWidth / 2f + paintWidth / 2f;// 圆弧半径，用于定位滑块位置
        int bgHeight = (int) (bgWidth/2f + bgWidth/2*sin(angle));
        lineView = new LineView(context);
        lineView.setLayoutParams(new ViewGroup.LayoutParams(bgWidth, bgHeight));
        addView(lineView);

        // 滑块尺寸
        iconView = new ImageView(context);
        iconView.setImageResource(R.drawable.circle_theme);
        iconView.setLayoutParams(new LayoutParams(iconRadius*2, iconRadius*2));
        addView(iconView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        View lineViewTemp = null;
        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            if (view instanceof LineView){
                lineViewTemp = view;
                break;
            }
        }
        if (lineViewTemp != null){
            setMeasuredDimension(lineViewTemp.getMeasuredWidth() + 100, lineViewTemp.getMeasuredHeight() + 100);
        }

        parentCenterX = (lineViewTemp.getMeasuredWidth()) / 2f + 50;
        parentCenterY = lineViewTemp.getMeasuredWidth() / 2f + 50;

        // 计算左下角坐标
        leftBottomX = parentCenterX - radiu * cos(angle);
        leftBottomY = parentCenterY + radiu * sin(angle);

        // 计算右下角坐标
        rightBottomX = parentCenterX + radiu * cos(angle);
        rightBottomY = parentCenterY + radiu * sin(angle);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            if (view instanceof LineView){
                layoutLineView(l,t,r,b);
            }
            if (view instanceof ImageView && iconVisible){
                layoutIcon();
            }
        }
    }

    /**
     * 放置滑块
     * **/
    private void layoutIcon() {
        double hasDegree = progress / max * radian;// 已经转过了这个角度
        if (hasDegree < angle){
            // 左下
            layoutLeftBottom(hasDegree);
        }else if (hasDegree < angle + pi/2f){
            // 左上
            layoutLeftTop(hasDegree);
        }else if (hasDegree < angle + pi){
            // 右上
            layoutRightTop(hasDegree);
        }else {
            // 右下
            layoutRightBottom(hasDegree);
        }
    }

    /**
     * 右下
     * **/
    private void layoutRightBottom(double hasDegree) {
        double angleX = angle - radian + hasDegree;// 与x轴的夹角
        int left = (int) (parentCenterX + radiu * cos(angleX) - iconRadius);
        int top = (int) (parentCenterY + radiu * sin(angleX) - iconRadius);
        int right = left + iconRadius*2;
        int bottom = top + iconRadius*2;
        iconView.layout(left, top, right, bottom);
        MyLog.e("右下 ： " + left + "," + top + "," + right + "," + top);
    }

    /**
     * 右上
     * **/
    private void layoutRightTop(double hasDegree) {
        double angleX = (pi + radian) / 2f - hasDegree;// 与x轴的夹角
        int left = (int) (parentCenterX + radiu * cos(angleX) - iconRadius);
        int top = (int) (parentCenterY - radiu * sin(angleX) - iconRadius);
        int right = left + iconRadius * 2;
        int bottom = top + iconRadius * 2;
        iconView.layout(left, top, right, bottom);
        MyLog.e("右上 ： " + left + "," + top + "," + right + "," + top);
    }

    /**
     * 左上
     * **/
    private void layoutLeftTop(double hasDegree) {
        double angleX = hasDegree - angle;// 与x轴的夹角
        int left = (int) (parentCenterX - radiu * cos(angleX) - iconRadius);
        int top = (int) (parentCenterY - radiu * sin(angleX) - iconRadius);
        int right = left + iconRadius * 2;
        int bottom = top + iconRadius * 2;
        iconView.layout(left, top, right, bottom);
        MyLog.e("左上 ： " + left + "," + top + "," + right + "," + top);
    }

    /**
     * 左下
     * **/
    private void layoutLeftBottom(double hasDegree) {
        double angleX = angle - hasDegree;// 与x轴的夹角
        int left = (int) (parentCenterX - radiu * cos(angleX) - iconRadius);
        int top = (int) (parentCenterY + radiu * sin(angleX) - iconRadius);
        int right = left + iconRadius*2;
        int bottom = top + iconRadius*2;
        iconView.layout(left, top, right, bottom);
        MyLog.e("左下 ： " + left + "," + top + "," + right + "," + top);
    }

    /**
     * 放置滑道
     * **/
    private void layoutLineView(int l, int t, int r, int b) {
        //  中心点坐标
        float centerX = (r - l) / 2f;
        float centerY = (b - t) / 2f;

        int left = (int) (centerX - lineView.getMeasuredWidth() / 2f);
        int top = (int) (centerY - lineView.getMeasuredHeight() / 2f);
        int right = (int) (centerX + lineView.getMeasuredWidth() / 2f);
        int bottom = (int) (centerY + lineView.getMeasuredHeight() / 2f);

        lineView.layout(left,top,right,bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!iconVisible){
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (downInIcon(event.getX(), event.getY()) || downInLine(event.getX(), event.getY())){
                    // 只有触摸在滑块上才会继续消耗后续事件 || 点击在滑道上也生效
                    if (onSeekBarChangeListener != null){
                        // 监听
                        onSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                    return true;
                }else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                // move
                handleMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                // 抬起的时候显示ui
                handleMove(event.getX(), event.getY());
                // 监听
                if (onSeekBarChangeListener != null){
                    onSeekBarChangeListener.onStopTrackingTouch(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据触摸点计算角度，再根据角度计算progress，progress重新拜访iconView
     * **/
    private void handleMove(float x, float y) {
        MyLog.e("x : " + x + "  centX : " + parentCenterX + "  y : " + y + "  centY : " + parentCenterY + "  leftBottomY : " + leftBottomY);
        if (y >= leftBottomY + iconRadius){
            // 超过下方，不是0，就是100
            if (x < parentCenterX){
                progress = 0;
                layoutIcon();
            }else {
                progress = 100;
                layoutIcon();
            }
            return;
        }
        if (x < parentCenterX) {
            // 左边
            handleLeftMove(x, y);
        }else if (x > parentCenterX){
            handleRightMove(x,y);
        }else {
            progress = 50;
            layoutIcon();
        }
        // 监听
        if (onSeekBarChangeListener != null){
            onSeekBarChangeListener.onProgressChanged(this, progress);
        }
    }

    /**
     * 移动到右侧
     * **/
    private void handleRightMove(float x, float y) {
        double hasAngle = radian - getRightHasMovedAngle(x,y); // 转过的角度
        progress = hasAngle / radian * max;
        layoutIcon();
    }

    /**
     * 计算右侧锐角大小
     * **/
    private double getRightHasMovedAngle(float x, float y){
        double a = getDistance(new Point(parentCenterX, parentCenterY),
                new Point(rightBottomX, rightBottomY));
        double b = getDistance(new Point(x, y),
                new Point(rightBottomX, rightBottomY));
        double c = getDistance(new Point(x, y),
                new Point(parentCenterX, parentCenterY));
        return Math.acos((a*a + c*c - b*b)/(2f*a*c));
    }

    /**
     * 移动到左边
     * **/
    private void handleLeftMove(float x, float y) {
        double hasAngle = getLeftHasMovedAngle(x,y); // 转过的角度
        progress = hasAngle / radian * max;
        layoutIcon();
    }

    /**
     * 计算左侧锐角大小
     * **/
    private double getLeftHasMovedAngle(float x, float y){
        double a = getDistance(new Point(parentCenterX, parentCenterY),
                new Point(leftBottomX, leftBottomY));
        double b = getDistance(new Point(x, y),
                new Point(leftBottomX, leftBottomY));
        double c = getDistance(new Point(x, y),
                new Point(parentCenterX, parentCenterY));
        return Math.acos((a*a + c*c - b*b)/(2f*a*c));
    }

    /**
     * 计算两点之间的距离
     * **/
    private double getDistance(Point p1, Point p2){
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    /**
     * 判断是否触摸在滑块范围内
     * **/
    private boolean downInIcon(float downX, float downY) {
        float iconX = iconView.getX();
        float iconY = iconView.getY();
        return downX >= iconX
                && downX <= iconX + iconRadius*2f
                && downY >= iconY
                && downY <= iconY + iconRadius*2f;
    }

    /**
     * 判断是否在滑道上
     * **/
    private boolean downInLine(float downX, float downY){
        // 点与圆心的距离大于r
        double distance = getDistance(
                new Point(downX, downY),
                new Point(parentCenterX, parentCenterY));
        return distance >= radiu - iconRadius;
    }

    private double sin(double rad){
        return Math.sin(rad);
    }
    private double cos(double rad){
        return Math.cos(rad);
    }


    /********************以下为滑道代码***************************/
    public class LineView extends View {

        // 画笔
        private List<Paint> paints;
        // 间隔角度
        private int divideDegree;
        private double startDegree;
        // 圆弧正方形
        private RectF oval;
        int width, height;

        public LineView(Context context) {
            super(context);
            initPaints(); // 初始化画笔
            startDegree = (pi - angle) / (2f * pi) * 360f; // 开始角度
            divideDegree = (int) ((radian / (2f * pi)) * 360f / colors.length); // 弧度->角度->除以360得到占比->除以颜色个数
        }

        /**
         * 初始化画笔
         * **/
        public void initPaints() {
            paints = new ArrayList<>();
            for (int color : colors){
                Paint paint = new Paint();
                paint.setColor(getContext().getResources().getColor(color));
                paint.setStrokeWidth(paintWidth);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);//取消锯齿
                paint.setPathEffect(new DashPathEffect(new float[]{4, 8}, 0));
                paints.add(paint);
            }
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getLayoutParams().width;
            height = getLayoutParams().height;
            MyLog.e("width : " + width + " height : " + height);
            setMeasuredDimension((int)(width + 2*paintWidth), (int)(height + 2*paintWidth));
            oval = new RectF();
            oval.left = paintWidth;
            oval.right = oval.left + width;
            oval.top = paintWidth;
            oval.bottom = oval.top + width;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            double start = startDegree;// 使用局部变量
            for (Paint paint : paints){
                // 不同颜色绘制不同paint
                canvas.drawArc(oval, (float) start, divideDegree, false, paint);
                start+=divideDegree;
            }

            // 不同颜色绘制不同paint
//            canvas.drawArc(oval, (float) startDegree, divideDegree, false, paint);
//            startDegree += divideDegree;
        }
    }

    /**
     * 坐标点
     * **/
    private class Point{
        double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    public int[] getColors() {
        return colors;
    }

    public double getAngle() {
        return angle;
    }

    public double getRadiu() {
        return radiu;
    }
}
