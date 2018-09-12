package com.android.cloud.speedup;

import android.app.Application;

/**
 * Created by yingyongduoduo on 2018/1/19.
 */

public class SpeedUpApplication extends Application {

    private static SpeedUpApplication _instance;

    public static SpeedUpApplication get_instance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
//        LeakCanary.install(this);

        //bugly初始化
//        CrashReport.initCrashReport(getApplicationContext(), getString(R.string.Buyly_AppId), false);


        //        因为buildsdk是>=24,所以调用Uri.fromFile时保错
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder ();
//        StrictMode.setVmPolicy (builder.build ());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            builder.detectFileUriExposure ();
//        }
        initFresco();
    }

    private void initFresco() {
//        Fresco.initialize(this, ConfigConstants.getImagePipelineConfig(this.getApplicationContext()));
    }
}
