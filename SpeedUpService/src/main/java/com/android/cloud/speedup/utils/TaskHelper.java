package com.android.cloud.speedup.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * Created by yuminer on 2017/8/23.
 */

public class TaskHelper {


    public static int size;

    /**
     * 获取进程数的方法
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            size = getTopApp(context);
        } else {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获取进程的集合
            runningAppProcesses = activityManager.getRunningAppProcesses();
            size = runningAppProcesses.size();

        }
        return size;

    }

    /**
     * 6.0获取进程数
     *
     * @param context
     * @return
     */
    private static int getTopApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);

                String topActivity = "";
                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                        topActivity = stats.get(j).getPackageName();

                    }

                }
                return stats.size();
            }
        }
        return 0;
    }

    /**
     * 获取栈顶的包名
     */

    public static String getTopActivityApp(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                if (m != null) {
                    long now = System.currentTimeMillis();
                    //获取60秒之内的应用数据
                    List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);


                    String topActivity = "";

                    //取得最近运行的一个app，即当前运行的app
                    if ((stats != null) && (!stats.isEmpty())) {
                        int j = 0;
                        for (int i = 0; i < stats.size(); i++) {
                            if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                                j = i;
                            }
                        }
                        topActivity = stats.get(j).getPackageName();
                        return topActivity;
                    }

                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
                // 最近使用的任务栈
                if (taskInfos != null && taskInfos.size() > 0)
                    return taskInfos.get(0).topActivity.getPackageName();
//					packname = taskInfo.topActivity.getYqPackageName();
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    /**
     * 获取可用内存的数据大小 ，单位为byte
     *
     * @param context
     * @return
     */
    public static long getAvailSpace(Context context) {
        //获取activityManager
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //构建可用内存对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();


        activityManager.getMemoryInfo(memoryInfo);

        long availMem = memoryInfo.availMem;
        return availMem;
    }

    public static long getTotalSpace(Context context) {
        //获取activityManager
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //构建可用内存对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.totalMem;
    }

    /**
     * 获取全部的内存空间
     *
     * @return
     */
    public static long getTotalSpace() {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("proc/meminfo");

            bufferedReader = new BufferedReader(fileReader);

            String lineOne = bufferedReader.readLine();

            char[] chars = lineOne.toCharArray();
            StringBuffer stringBuffer = new StringBuffer();
            for (char aChar : chars) {
                if (aChar >= '0' && aChar <= '9') {
                    stringBuffer.append(aChar);
                }
            }
            return Long.parseLong(stringBuffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (fileReader != null && bufferedReader != null) {
                try {
                    bufferedReader.close();
                    fileReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

}
