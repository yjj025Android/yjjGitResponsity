package com.yjj.customwidget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.logging.Logger;

/**
 * 自动换行的LinearLayout
 * **/
public class ChangeLineLinear extends LinearLayout {

    private static final String TAG = "ChangeLineLinear";

    public ChangeLineLinear(Context context) {
        super(context);
    }

    public ChangeLineLinear(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildren(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST)
                ,MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        );

        // 确定layout最大宽度，parent宽边界
        int parentMaxWidth = 0;
        ViewGroup.LayoutParams paramsP = getLayoutParams();
        switch (paramsP.width){
            case ViewGroup.LayoutParams.MATCH_PARENT:
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                parentMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
                break;
            default:
                parentMaxWidth = paramsP.width;
                break;
        }

        // 总宽高
        int totalWidth = 0 , totalHeight = 0;
        // 每行最高
        int maxHeight = 0;
        // 每行已有宽度
        int lineWidth = 0;

        for (int i = 0; i< getChildCount(); i++){
            View childView = getChildAt(i);
            LayoutParams params = (LayoutParams) childView.getLayoutParams();
            // 子view宽高
            int childHeight = childView.getMeasuredHeight();
            int childWidth = childView.getMeasuredWidth();
            // 子view占有（本身+margin）的宽高
            int viewHasWidth = childWidth + params.leftMargin + params.rightMargin;
            int viewHasHeight = childHeight + params.topMargin + params.bottomMargin;

            if (lineWidth + viewHasWidth <= parentMaxWidth){
                // 当前行继续往后加，比较当前行与之前一行，取最长宽度
                lineWidth += viewHasWidth;
                totalWidth = totalWidth < lineWidth ?  lineWidth : totalWidth;
                // 取当前行最高宽度
                if (maxHeight < viewHasHeight){
                    totalHeight = totalHeight - maxHeight + viewHasHeight;
                    maxHeight = viewHasHeight;
                }

            }else{
                // 换行
                // 比较与前一行，取最长宽度
                lineWidth = viewHasWidth;
                totalWidth = totalWidth < lineWidth ?  lineWidth : totalWidth;
                // 增加总高度
                maxHeight = viewHasHeight;
                totalHeight += maxHeight;
            }

        }
        Logger.getLogger(TAG).info("measuredWidth : " + totalWidth + "  measuredHeight : " + totalHeight);
        setMeasuredDimension(totalWidth,totalHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        int widthTotal = 0;// 这一行已经绘制的所有子view的宽度
        int heightTotal = 0;// 这一行和上面所有行的总高度
        int lineMaxHeight = 0; // 这一行所有子view的最高高度
        int lastTotalHeight = 0; // 这一行上面所有高度
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            View childView = getChildAt(i);

            LayoutParams params = (LayoutParams) childView.getLayoutParams();
            // 子view宽高
            int childHeight = childView.getMeasuredHeight();
            int childWidth = childView.getMeasuredWidth();
            Logger.getLogger(TAG).info("childWidth : " + childWidth + "   childHeight : " +childHeight);
            // 子view占有（本身+margin）的宽高
            int viewHasWidth = childWidth + params.leftMargin + params.rightMargin;
            int viewHasHeight = childHeight + params.topMargin + params.bottomMargin;

//            Logger.getLogger(TAG).info("widthTotal : " + widthTotal + "   childWidth : " + childWidth + "   screenWidth : " + screenWidth);

            if (widthTotal + viewHasWidth <= getMeasuredWidth()){
                // 在屏幕范围之内，直接绘制在后面

                int drawLeft = widthTotal + params.leftMargin;
                int drawTop = lastTotalHeight + params.topMargin;
                int drawRight = drawLeft + childWidth;
                int drawBottom = drawTop + childHeight;
                Logger.getLogger(TAG).info("left : " + drawLeft + "   top : " + drawTop + "   right : " + drawRight + "   bottom : " + drawBottom);
                // 绘制当前子view
                childView.layout(drawLeft,drawTop,drawRight,drawBottom);

                // 更新当前总共宽度
                widthTotal += viewHasWidth;
                // 更新当前这一行最高宽度,选取当前这一行最高的view高度,重新计算总高度
                if (lineMaxHeight < viewHasHeight){
                    heightTotal = heightTotal - lineMaxHeight + viewHasHeight;
                    lineMaxHeight = viewHasHeight;
                }

            }else{

                // 换行 增加所有高度、新行宽度和最高高度更新
                lastTotalHeight += lineMaxHeight;
                lineMaxHeight = viewHasHeight;
                Logger.getLogger(TAG).info("change line : heightTotal --> " + heightTotal);
                widthTotal = 0;

                // 换行  从头开始画
                int drawLeft = params.leftMargin;
                int drawTop = heightTotal + params.topMargin;
                int drawRight = drawLeft + childWidth;
                int drawBottom = drawTop + childHeight;
                Logger.getLogger(TAG).info("left : " + drawLeft + "   top : " + drawTop + "   right : " + drawRight + "   bottom : " + drawBottom);
                // 绘制
                childView.layout(drawLeft,drawTop,drawRight,drawBottom);

                // 更新当前已有宽度
                widthTotal += viewHasWidth;
                // 更新当前所有高度
                heightTotal += lineMaxHeight;

            }
        }
    }
}
