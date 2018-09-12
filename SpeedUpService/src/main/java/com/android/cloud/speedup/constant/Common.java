package com.android.cloud.speedup.constant;

/**
 * Created by yingyongduoduo on 2018/4/17.
 */

public class Common {
    /** SP保存第一次安装/打开应用时间*/
    public static final String KEY_FIRST_INSTALL_TIME = "data_time";
    public static final String KEY_FIRST_INSTALL_TIME_B = "data_time_b";

    /** 展示开屏广告，插屏广告,不展示*/
    public static final int TYPE_SHOWKP = 1;
    public static final int TYPE_SHOWCP = 2;
    public static final int TYPE_NO_SHOW = 0;

    /**
     * 间隔多长时间展示广告,毫秒,这里配置的是默认值
     */
    public static final int intervalTime = 5 * 60 * 1000;
    /**
     * 间隔多长时间更新一次数据，重新请求接口
     */
    public static final long updateTime = 1000 * 60 * 2; //两分钟
    /**
     * 轮询重复时间，毫秒
     */
    public static final int repeatTime = 1200;
}
