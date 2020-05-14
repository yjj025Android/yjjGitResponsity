package com.yjj.customwidget.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yjj.customwidget.utils.MyLog;
import com.yjj.customwidget.utils.ScreenUtil;

import java.util.List;

/**
 * Created by yjj
 * Date : 2020/4/27
 * 柱状图
 * List<HistogramView.HistogramData> hisData;
 * histogramView.setData(hisData)
 *
 **/
public class HistogramView extends ViewGroup {

    // todo 自定义属性

    private HistogramBackgroundView axisView;
    private YAxisView yAxisView;
    private BgView bgView;
    private HorizontalScrollView scrollView;
    private TextView titleTv;

    private RectF axisRecf;// 坐标轴矩形
    private RectF yRecf;// 纵坐标矩形
    private RectF rRecf;// item矩形
    private RectF bgRecf;// 背景

    private Paint axisPaint; // 坐标轴画笔

    private int bgLineHeight = 80;// 背景每一行的高度
    private int rowCount = 10; // 多少行
    private int bgColor, bgDarkColor;// 绘制背景颜色

    private int yLeft = 150; // y轴宽度
    private int yTop = 100; // y轴距离上方距离
    private int xRight = 50; // x轴距离右边距离
    private int xBottom = 150; // x轴内容高度
    private int lineWidth = 4; // 坐标轴宽度
    private int lineCountAverage = 100;// 每行代表多少量

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 背景view
        axisView = new HistogramBackgroundView(context);
        axisView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(axisView);

        // 纵坐标
        yAxisView = new YAxisView(context);
        yAxisView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(yAxisView);

        // 背景
        bgView = new BgView(context);
        bgView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(bgView);


        // 横坐标
        scrollView = new HorizontalScrollView(context);
        scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(scrollView);

        // 文字
        titleTv = new TextView(context);
        titleTv.setText("(亩均总施肥量kg/亩)");
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        titleTv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(titleTv);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getVisibility() != VISIBLE){
            return;
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(getMeasuredWidth(), rowCount*bgLineHeight + xBottom);

        // 部分尺寸
        initSize();

    }

    /**
     * 初始化部分尺寸
     * **/
    private void initSize(){
        // 父控件 的尺寸
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();

        yRecf = new RectF(0, yTop, yLeft, parentHeight - xBottom);
        bgRecf = new RectF(yLeft + lineWidth, yTop, parentWidth, parentHeight - xBottom);
        axisRecf = new RectF(yLeft, 0, parentWidth, parentHeight - xBottom + lineWidth);
        rRecf = new RectF(yLeft, 0, parentWidth-xRight, parentHeight);

        yAxisView.setLayoutParams(new LayoutParams((int) (yRecf.right - yRecf.left), (int) (yRecf.bottom - yRecf.top)));
        bgView.setLayoutParams(new LayoutParams((int) (bgRecf.right - bgRecf.left), (int) (bgRecf.bottom - bgRecf.top)));
        axisView.setLayoutParams(new LayoutParams((int) (axisRecf.right - axisRecf.left), (int) (axisRecf.bottom - axisRecf.top)));
        scrollView.setLayoutParams(new LayoutParams((int) (rRecf.right - rRecf.left), (int) (rRecf.bottom - rRecf.top)));
    }

    public void setData(List<HistogramData> list){
        if (list == null || list.isEmpty()){
            return;
        }

        // 动态计算纵坐标数值
        handleYData(list);

        scrollView.removeAllViews();
        LinearLayout parentLayout = new LinearLayout(getContext());
        parentLayout.removeAllViews();
        parentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        parentLayout.setGravity(Gravity.BOTTOM);
        for (int i = 0; i < list.size(); i++){
            HistogramData histogramData = list.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_histogram, null, false);
            View item = view.findViewById(R.id.view);
            TextView name_tv = view.findViewById(R.id.name_tv);
            TextView count_tv = view.findViewById(R.id.count_tv);
//            item.setBackground(bgDrawable.get(i%bgDrawable.size()));
            item.setBackground(histogramData.columnDrawable);
            name_tv.setText(histogramData.name);
            count_tv.setText(histogramData.count+"");

            // 下方x轴文字尺寸
            name_tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, xBottom));
            int height = (int) (histogramData.count / (lineCountAverage * rowCount) * (getHeight() - yTop - xBottom));
            MyLog.e("height : " + height);
            int viewWidth = (int)(20*ScreenUtil.getDeisity(getContext()));
            item.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, height));

            // 柱体尺寸和间隔
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int totalWidth = (int) (rRecf.right - rRecf.left - xRight);
            int distance = (totalWidth - list.size() * viewWidth) / (list.size() * 2);
            if (distance < 10 * ScreenUtil.getDeisity(getContext())){
                distance = (int) (10 * ScreenUtil.getDeisity(getContext()));
            }
            layoutParams.setMargins(distance, 0, 0, 0);
            view.setLayoutParams(layoutParams);

            parentLayout.addView(view);
        }
        scrollView.addView(parentLayout);
    }

    /**
     * 动态计算纵坐标的值
     * **/
    private void handleYData(List<HistogramData> list) {
        int max = 0;
        for (HistogramData histogramData : list){
            if (histogramData.count > max){
                max = (int) histogramData.count;
            }
        }
        MyLog.e("max : " + max);
        // 默认10行
        rowCount = 10;
        // 得到最大值max
        if (max > 1000){
            // 1000已上，每行100，增加行数
            rowCount = max / 100;
            lineCountAverage = 100;
        }else {
            // 小于1000
            if (max >= 100){
                // 三位数
                lineCountAverage = ((max / 100) + 1) * 100 / rowCount;
            }else if (max >= 10){
                // 两位数
                lineCountAverage = ((max / 10) + 1) * 10 / rowCount;
            }else if (max >= 1){
                // 个位数
                lineCountAverage = 10 / rowCount;
            }
        }
        MyLog.e("行数 ： " + rowCount + "  每行 ： " + lineCountAverage);
        // y轴更新ui
        yAxisView.refreshUi();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getVisibility() != VISIBLE){
            return;
        }
        // 摆放坐标轴
        layoutAxis();
        // 摆放背景
        layoutBg();
        // 摆放纵坐标
        layoutY();
        // 摆放柱体
        layoutItem();
        // 摆放文字
        layoutText();
    }

    private void layoutText() {
        titleTv.layout((int)bgRecf.left, 30, (int)(bgRecf.left + titleTv.getMeasuredWidth()), (30 + titleTv.getMeasuredHeight()));
    }

    private void layoutBg() {
        bgView.layout((int)bgRecf.left,(int)bgRecf.top,(int)bgRecf.right,(int)bgRecf.bottom);
    }

    private void layoutItem() {
        scrollView.layout((int)rRecf.left,(int)rRecf.top,(int)rRecf.right,(int)rRecf.bottom);
    }


    private void layoutY() {
        double itemHeight = (yRecf.bottom - yRecf.top) / rowCount;
        int left = (int) yRecf.left;
        int right = (int) yRecf.right;
        int top = (int) (yRecf.top - itemHeight / 2f);
        int bottom = (int) (yRecf.bottom - itemHeight / 2f);
        yAxisView.layout(left, top, right, bottom);
//        yAxisView.layout((int)yRecf.left,(int)yRecf.top,(int)yRecf.right,(int)yRecf.bottom);
    }

    private void layoutAxis() {
        axisView.layout((int) axisRecf.left, (int) axisRecf.top, (int) axisRecf.right, (int) axisRecf.bottom);
    }

//    private List<Drawable> bgDrawable = new ArrayList<>();
//    private void initbgDrawable(){
//        bgDrawable = new ArrayList<>();
//        bgDrawable.add(getContext().getResources().getDrawable(R.drawable.gradient_theme));
//        bgDrawable.add(getContext().getResources().getDrawable(R.drawable.gradient_orange));
//        bgDrawable.add(getContext().getResources().getDrawable(R.drawable.gradient_red));
//    }

    /**
     * 纵坐标
     * **/
    private class YAxisView extends LinearLayout {

        private Context mContext;

        public YAxisView(Context context) {
            super(context);
            setOrientation(VERTICAL);
            this.mContext = context;
            refreshUi();
        }

        public void refreshUi(){
            removeAllViews();
            for (int i = rowCount; i > 0; i--){
                TextView textView = new TextView(mContext);
                textView.setText(i*lineCountAverage+"");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
                params.weight = 1;
                textView.setLayoutParams(params);
                addView(textView);
            }
        }
    }

    private class BgView extends LinearLayout{

        public BgView(Context context) {
            super(context);
            setOrientation(VERTICAL);
            removeAllViews();
            for (int i = rowCount; i > 0; i--){
                TextView textView = new TextView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
                params.weight = 1;
                textView.setLayoutParams(params);
                if (i%2 == 0){
                    textView.setBackgroundColor(bgDarkColor);
                }else {
                    textView.setBackgroundColor(bgColor);
                }
                addView(textView);
            }
        }
    }

    /**********************************背景view start******************************/
    /**
     * 背景view
     * **/
    private class HistogramBackgroundView extends View{

        public HistogramBackgroundView(Context context) {
            super(context);
            initBgData();
        }

        private void initBgData(){
            axisPaint = new Paint();
            axisPaint.setColor(Color.BLACK);
            axisPaint.setStrokeWidth(lineWidth);
            axisPaint.setStyle(Paint.Style.FILL);
            axisPaint.setAntiAlias(true);//取消锯齿

            bgColor = Color.GRAY;
            bgDarkColor = Color.;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // 绘制横纵坐标
            drawAxis(canvas);
        }

        /**
         * 绘制横纵坐标
         * **/
        private void drawAxis(Canvas canvas) {
            // 纵坐标
            canvas.drawLine(0, getMeasuredHeight(), 0, 0, axisPaint);
            // 横坐标
            canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), axisPaint);
        }

    }

    /**********************************背景view end******************************/

    /**
     * 数据模型
     * **/
    public static class HistogramData{
        private double count;
        private Drawable columnDrawable;
        private String name;

        public HistogramData(double count, Drawable columnDrawable, String name) {
            this.count = count;
            this.columnDrawable = columnDrawable;
            this.name = name;
        }
    }

    /**
     * 点坐标
     * **/
    private class Point{
        private int x,y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
