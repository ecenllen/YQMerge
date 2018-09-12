package com.android.cloud.speedup.ad;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.android.cloud.speedup.AdInit;
import com.android.cloud.speedup.ad.bean.ADBean;
import com.android.cloud.speedup.ad.dialog.SelfCPDialog;
import com.android.cloud.speedup.ad.interfaceimpl.KPAdListener;
import com.android.cloud.speedup.ad.interfaceimpl.SelfBannerAdListener;
import com.android.cloud.speedup.ad.interfaceimpl.SelfKPAdListener;
import com.android.cloud.speedup.ad.view.SelfKPView;
import com.android.cloud.speedup.utils.LogUtil;
import com.android.cloud.speedup.utils.Screen;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.interstitial.InterstitialADListener;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class ADControl {

    public static void ShowKp(Context context, ViewGroup adsParent, final KPAdListener kpAdListener) throws Exception {


        String kpType = "gdt"; //给个默认值，广点通广告
        if (EngineValue.adShowType.equalsIgnoreCase("dandu")) { //单独展示广告
            kpType = EngineValue.ad_ShunXu_Map.get(EngineValue.shunxu_position);  //Key是根据后台定义的顺序添加进来的，
        } else if (EngineValue.adShowType.equalsIgnoreCase("hunhe")) { //混合展示广告
            kpType = adTypeFromQuanZhong();
        }

        if (!TextUtils.isEmpty(kpType)) {
            if ("baidu".equals(kpType)) {
//                ShowSelfKP(context, adsParent, kpAdListener);
                ShowBaiduKP(context, adsParent, kpAdListener, EngineValue.kp_idMap.get(kpType).get("1"));
            } else if ("gdt".equals(kpType)) {
                if (AdInit.adItems == null || AdInit.adItems.size() == 0 || System.currentTimeMillis() - AdInit.initTime > 45 * 60 * 1000) {
                    AdInit.InitGDTMuBan(context, EngineValue.mb_idMap.get(kpType).get("1"), EngineValue.mb_idMap.get(kpType).get("2"));
                }
                ShowGDTKP(context, adsParent, kpAdListener, EngineValue.kp_idMap.get(kpType).get("1"), EngineValue.kp_idMap.get(kpType).get("2"));
            } else if ("self".equalsIgnoreCase(kpType)) {
                ShowSelfKP(context, adsParent, kpAdListener);
            } else
                kpAdListener.onAdFailed("其他不支持广告类型" + kpType);
        } else {
            ShowSelfKP(context, adsParent, kpAdListener);
        }
    }

    private static void ShowGDTKP(final Context context, final ViewGroup adsParent, final KPAdListener kpAdListener, final String appid, final String adplaceid) {
        SplashADListener listener = new SplashADListener() {
            @Override
            public void onADDismissed() {
                if (kpAdListener != null)
                    kpAdListener.onAdDismissed();
            }

            @Override
            public void onNoAD(AdError adError) {
                if (EngineValue.adShowType.equalsIgnoreCase("dandu")) { //单独展示广告
                    try {
                        ShowGDTMuBanKP(context, adsParent, kpAdListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (kpAdListener != null)
                            kpAdListener.onAdFailed(e.getMessage());
                    }
                } else {
                    ShowSelfKP(context, adsParent, kpAdListener);
                }

            }

            @Override
            public void onADPresent() {
                if (kpAdListener != null)
                    kpAdListener.onAdPresent();
            }

            @Override
            public void onADClicked() {
                if (kpAdListener != null)
                    kpAdListener.onAdClick();
            }

            @Override
            public void onADTick(long l) {

            }
        };
        new SplashAD((Activity) context, adsParent, appid, adplaceid, listener);
    }

    private static void ShowGDTMuBanKP(final Context context, final ViewGroup adsParent, final KPAdListener kpAdListener) throws Exception {
        if (AdInit.adItems != null && AdInit.adItems.size() > 0) {
            int index = new Random(System.currentTimeMillis()).nextInt(AdInit.adItems.size());
            NativeExpressADView adItem1 = AdInit.adItems.get(index);
            index = (index + 1) % AdInit.adItems.size();
            NativeExpressADView adItem2 = AdInit.adItems.get(index);
            ViewGroup parent = (ViewGroup) adItem1.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
            parent = (ViewGroup) adItem2.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.height = adsParent.getMeasuredHeight() / 2;
            layoutParams.width = Screen.getWidth();

            adItem1.setLayoutParams(layoutParams);
            adItem2.setLayoutParams(layoutParams);

            adsParent.addView(adItem1);
            adsParent.addView(adItem2);

            adItem1.render();
            adItem2.render();
            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    if (isdismiss)//在别的地方关闭的
//                        cancel();
//                    else
//                        tv_close.setText(millisUntilFinished / 1000 + "");
                }

                @Override
                public void onFinish() {
                    if (kpAdListener != null)
                        kpAdListener.onAdDismissed();

                }
            }.start();

        } else { //相当于调用失败，这里仅仅是单独展示广告方式，不存在混合
            EngineValue.shunxu_position += 1;
            if (EngineValue.shunxu_position > EngineValue.ad_ShunXu_Map.size()) {
                EngineValue.shunxu_position = 1;
                ShowSelfKP(context, adsParent, kpAdListener);
            } else {
                ShowKp(context, adsParent, kpAdListener);
            }
        }
    }

    private static void ShowBaiduKP(final Context context, final ViewGroup adsParent, final KPAdListener kpAdListener, String appid) {
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                if (kpAdListener != null)
                    kpAdListener.onAdDismissed();
            }

            @Override
            public void onAdFailed(String adError) {
                if (EngineValue.adShowType.equalsIgnoreCase("dandu")) { //单独展示广告
                    EngineValue.shunxu_position += 1;
                    if (EngineValue.shunxu_position > EngineValue.ad_ShunXu_Map.size()) {
                        EngineValue.shunxu_position = 1;
                        ShowSelfKP(context, adsParent, kpAdListener);
                    } else {
                        try {
                            ShowKp(context, adsParent, kpAdListener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    ShowSelfKP(context, adsParent, kpAdListener);
                }

            }

            @Override
            public void onAdPresent() {
                if (kpAdListener != null)
                    kpAdListener.onAdPresent();
            }

            @Override
            public void onAdClick() {
                // 设置开屏可接受点击时，该回调可用
                if (kpAdListener != null)
                    kpAdListener.onAdClick();
            }
        };
        new SplashAd(context, adsParent, listener, appid, true);
    }

    public static void ShowSelfKP(final Context context, ViewGroup adsParent, final KPAdListener kpAdListener) {

        SelfKPAdListener listener = new SelfKPAdListener() {
            @Override
            public void onAdDismissed(ADBean bean) {//广告展示完毕
                if (kpAdListener != null)
                    kpAdListener.onAdDismissed();
            }

            @Override
            public void onAdFailed(ADBean bean) {//广告获取失败
                if (kpAdListener != null)
                    kpAdListener.onAdFailed("");
            }

            @Override
            public void onAdPresent(ADBean bean) {//广告开始展示
                if (kpAdListener != null)
                    kpAdListener.onAdPresent();
            }

            @Override
            public void onAdClick(ADBean bean) {//广告被点击
                if (kpAdListener != null)
                    kpAdListener.onAdClick();
            }
        };
        SelfKPView selfKPView = new SelfKPView(context);
        selfKPView.setADListener(listener);
        adsParent.removeAllViews();
        adsParent.addView(selfKPView);
    }

    /**
     * 根据权重获取广告类型
     *
     * @return 广告类型
     */
    private static String adTypeFromQuanZhong() {
        String adType = "gdt";//给个默认值，广点通广告
        Random random = new Random();
        int tempRandom = random.nextInt(EngineValue.ad_QuanZhong_Total);//0 - total 随机数
        Iterator<Map.Entry<String, Integer>> iterator = EngineValue.ad_QuanZhong_Map.entrySet().iterator();
        int weiSum = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            int weight = next.getValue();
            weiSum += weight;
            if (tempRandom < weiSum) {
                adType = next.getKey();
                break;
            }
        }
        LogUtil.e("kptype=" + adType + ", random=" + tempRandom);
        return adType;
    }

    /**
     * 展示插屏广告
     *
     * @param context
     */
    public static void ShowCp(Context context) throws Exception {
        String cpType = "gdt"; //给个默认值，广点通广告
        if (EngineValue.adShowType.equalsIgnoreCase("dandu")) { //单独展示广告
            cpType = EngineValue.ad_ShunXu_Map.get(1);  //Key是根据后台定义的顺序添加进来的，
        } else if (EngineValue.adShowType.equalsIgnoreCase("hunhe")) { //混合展示广告
            cpType = adTypeFromQuanZhong();
        }
        LogUtil.e("cpType=" + cpType);
        if (!TextUtils.isEmpty(cpType)) {
            if ("baidu".equals(cpType)) {
//                ShowSelfCP(context);
                ShowBaiduCP(context, EngineValue.cp_idMap.get(cpType).get("1"));
            } else if ("gdt".equals(cpType)) {
                ShowGDTCP(context, EngineValue.cp_idMap.get(cpType).get("1"), EngineValue.cp_idMap.get(cpType).get("2"));
            } else if ("self".equalsIgnoreCase(cpType)) {
                ShowSelfCP(context);
            } else
                ShowSelfCP(context);
        } else {
            ShowSelfCP(context);
        }
    }

    public static void ShowGDTCP(final Context context, String appid, String adplaceid) {
//        SplashAd.setAppSid(context, appid);// 其中的debug需改为您的APPSID
        if(TextUtils.isEmpty(appid) || TextUtils.isEmpty(adplaceid)) {
            ((Activity) context).finish();
            return;
        }
        final InterstitialAD interAd = new InterstitialAD((Activity) context, appid, adplaceid);
        interAd.setADListener(new InterstitialADListener() {
            @Override
            public void onADReceive() {
                interAd.show();
            }

            @Override
            public void onNoAD(AdError adError) {
                ((Activity) context).finish();
            }


            @Override
            public void onADOpened() {

            }

            @Override
            public void onADExposure() {

            }

            @Override
            public void onADClicked() {

            }

            @Override
            public void onADLeftApplication() {

            }

            @Override
            public void onADClosed() {
                ((Activity) context).finish();
            }
        });
        interAd.loadAD();
    }

    private static void ShowBaiduCP(final Context context, String id) {
        String adPlaceId = "2403633"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        final InterstitialAd interAd = new InterstitialAd(context, id);
        interAd.setListener(new InterstitialAdListener() {

            @Override
            public void onAdClick(InterstitialAd arg0) {
                Log.i("InterstitialAd", "onAdClick");
            }

            @Override
            public void onAdDismissed() {
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }

            @Override
            public void onAdFailed(String arg0) {
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }

            @Override
            public void onAdPresent() {

            }

            @Override
            public void onAdReady() {
                interAd.showAd((Activity) context);
            }

        });
        interAd.loadAd();
    }

    private static void ShowSelfCP(final Context context) {

        if (context == null)
            return;
        SelfCPDialog sfCP = new SelfCPDialog(context);
        sfCP.setADListener(new SelfBannerAdListener() {
            @Override
            public void onAdClick(ADBean adBean) {
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }

            @Override
            public void onAdFailed() {
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }

            @Override
            public void onADReceiv(ADBean adBean) {

            }

            @Override
            public void onAdCloseClick() {
                if (context instanceof Activity)
                    ((Activity) context).finish();
            }
        });
        sfCP.show();

    }

}
