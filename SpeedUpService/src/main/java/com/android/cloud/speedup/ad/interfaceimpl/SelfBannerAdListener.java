package com.android.cloud.speedup.ad.interfaceimpl;


import com.android.cloud.speedup.ad.bean.ADBean;

/**
 * Created by yuminer on 2017/5/26.
 */
public interface SelfBannerAdListener {
    void onAdClick(ADBean adBean);
    void onAdFailed();
    void onADReceiv(ADBean adBean);
    void onAdCloseClick();
}
