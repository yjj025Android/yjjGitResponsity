package com.yjj.customwidget.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.yjj.customwidget.utils.MyLog;
import com.yjj.customwidget.utils.ScreenUtil;
import com.yjj.customwidget.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yjj
 * Date : 2020/4/27
 * 扇形图
 * fanChart.setData(fanData);
 * fanChart.setClickEnable(true);
 * fanChart.setRadius(120, 130);
 * fanChart.setLineLength(30);
 * fanChart.setTextSize(9);
 * fanChart.submitUiChange();
 **/
public class FanChart extends View {

    // todo 自定义属性

    private List<FanData> data = new ArrayList<>();
    private double radius = 200;// 初始半径
    private double radiusFocus = 220;// 选中半径
    private int centerX, centerY;// 中心点坐标
    private int clickPosition = -1; // 点击选中的下表
    private List<Boundary> boundaries = new ArrayList<>();// 每个扇形的角度绘制范围
    private double pi = Math.PI;
    private Paint linePaint;// 实线画线
    private int lineLength = 80;// 虚线大小
    private Paint textLinePaint;// 虚线画笔
    private Paint textPaint;// 文字画笔

    public List<FanData> getData() {
        return data;
    }

    public FanChart(Context context) {
        super(context);
    }

    public FanChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        data = new ArrayList<>();
        data.add(new FanChart.FanData(Color.RED, 0.2));
        data.add(new FanChart.FanData(Color.GREEN, 0.3));
        data.add(new FanChart.FanData(Color.BLUE, 0.4));
        data.add(new FanChart.FanData(Color.BLACK, 0.1));
        initData();
    }


    private float textSize = 8;
    private void initData(){
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(4);
        linePaint.setAntiAlias(true);//取消锯齿

        textLinePaint = new Paint();
        textLinePaint.setColor(Color.GRAY);
        textLinePaint.setStyle(Paint.Style.FILL);
        textLinePaint.setStrokeWidth(2);
        textLinePaint.setAntiAlias(true);//取消锯齿
        textLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));

        textPaint = new Paint();
        textPaint.setColor(Color.GRAY);
        textPaint.setAntiAlias(true);//取消锯齿
        textPaint.setTextSize(textSize * ScreenUtil.getDeisity(getContext()) + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (2*radiusFocus + 50f);
        int width = (int) (2*radiusFocus + getTextWidth("100.00%")*2 + lineLength * 2);
//        int width = getLayoutParams().width;
        setMeasuredDimension(width, height);
        centerX = (int) (width / 2f);
        centerY = (int) (height / 2f);

        MyLog.e("X : " + centerX + " Y : " + centerY);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.size() == 0){
            MyLog.e("call setData() first draw");
            return;
        }
        boundaries = new ArrayList<>();
        int startDegree = 0;
        for (FanData fanData : data){
            // 绘制扇形
            int degree = (int) (360 * fanData.percent);// 计算绘制的角度 360占比
            Boundary boundary = new Boundary(startDegree, startDegree+degree);
            boundaries.add(boundary);
            fanData.setBoundary(boundary);
            MyLog.e("draw arc");
            canvas.drawArc(getOval(fanData), startDegree, degree, true, fanData.getPaint());
            // 绘制线，做出一个中间有间隔的效果
            drawLine(canvas, fanData, startDegree, startDegree + degree);
            startDegree += degree;

            // 绘制文字
            drawText(canvas, fanData);
        }
    }

    /**
     * 绘制文字
     * **/
    private void drawText(Canvas canvas, FanData fanData) {
        // 先绘制虚线，从平分线开始绘制
        Point point = drawTextLine(canvas, fanData);
        // 再绘制文字
        drawPercentText(canvas, point, fanData);
    }

    /**
     * 绘制文字
     * **/
    private void drawPercentText(Canvas canvas, Point point, FanData fanData) {
        String text = MathUtils.lastTwo((float) (fanData.getPercent() * 100f)) + "%";
        if (point.isLeft()){
            // 往左边绘制文字
            canvas.drawText(text, (float) (point.getX() -  getTextWidth(text) - 10), (float) point.getY(), textPaint);
        }else {
            // 往右边绘制文字
            canvas.drawText(text, (float) point.getX(), (float) point.getY() + 10, textPaint);
        }
    }

    /**
     * 文字宽度
     * **/
    private double getTextWidth(String text){
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        MyLog.e("文字宽度 : " + rect.width());
        return rect.width();
    }

    /**
     * 绘制文字虚线
     * **/
    private Point drawTextLine(Canvas canvas, FanData fanData) {
        boolean left;// 判断绘制文字的时候往哪边绘制，虚线左边还是虚线右边
        double degree = fanData.getCenterAngle();// 平分线角度
        MyLog.e("degree : " + degree);
        // 计算平分线与圆弧边界的交点坐标
        Point point = getCrossPoint(fanData, degree);
        float endX;
        if (point.getX() < centerX){
            // 往左边画
            left = true;
            endX = (float) (point.getX() - lineLength);
        }else {
            // 往右边画
            left = false;
            endX = (float) (point.getX() + lineLength);
        }
        // 绘制线
        canvas.drawLine(
                (float) point.getX(),
                (float) point.getY(),
                endX,
                (float) point.getY(),
                textLinePaint);
        Point pointResult =  new Point(endX, point.getY());
        pointResult.setLeft(left);
        return pointResult;
    }

    /**
     * 线与圆弧交点坐标
     * **/
    private Point getCrossPoint(FanData fanData, double degree) {
        double x = 0, y = 0;
        double r = radius;
        if (fanData.isShow()){
            r = radiusFocus;
        }
        if (degree >=0 && degree < 90){
            // 右下
            x = centerX + r*Math.cos(Math.toRadians(degree));
            y = centerY + r*Math.sin(Math.toRadians(degree));
        }else if (degree >= 90 && degree < 180){
            // 左下
            x = centerX - r*Math.cos(Math.toRadians(180 - degree));
            y = centerY + r*Math.sin(Math.toRadians(180 - degree));
        }else if (degree >= 180 && degree < 270){
            // 左上
            x = centerX - r*Math.cos(Math.toRadians(degree - 180));
            y = centerY - r*Math.sin(Math.toRadians(degree - 180));
        }else {
            // 右上
            x = centerX + r*Math.cos(Math.toRadians(360 - degree));
            y = centerY - r*Math.sin(Math.toRadians(360 - degree));
        }
        return new Point(x, y);
    }

    /**
     * 画布旋转绘制线，做出扇形中间有间隔的效果
     * **/
    private void drawLine(Canvas canvas, FanData fanData, int startDegree, int endDegree){
        canvas.save();
        canvas.rotate(startDegree, centerX, centerY);
        if (fanData.isShow()) {
            canvas.drawLine(centerX, centerY, (float) (centerX + radiusFocus), centerY, linePaint);
        }else {
            canvas.drawLine(centerX, centerY, (float) (centerX + radius), centerY, linePaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(endDegree, centerX, centerY);
        if (fanData.isShow()) {
            canvas.drawLine(centerX, centerY, (float) (centerX + radiusFocus), centerY, linePaint);
        }else {
            canvas.drawLine(centerX, centerY, (float) (centerX + radius), centerY, linePaint);
        }
        canvas.restore();
    }

    private RectF getOval(FanData fanData) {
        RectF oval = new RectF();
        if (!fanData.isShow()){
            // 常规oval
            oval.left = (float) (centerX - radius); // 平移x
            oval.top = (float) (centerY - radius);
            oval.right = (float) (oval.left + 2*radius);
            oval.bottom = (float) (oval.top + 2*radius);
        }else {
            // 选中oval
            oval.left = (float) (centerX - radiusFocus); // 平移x
            oval.top = (float) (centerY - radiusFocus);
            oval.right = (float) (oval.left + 2*radiusFocus);
            oval.bottom = (float) (oval.top + 2*radiusFocus);
        }
        MyLog.e("oval : " + oval.toShortString());
        return oval;
    }

    private boolean clickEnable = false;

    public void setClickEnable(boolean clickEnable) {
        this.clickEnable = clickEnable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event.getX(), event.getY());
                break;
        }
        return clickEnable;
    }

    /**
     * 处理抬起事件
     * **/
    private void handleActionUp(float x, float y) {
        int distance = (int) getDistance(new Point(x, y), new Point(centerX, centerY));
        if (distance > radius){
            // 点击范围超过半径，视为无效点击
            return;
        }
        if (boundaries == null || boundaries.size() == 0){
            MyLog.e("call setData() first");
            // 没有绘制
            return;
        }
        // 点击范围内，即有效点击
        // 计算与x轴的夹角，判断点击在哪个扇形里面
        double angle = 0;
        if (x > centerX){
            if (y > centerY){
                // 右下
                MyLog.e("右下");
                angle = Math.toDegrees(Math.acos((x - centerX) / radius));
            }else {
                // 右上
                MyLog.e("右上");
                angle = 360f - Math.toDegrees(Math.acos((x - centerX) / radius));
            }
        }else {
            if (y > centerY){
                // 左下
                MyLog.e("左下");
                angle = 180f - Math.toDegrees(Math.acos((centerX - x) / radius));
            }else {
                // 左上
                MyLog.e("左上");
                angle = 180f + Math.toDegrees(Math.acos((centerX - x) / radius));
            }
        }
        int lastClickPosition = -1;
        for (int i = 0; i < boundaries.size(); i++){
            Boundary boundary = boundaries.get(i);
            if (boundary.isInBoundary(angle)){
                lastClickPosition = clickPosition;// 保存上次的点击位置
                clickPosition = i; // 重新赋值当前点击位置
                break;
            }
        }
        MyLog.e("clickPosition : " + clickPosition);
        // 当前点击位置不匹配
        if (clickPosition == -1){
            return;
        }
        // 清空上次点击状态
        if (lastClickPosition != -1){
            data.get(lastClickPosition).setShow(false);
        }
        // 重新设置当前点击状态
        data.get(clickPosition).setShow(true);
        // 重新绘制
        invalidate();
    }


    /**
     * 计算两点之间的距离
     * **/
    private double getDistance(Point p1, Point p2){
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    /**
     * 设置颜色值等信息
     * **/
    public void setData(List<FanData> data) {
        this.data = data;
    }

    /**
     * 改变绘制半径(单位：像素)
     * **/
    public void setRadius(float r, float rFocus){
        this.radius = r;
        this.radiusFocus = rFocus;
    }

    /**
     * 百分比线大小
     * **/
    public void setLineLength(int length){
        this.lineLength = length;
    }

    /**
     * 设置百分比文字大小
     * **/
    public void setTextSize(float size){
        this.textSize = size;
    }

    /**
     * 刷新ui
     * **/
    public void submitUiChange(){
        invalidate();
    }

    /**
     * 扇形角度范围
     * **/
    private class Boundary{
        int start,end;

        public Boundary(int start, int end) {
            this.start = start;
            this.end = end;
            MyLog.e("boundary : " + toString());
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        // 是否位于范围内
        public boolean isInBoundary(double angle){
            return angle >= start && angle <= end;
        }

        @Override
        public String toString() {
            return "Boundary{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    /**
     * 坐标点
     * **/
    private class Point{
        double x, y;
        boolean left;

        public boolean isLeft() {
            return left;
        }

        public void setLeft(boolean left) {
            this.left = left;
        }

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

    /**
     * 数据模型
     * **/
    public static class FanData{
        private int color; // 颜色
        private double percent; // 占比
        private boolean show = false; // 是否选中
        private Paint paint; // 画笔
        private Boundary boundary;
        private String name;
        private String count;

        public String getName() {
            return name;
        }

        public String getCount() {
            return count;
        }

        // 平分线角度
        private double getCenterAngle(){
            return (boundary.end + boundary.start) / 2f;
        }

        public void setBoundary(Boundary boundary) {
            this.boundary = boundary;
        }

        public FanData(int color, double percent, String name, String count) {
            this.color = color;
            this.percent = percent;
            this.name = name;
            this.count = count;
            initPaint();
        }

        public FanData(int color, double percent) {
            this.color = color;
            this.percent = percent;
            initPaint();
        }

        // 初始化画笔
        private void initPaint() {
            paint = new Paint();
            paint.setColor(getColor());
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);//取消锯齿
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public Paint getPaint() {
            return paint;
        }

        public boolean isShow() {
            return show;
        }

        public int getColor() {
            return color;
        }

        public double getPercent() {
            return percent;
        }
    }
}
