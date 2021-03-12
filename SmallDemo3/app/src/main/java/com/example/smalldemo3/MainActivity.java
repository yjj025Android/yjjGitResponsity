package com.example.smalldemo3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.wequick.small.Small;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Small.setUp(MainActivity.this, new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("main", MainActivity.this);
                    }
                });

            }
        });
    }
}
