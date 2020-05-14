package com.farm.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * desc : 计算工具类
 */

public class MathUtils {
    /**
     * TODO 除法运算，保留小数
     * @date 2018-4-17下午2:24:48
     * @param a 被除数
     * @param b 除数
     * @return 商
     */
    public static String txfloat(int a,int b) {

        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数

        return df.format((float)a/b);

    }

    /**
     * 保留两位小数
     * **/
    public static String lastTwo(float original){
        DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
        return df.format(original);
    }

    /**
     * 保留x位小数
     * **/
    public static String lastPoint(float original, String format){
        DecimalFormat df=new DecimalFormat(format);//设置保留位数
        return df.format(original);
    }


    /**
     * 变成xxx.xx万
     * **/
    public static String toTenThousand(String number){
        if (TextUtils.isEmpty(number)){
            return "0";
        }
        long content = Long.valueOf(number);
        if (content < 10000){
            return number;
        }
        return lastPoint(content * 1.0f / 10000.0f, "0.00") + "万";
    }

}
