package com.yjj.customwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yjj.customwidget.widget.RatingBarWithDot;

public class MainActivity extends AppCompatActivity implements RatingBarWithDot.OnChangeListener {

    RatingBarWithDot ratingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ratingbar = findViewById(R.id.ratingBar);
        ratingbar.setOnChangeListener(this);

    }

    @Override
    public void onChange(double stars) {
        Toast.makeText(MainActivity.this,"stars : " + stars,Toast.LENGTH_SHORT).show();
    }
}
