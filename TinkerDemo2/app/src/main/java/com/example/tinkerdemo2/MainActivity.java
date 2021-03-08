package com.example.tinkerdemo2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tinker/patch/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1111);

        findViewById(R.id.load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPatch();
            }
        });

        findViewById(R.id.kill_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killApp();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            File file = new File(patchPath);
            if (file == null || !file.exists()){
                file.mkdirs();
            }
        }
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath.concat("first.apk"));
    }

    /**
     * 杀死应用加载补丁
     */
    public void killApp() {
        ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
