package com.android.cloud.speedup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.cloud.speedup.activity.SpeedUpActivity;
import com.android.cloud.speedup.ad.EngineValue;
import com.android.cloud.speedup.constant.Common;
import com.android.cloud.speedup.model.ADInfo;
import com.android.cloud.speedup.model.ADInfoItem;
import com.android.cloud.speedup.model.EngineApp;
import com.android.cloud.speedup.model.EngineInfo;
import com.android.cloud.speedup.utils.AppConfiguration;
import com.android.cloud.speedup.utils.GetSystemInfoUtils;
import com.android.cloud.speedup.utils.GetVersionCodeUtils;
import com.android.cloud.speedup.utils.HttpUtil;
import com.android.cloud.speedup.utils.NetworkUtils;
import com.android.cloud.speedup.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class SpeedUpService extends Service {

    public static boolean allReady = false;

    private boolean isHasError;

    private static HandlerThread thread;
    private static Handler mHandler;

    public static String ACTION_PACKAGENAME_LOCAL = "com.android.cloud.speedup.SpeedUpService";

    private static final String TAG = SpeedUpService.class.getSimpleName();
    private static RemoteCallbackList<ISpeedUpServiceCallback> remoteCallbackList = new RemoteCallbackList<>();
    private static Executor executor = Executors.newCachedThreadPool();

    private AtomicReference<EngineInfo> atomicReferenceJson = new AtomicReference<>();

    private MessengerHandler messengerHandler;


    public SpeedUpService() {
        messengerHandler = new MessengerHandler(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return speedUpServiceBind;
    }

    private class MessengerHandler extends Handler {
        WeakReference<SpeedUpService> reference;

        public MessengerHandler(SpeedUpService service) {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SpeedUpService speedUpService = reference.get();
            if (speedUpService == null) return;
            speedUpService.startHandler();
        }
    }

    ISpeedUpService.Stub speedUpServiceBind = new ISpeedUpService.Stub() {

        /**
         * 启动引擎2.0
         *
         * @param json 启动参数
         */
        @Override
        public void startSpeedUpService(String json) throws RemoteException {
//             callback(json);
            Log.i("startSpeedUpService", json + "");
            postSpeedUpMessage(allReady + "," + GetVersionCodeUtils.getVersionName(getApplicationContext()));
        }

        @Override
        public void speedUpServiceLog(String log) throws RemoteException {

        }

        @Override
        public void speedUpError(String message) throws RemoteException {
            postSpeedUpError(message);
        }

        @Override
        public void speedUpPresent() throws RemoteException {
            postSpeedUpPresent();
        }

        @Override
        public void speedUpDismissed() throws RemoteException {
            postSpeedUpDismissed();
        }

        @Override
        public void registerISpeedUpServiceCallback(ISpeedUpServiceCallback callback) throws RemoteException {
            remoteCallbackList.register(callback);
        }

        @Override
        public void unregisterISpeedUpServiceCallback(ISpeedUpServiceCallback callback) throws RemoteException {
            remoteCallbackList.unregister(callback);
        }
    };

    private void postSpeedUpDismissed() throws RemoteException {
        int count = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            remoteCallbackList.getBroadcastItem(i).onSpeedUpServiceDismissed();
        }
        remoteCallbackList.finishBroadcast();
    }

    private void postSpeedUpMessage(String msg) throws RemoteException {
        int count = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            remoteCallbackList.getBroadcastItem(i).onSpeedUpServiceMessage(msg);
        }
        remoteCallbackList.finishBroadcast();
    }

    private void postSpeedUpPresent() throws RemoteException {
        int count = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            remoteCallbackList.getBroadcastItem(i).onSpeedUpServicePresent();
        }
        remoteCallbackList.finishBroadcast();
    }

    private void postSpeedUpError(String message) throws RemoteException {
        int count = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            remoteCallbackList.getBroadcastItem(i).onSpeedUpServiceError(message);
        }
        remoteCallbackList.finishBroadcast();
    }

    private void showAD(String json, String packageName) {
        showAD(json, packageName, null);
    }

    private void showAD(String json, String packageName, HashMap<String, String> map) {
        Intent intent = new Intent(this, SpeedUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SpeedUpActivity.EXTRA_AD_MESSAGE, (Parcelable) atomicReferenceJson.get());
        intent.putExtra(SpeedUpActivity.EXTRA_USERDATA, json);
        intent.putExtra(SpeedUpActivity.EXTRA_PACKAGE_NAME, packageName);
        if (map != null)
            intent.putExtra(SpeedUpActivity.EXTRA_PARAMS, map);
        startActivity(intent);
    }


    /**
     * 开启本地服务
     *
     * @param context
     */
    public static void startSpeedUpService(Context context) {
        Intent intent = new Intent(context, SpeedUpService.class);
        intent.setAction(ACTION_PACKAGENAME_LOCAL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else
            context.startService(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        getAd();
    }

    private void initSelfAd() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                AdInit.Init(getApplicationContext());
            }
        });
    }

    private void getAd() {
        initSelfAd();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    String json = HttpUtil.HTTPGet("https://dev.caibat.com/xly/engine/2.0/adcontrolinfo/get.action?imei=" + GetSystemInfoUtils.getImeiOrMeid(getApplicationContext()));
//                    String json = HttpUtil.HTTPGet("http://192.168.137.1:8080/xly/engine/2.0/adcontrolinfo/get.action?imei=" + GetSystemInfoUtils.getImeiOrMeid(getApplicationContext())
                    String json = HttpUtil.HTTPGet("http://47.106.9.254/xly/engine/2.0/adcontrolinfo/get.action?imei=" + GetSystemInfoUtils.getImeiOrMeid(getApplicationContext())
                            + "&packageName=" + getPackageName());
                    JSONObject object = new JSONObject(json);
                    JSONObject data = object.optJSONObject("data");
                    if (data != null) {
                        EngineApp info = new EngineApp();
                        /**
                         * "id":1,
                         "downTimeHour":0,
                         "repeatTime":1500,
                         "intervalTime":120,
                         "adShowType":"hunhe",
                         "network":"all",
                         "updateTime":3600*12,
                         * */
                        info.setDownTimeHour(data.optInt("downTimeHour"));
                        info.setRepeatTime(data.getInt("repeatTime"));
                        info.setIntervalTime(data.getInt("intervalTime"));
                        info.setAdShowType(data.getString("adShowType"));
                        info.setNetwork(data.getString("network"));
                        info.setUpdateTime(data.getInt("updateTime"));
                        info.setYqPackageName(data.getString("yqPackageName"));

                        if (!TextUtils.isEmpty(info.getYqPackageName())) {
                            boolean hasPackage = false;
                            for (String name : AppConfiguration.systemPackageName) {
                                if (name.equalsIgnoreCase(info.getYqPackageName())) {
                                    hasPackage = true;
                                    break;
                                }
                            }
                            if (!hasPackage) {
                                AppConfiguration.systemPackageName.add(info.getYqPackageName());
                            }
                        }

                        JSONArray adInfoList = data.optJSONArray("adInfoList");
                        if (adInfoList != null) {
                            List<ADInfo> infoList = new ArrayList<>();
                            for (int i = 0; i < adInfoList.length(); i++) {
                                JSONObject infoObj = adInfoList.getJSONObject(i);
                                if (infoObj != null) {
                                    ADInfo adInfo = new ADInfo();
                                    /**
                                     * "id":1,
                                     "adName":"gdt",
                                     "adShunxu":1,
                                     "adQuanZhong":50,*/
                                    adInfo.setAdName(infoObj.optString("adName"));
                                    adInfo.setAdShunxu(infoObj.optInt("adShunxu"));
                                    adInfo.setAdQuanZhong(infoObj.optInt("adQuanZhong"));
                                    JSONArray adInfoItemList = infoObj.optJSONArray("adInfoItems");
                                    if (adInfoItemList != null) {
                                        List<ADInfoItem> adInfoItems = new ArrayList<>();
                                        for (int j = 0; j < adInfoItemList.length(); j++) {
                                            JSONObject item = adInfoItemList.getJSONObject(j);
                                            if (item != null) {
                                                /**"id":1,
                                                 "kpId":"1101152570",
                                                 "cpId":"1101152570",
                                                 "idNo":1,
                                                 "new":false */
                                                ADInfoItem adInfoItem = new ADInfoItem();
                                                adInfoItem.setAppId(item.optString("appId"));
                                                adInfoItem.setAdPlaceId(item.optString("adPlaceId"));
                                                adInfoItem.setType(item.optString("type"));
                                                adInfoItems.add(adInfoItem);
                                            }

                                        }
                                        adInfo.setAdInfoItems(adInfoItems);
                                    }

                                    infoList.add(adInfo);
                                }
                            }
                            info.setAdInfoList(infoList);
                        }
                        dataProcess(info);
                        EngineValue.currentUpdateTime = System.currentTimeMillis();
                        isHasError = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isHasError = true;

                }


            }
        });
    }

    @Override
    public void onDestroy() {
//        presenter.onDestroy();
        super.onDestroy();
        startSpeedUpService(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startHandler();
        return Service.START_STICKY;
    }


    private void dataProcess(EngineApp engineApp) {
        if (engineApp == null) return;
        //可以做其他数据处理,广告控制参数赋值
        EngineValue.intervalTime = engineApp.getIntervalTime() == 0 ?
                Common.intervalTime : engineApp.getIntervalTime() * 1000; //间隔多长时间展示广告，2分钟，毫秒单位,后台是秒
        EngineValue.updateTime = engineApp.getUpdateTime() == 0 ?
                Common.updateTime : engineApp.getUpdateTime() * 1000;//间隔多长时间更新数据，12个钟，毫秒单位,后台是秒
        EngineValue.repeatTime = engineApp.getIntervalTime() == 0 ?
                Common.repeatTime : engineApp.getRepeatTime(); //重复轮询时间，1500毫秒，毫秒单位
        EngineValue.DEFAULT_TIME = EngineValue.repeatTime * 6;
        EngineValue.network = engineApp.getNetwork(); //什么网络展示广告，4G/WIFI/ALL
        EngineValue.adShowType = engineApp.getAdShowType();//单独展示广告，或混合展示
        if (TextUtils.isEmpty(EngineValue.adShowType))
            EngineValue.adShowType = "dandu";
        EngineValue.downTimeHour = engineApp.getDownTimeHour();

        if ("4G".equalsIgnoreCase(EngineValue.network)) {
            EngineValue.NETWORK_TYPE_SHOW = EngineValue.NETWORK_TYPE_SHOW_4G;
        } else if ("WIFI".equalsIgnoreCase(EngineValue.network)) {
            EngineValue.NETWORK_TYPE_SHOW = EngineValue.NETWORK_TYPE_SHOW_WIFI;
        } else {
            EngineValue.NETWORK_TYPE_SHOW = EngineValue.NETWORK_TYPE_SHOW_ALL;
        }

        List<ADInfo> infos = engineApp.getAdInfoList();
        if (infos != null) {
            int quanZhong = 0;
            for (int i = 0; i < infos.size(); i++) {
                ADInfo adInfo = infos.get(i);
                EngineValue.ad_ShunXu_Map.put(infos.get(i).getAdShunxu(), infos.get(i).getAdName());
                EngineValue.ad_QuanZhong_Map.put(infos.get(i).getAdName(), infos.get(i).getAdQuanZhong());
                quanZhong += infos.get(i).getAdQuanZhong();

                List<ADInfoItem> adInfoItems = infos.get(i).getAdInfoItems();

                Map<String, String> kpIdMap = new HashMap<>();
                Map<String, String> cpIdMap = new HashMap<>();
                Map<String, String> mbIdMap = new HashMap<>(); //广点通模板ID
                if (adInfoItems != null) {
                    for (ADInfoItem infoItem : adInfoItems) {
                        if ("kp".equalsIgnoreCase(infoItem.getType())) {
                            kpIdMap.put("1", infoItem.getAppId());
                            kpIdMap.put("2", infoItem.getAdPlaceId());
                        } else if ("cp".equalsIgnoreCase(infoItem.getType())) {
                            cpIdMap.put("1", infoItem.getAppId());
                            cpIdMap.put("2", infoItem.getAdPlaceId());
                        } else if ("mb".equalsIgnoreCase(infoItem.getType())) {
                            mbIdMap.put("1", infoItem.getAppId());
                            mbIdMap.put("2", infoItem.getAdPlaceId());
                        }
                    }
                }
                EngineValue.kp_idMap.put(adInfo.getAdName(), kpIdMap);
                EngineValue.cp_idMap.put(adInfo.getAdName(), cpIdMap);
                EngineValue.mb_idMap.put(adInfo.getAdName(), mbIdMap);
            }
            if (quanZhong > 0)
                EngineValue.ad_QuanZhong_Total = quanZhong;

            AdInit.InitGDTMuBan(getApplicationContext(), EngineValue.mb_idMap.get("gdt").get("1"), EngineValue.mb_idMap.get("gdt").get("2"));
        }
        messengerHandler.sendEmptyMessage(1);
    }

    private void startHandler() {
        if (thread == null) {
            thread = new HandlerThread("Android");
        }
        if (!thread.isAlive())
            thread.start();

        if (mHandler == null) {
            mHandler = new Handler(thread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case Common.TYPE_SHOWKP:
                            showKP();
                            AppConfiguration.getInstance().currentShowTime = System.currentTimeMillis();
                            AppConfiguration.getInstance().cpDownTime = EngineValue.DEFAULT_TIME;

                            if (System.currentTimeMillis() - EngineValue.currentUpdateTime > EngineValue.updateTime)//请求接口，更新一下数据
                                getAd();
                            break;
                        case Common.TYPE_SHOWCP:
                            showCP();

                            if (System.currentTimeMillis() - EngineValue.currentUpdateTime > EngineValue.updateTime)//请求接口，更新一下数据
                                getAd();
                            break;

                    }
                }
            };

            Runnable run = new Runnable() {
                @Override
                public void run() {

                    int showType = networkIsCanDo();

                    mHandler.obtainMessage(showType).sendToTarget();

                    mHandler.postDelayed(this, EngineValue.repeatTime);
                }
            };
            mHandler.postDelayed(run, EngineValue.repeatTime);
        }
    }

    /**
     * 判断当前网络是否满足条件
     *
     * @return
     */
    private int networkIsCanDo() {

        // 全部网络展示，则不进去
        if (EngineValue.NETWORK_TYPE_SHOW != EngineValue.NETWORK_TYPE_SHOW_ALL) {

            //判断当前连接网络类型
            int netWorkType = NetworkUtils.getNetWorkType(SpeedUpApplication.get_instance().getApplicationContext());
            switch (EngineValue.NETWORK_TYPE_SHOW) {
                case EngineValue.NETWORK_TYPE_SHOW_4G: //后台设置4G显示广告
                    if (netWorkType != NetworkUtils.NETWORK_3G && netWorkType != NetworkUtils.NETWORK_4G)
                        return Common.TYPE_NO_SHOW;
                    break;
                case EngineValue.NETWORK_TYPE_SHOW_WIFI://后台设置WIFI显示广告
                    if (netWorkType != NetworkUtils.NETWORK_WIFI)
                        return Common.TYPE_NO_SHOW;
                    break;
                case EngineValue.NETWORK_TYPE_SHOW_ALL:
                    break;
                default:
            }
        }


        //当前网络满足
        return startGetTopActivity();
    }

    /**
     * 开始遍历获取栈顶ACTIVITY
     *
     * @return
     */
    private int startGetTopActivity() {
        //12小时后方可展示广告
        if (isCanShowAD()) {
            try {
                if (AppConfiguration.getInstance().startProcessLoader()) {
                    return Common.TYPE_SHOWKP;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Common.TYPE_NO_SHOW;
            }
            if (AppConfiguration.getInstance().isShowCpAd()) {
                return Common.TYPE_SHOWCP;
            }
        }
        return Common.TYPE_NO_SHOW;

    }

    /**
     * 自安装应用起，12小时后方可显示广告
     * downTimeHour 设置时间值
     *
     * @return 是否可以开启广告
     */
    private boolean isCanShowAD() {
        if (!PreferencesUtils.getBoolean(getApplicationContext(), Common.KEY_FIRST_INSTALL_TIME_B, false)) {
            int firstInstallTime = PreferencesUtils.getInt(getApplicationContext(), Common.KEY_FIRST_INSTALL_TIME, 0);
            if (firstInstallTime > 0) {
                if ((int) (System.currentTimeMillis() / (1000 * 3600)) - firstInstallTime >= EngineValue.downTimeHour) {
                    PreferencesUtils.putBoolean(getApplicationContext(), Common.KEY_FIRST_INSTALL_TIME_B, true);
                    return true;
                }
            } else {
                PreferencesUtils.putInt(getApplicationContext(), Common.KEY_FIRST_INSTALL_TIME, (int) (System.currentTimeMillis() / (1000 * 3600)));
                return false;
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean stopService(Intent name) {
        startSpeedUpService(getApplicationContext());
        return super.stopService(name);
    }

    public void showKP() {
        if (isHasError) {
            getAd();
        } else {
            try {
                showAD(SpeedUpActivity.KP_AD, AppConfiguration.getInstance().getShowingPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void showCP() {
        if (isHasError) {
            getAd();
        } else {
            try {
                showAD(SpeedUpActivity.CP_AD, AppConfiguration.getInstance().getShowingPackageName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
