package com.android.cloud.speedup.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.cloud.speedup.BuildConfig;
import com.android.cloud.speedup.SpeedUpService;
import com.android.cloud.speedup.utils.KeepLiveUtils;


/**
 * Created by haifeng on 2017/8/25.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("91ySdk", "onStartJob");
        startService();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("91ySdk","onStopJob");
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("91ySdk","onStartCommand");
        try {
            int id = 1;
            JobInfo.Builder builder = new JobInfo.Builder(id,
                    new ComponentName(getPackageName(), MyJobService.class.getName() ));
            builder.setPeriodic(60*1000);  //间隔5分钟毫秒调用onStartJob函数
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); //有网络的时候唤醒
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int ret = jobScheduler.schedule(builder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        startService();
        return Service.START_STICKY;
    }

    public void startService(){
        boolean isOnLineServiceWork = KeepLiveUtils.isServiceWork(this, SpeedUpService.ACTION_PACKAGENAME_LOCAL);
        if(!isOnLineServiceWork){
            SpeedUpService.startSpeedUpService(this);
            if(BuildConfig.DEBUG)
                Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
        }
    }
}
