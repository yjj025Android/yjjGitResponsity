package com.example.tinkerdemo2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * author : yjj
 * date   : 2021/3/515:03
 * desc   :
 */
@DefaultLifeCycle(application = "com.example.tinkerdemo2.SampleApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class SampleApplicationLike extends DefaultApplicationLike {

    public SampleApplicationLike(Application application,
                                 int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime,
                                 long applicationStartMillisTime,
                                 Intent tinkerResultIntent) {

        super(application,
                tinkerFlags,
                tinkerLoadVerifyFlag,
                applicationStartElapsedTime,
                applicationStartMillisTime,
                tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        MultiDex.install(base);

        TinkerInstaller.install(
                this,
                new DefaultLoadReporter(getApplication()),
                new DefaultPatchReporter(getApplication()),
                new DefaultPatchListener(getApplication()),
                SampleResultService.class,
                new UpgradePatch());
        Tinker tinker = Tinker.with(getApplication());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 自己app的application内容
    }
}
