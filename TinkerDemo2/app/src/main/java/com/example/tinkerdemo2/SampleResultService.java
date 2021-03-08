package com.example.tinkerdemo2;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;

/**
 * author : yjj
 * date   : 2021/3/515:09
 * desc   :
 */
public class SampleResultService extends DefaultTinkerResultService {

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onPatchResult(PatchResult result) {
        super.onPatchResult(result);

        boolean isSuccess = result.isSuccess;
        if (isSuccess) {
            Toast.makeText(getApplicationContext(), "patchSuccess", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "patchFail", Toast.LENGTH_LONG).show();
        }

        Log.e("yjj", "patch result : " + result.toString());
    }
}
