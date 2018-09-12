package com.android.cloud.speedup.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.cloud.speedup.AdInit;
import com.android.cloud.speedup.ad.EngineValue;
import com.android.cloud.speedup.process.AndroidProcesses;
import com.android.cloud.speedup.process.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppConfiguration {
    private static final String TAG = AppConfiguration.class.getSimpleName();
    private static AppConfiguration _instance;

    /**
     * 系统应用，包括自己本身也当成系统应用
     */
    public static List<String> systemPackageName = new ArrayList<>();

    static {
        systemPackageName.add("com.mfsd.vam");
        systemPackageName.add("com.xly.adtest");
        systemPackageName.add("com.android.");
        systemPackageName.add("com.huawei.");
        systemPackageName.add("com.vivo.");
        systemPackageName.add("com.meizu.");
        systemPackageName.add("com.bbk.");
        systemPackageName.add("com.miui.");
        systemPackageName.add("com.svox.pico");
        systemPackageName.add("com.samsung.");
        systemPackageName.add("com.sec.android.");
    }

    private static ExecutorService EXECUTORS_INSTANCE;

    private AppConfiguration() {
        super();
    }

    //    private AndroidAppProcessBean oldProcesses;
    private List<AndroidAppProcess> oldProcessList;
    private String showingPackageName = "";
    private boolean currApp;
    private boolean intervalTime;  //是否达到间隔时间显示
    private boolean showCpAd;

    public boolean isCurrApp() {
        return currApp;
    }

    public boolean isShowCpAd() {
        return showCpAd;
    }

    public String getShowingPackageName() {
        return showingPackageName;
    }

    public static AppConfiguration getInstance() {
        if (_instance == null) {
            _instance = SINGLETON.sAppConfiguration;
        }

        return _instance;
    }

    private static class SINGLETON {
        static AppConfiguration sAppConfiguration = new AppConfiguration();
    }

    private static class getExecutor {

        static ExecutorService EXECUTORS_INSTANCE = Executors.newFixedThreadPool(6);
    }

    //    共用线程池
    public static void runOnThread(Runnable runnable) {
        if (EXECUTORS_INSTANCE == null) {
            EXECUTORS_INSTANCE = getExecutor.EXECUTORS_INSTANCE;
        }
        EXECUTORS_INSTANCE.execute(runnable);
    }

    /**
     * app运行初始，创建app配置
     */
    public static void init() {
        if (_instance == null) {
            // 无存储,则需首次创建配置对象，并存储到sharereference
            _instance = SINGLETON.sAppConfiguration;
        }
    }

    public long currentShowTime;
    public long cpDownTime;

    /**
     * 开始加载正在运行APP
     */
    public boolean startProcessLoader() throws Exception {
        String topPackageName = TaskHelper.getTopActivityApp(AdInit.context);
        if (!TextUtils.isEmpty(topPackageName)) {
            boolean isSystem = isSystemPackageName(topPackageName); //非系统应用
//            currApp = false;
            showCpAd = false;
            if (!isSystem) { //非系统应用
                if (showingPackageName.equals(topPackageName)) { //同一应用
                    currApp = true;
                    if(intervalTime)
                        countDownToCPAD();
                    return false;
                }

                showingPackageName = topPackageName;
                EngineValue.countdownTime = EngineValue.DEFAULT_TIME;

                // 间隔多长时间展示广告,插屏广告时间之后开始计算
                if (cpDownTime >= 0) {
                    if (System.currentTimeMillis() - currentShowTime < EngineValue.intervalTime){
                        intervalTime = false;
                        return false;
                    }
                }
                intervalTime = true;
                cpDownTime -= EngineValue.repeatTime;

            }
            return !isSystem;
        } else {
            List<AndroidAppProcess> processes = getRunningForegroundApp(AdInit.context);
            return isStartNewProcess(processes);//是否开启新的应用：非系统应用
        }
    }


    /**
     * 获取正在运行的前台应用
     *
     * @param context
     * @return
     */
    private List<AndroidAppProcess> getRunningForegroundApp(Context context) {
        List<AndroidAppProcess> processes;
        try {
            processes = AndroidProcesses.getRunningForegroundApps(context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return processes;
    }

    /**
     * 获取所有正在运行应用
     *
     * @return
     */
    private List<AndroidAppProcess> getAllRunningAppProcess() {
        return AndroidProcesses.getRunningAppProcesses();
    }

    /**
     * 是否新打开非系统应用
     *
     * @param newList
     * @return
     */
    private boolean isStartNewProcess(List<AndroidAppProcess> newList) {
        currApp = false;
        showCpAd = false;
        if (oldProcessList == null) {
            oldProcessList = newList;
            return false;
        }
        if (newList != null) {
//                removeKilledApp(newList);
            for (AndroidAppProcess process : newList) {
                String packageName = process.getPackageName();
                if (showingPackageName.equals(packageName)) {
                    currApp = true;
                    break;//是否一直打开同一个应用，不给弹出广告，return false是，return true 否
                }
                if (isSystemPackageName(packageName)) {
                    continue;
                }
                boolean isNew = true;
                for (AndroidAppProcess oldProcess : oldProcessList) {
                    if (oldProcess.getPackageName().equals(packageName)) {//旧进程列表包含，说明是已打开应用
                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    if (process.foreground) { //是否前台应用
                        //打开新的应用，更新应用信息
                        Log.e(TAG, "process = " + packageName);
                        showingPackageName = packageName;
                        EngineValue.countdownTime = EngineValue.DEFAULT_TIME;
                        // 每次都更新旧列表
                        oldProcessList = newList;

                        // 间隔多长时间展示广告,插屏广告时间之后开始计算
                        if (cpDownTime >= 0) {
                            if (System.currentTimeMillis() - currentShowTime < EngineValue.intervalTime)
                                intervalTime = false;
                                return false;
                        }
                        cpDownTime -= EngineValue.repeatTime;
                        intervalTime = true;

                        return true;
                    }
                }
            }
            // 每次都更新旧列表
            oldProcessList = newList;
            if(intervalTime)
                countDownToCPAD();

        }

        return false;
    }

    private void countDownToCPAD() {
        if (EngineValue.countdownTime > 0 && currApp) {
            EngineValue.countdownTime -= EngineValue.repeatTime;
            if (EngineValue.countdownTime == 0) {
                showCpAd = true;
            }
        }
    }


    /**
     * 是否系统应用
     *
     * @param packageName
     * @return
     */
    private boolean isSystemPackageName(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            for (String s : AppConfiguration.systemPackageName) {
                if (packageName.startsWith(s)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

}