package com.android.cloud.speedup.ad;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by yingyongduoduo on 2018/4/17.
 */

public class EngineValue {

    /**
     * 安装应用后，多少小时后开启广告
     */
    public static int downTimeHour = 12;
    /**
     * 轮询重复时间，毫秒
     */
    public static int repeatTime = 1500;
    /**
     * 间隔多长时间展示广告,毫秒
     */
    public static int intervalTime = 5 * 60 * 1000;

    /**
     * 间隔多长时间更新一次数据，重新请求接口
     */
    public static long updateTime = 1000 * 3600 * 12;
    public static long currentUpdateTime;

    public static int DEFAULT_TIME = repeatTime * 6;// 默认倒计时：8秒, DEFAULT_REPEAT_TIME的整倍数
    public static int countdownTime = DEFAULT_TIME;  //插屏广告倒时时

    /**
     * 什么网络下展示广告
     */
    public static String network = "";
    public static int NETWORK_TYPE_SHOW = 3;
    public static final int NETWORK_TYPE_SHOW_WIFI = 1;
    public static final int NETWORK_TYPE_SHOW_4G = 2;
    public static final int NETWORK_TYPE_SHOW_ALL = 3;

    /**
     * 单独、hunhe混合展示
     */
    public static String adShowType = "dandu";

//    /**广告顺序, 后台定义参数："gdt,baidu,google"*/
//    public static String adShunXu1  = "1";
//    public static String adShunXu2  = "2";
//    public static String adShunXu3  = "3";
//    public static String adShunXu4  = "4";
//    public static String adShunXu5  = "5";
//    public static String adShunXu6  = "6";  暂时不用这个，

    public static String adId1 = "1101152570";
    public static String adId2 = "8863364436303842593";

    /**
     * 开屏ID和插屏ID集合
     **/
    public static Map<String, Map<String, String>> kp_idMap = new HashMap<>();
    public static Map<String, Map<String, String>> cp_idMap = new HashMap<>();
    public static Map<String, Map<String, String>> mb_idMap = new HashMap<>();
    /**
     * 广告顺序集合加进来
     */
    public static Map<Integer, String> ad_ShunXu_Map = new HashMap<>();
    public static int shunxu_position = 1;
    /**
     * 权重,有序集合
     */
    public static Map<String, Integer> ad_QuanZhong_Map = new HashMap<>();
    public static int ad_QuanZhong_Total = 100;

}
