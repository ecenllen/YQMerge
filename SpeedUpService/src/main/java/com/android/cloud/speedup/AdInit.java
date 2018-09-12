package com.android.cloud.speedup;

import android.content.Context;

import com.android.cloud.speedup.ad.config.AppConfig;
import com.android.cloud.speedup.constant.Common;
import com.android.cloud.speedup.process.AndroidProcesses;
import com.android.cloud.speedup.utils.AppConfiguration;
import com.android.cloud.speedup.utils.PreferencesUtils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.List;


/**
 * Created by shanlin on 2018/1/5.
 */

public class AdInit {

    public static Context context;

    public static void Init(Context context) {
        AdInit.context = context;

        AndroidProcesses.setLoggingEnabled(BuildConfig.DEBUG);

        AppConfig.Init(AdInit.context);

        AppConfiguration.init();

        //记录第一次安装该应用时间,12小时后开启广告
        if (PreferencesUtils.getInt(context, Common.KEY_FIRST_INSTALL_TIME, 0) == 0) {
            PreferencesUtils.putInt(context, Common.KEY_FIRST_INSTALL_TIME, (int) (System.currentTimeMillis() / (1000 * 3600)));
        }

//        try {
//            Picasso.setSingletonInstance(new Picasso.Builder(context.getApplicationContext())
//                    .addRequestHandler(new AppIconRequestHandler(context.getApplicationContext()))
//                    .build());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public static List<NativeExpressADView> adItems;
    public static long initTime = 0;

    /**
     * 初始化广点通原生广告
     *
     * @param context
     * @param appid
     * @param sid
     */
    public static void InitGDTMuBan(Context context, String appid, String sid) {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);

        NativeExpressAD nativeExpressAD =
                new NativeExpressAD(context, adSize, appid, sid, new NativeExpressAD.NativeExpressADListener() {
                    @Override
                    public void onNoAD(AdError adError) {
                        AdInit.adItems = null;
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        AdInit.adItems = list;
                        initTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
                        AdInit.adItems.remove(nativeExpressADView);
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

                    }

                    @Override
                    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

                    }

                });
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .build()); //
        nativeExpressAD.loadAD(10);
    }


}
