package com.android.cloud.speedup.utils;

import android.util.Log;

import com.android.cloud.speedup.BuildConfig;

/**
 * Created by yingyongduoduo on 2018/1/26.
 */

public class LogUtil {

    private static final String TAG = "SpeedUpService";

    public static void e(String msg){
        if(BuildConfig.DEBUG){
            Log.e(TAG, msg+"");
        }
    }

    public static void i(String msg) {
        if(BuildConfig.DEBUG){
            Log.i(TAG, msg+"");
        }
    }

}
