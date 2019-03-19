package com.yjj.customwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yjj.customwidget.widget.Banner;
import com.yjj.customwidget.widget.RatingBarWithDot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RatingBarWithDot.OnChangeListener {

    RatingBarWithDot ratingbar;
    Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ratingbar = findViewById(R.id.ratingBar);
        ratingbar.setOnChangeListener(this);

        banner = findViewById(R.id.banner);

        List<ImageView> views = new ArrayList<>();
        int[] pisc = new int[]{R.drawable.pic1, R.drawable.pic2,R.drawable.pic3, R.drawable.pic4};
        for (int i = 0; i<pisc.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(getResources().getDrawable(pisc[i]));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            views.add(imageView);
        }
        banner.setmViews(views);
        banner.setmScrollSpeed(1*1000);
        banner.setmDuration(5*1000);
//        banner.startScrollAuto();// 线程死循环
        banner.startByHandler();// handler延时发送

    }

    @Override
    public void onChange(double stars) {
        Toast.makeText(MainActivity.this,"stars : " + stars,Toast.LENGTH_SHORT).show();
    }
}
