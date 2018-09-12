package com.android.cloud.speedup.ad.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.cloud.speedup.ad.bean.ADBean;
import com.android.cloud.speedup.ad.bean.ConfigBean;
import com.android.cloud.speedup.ad.bean.PublicConfigBean;
import com.android.cloud.speedup.ad.bean.WXGZHBean;
import com.android.cloud.speedup.ad.util.DownLoaderAPK;
import com.android.cloud.speedup.ad.util.HttpUtil;
import com.android.cloud.speedup.ad.util.PackageUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * 保存软件的配置信息
 * Created by yuminer on 2017/3/16.
 */
public class AppConfig {

    public static String GZHPath;

    public static String versioncode = "";
    public static String Channel = "";
    public static String APPKEY = "";

    private final static String baseURL1 = "https://dev.caibat.com/bizhi1/";
    private final static String baseURL2 = "http://videodata.gz.bcebos.com/bizhi1/";
    private final static String baseURL3 = "http://www.yingyongduoduo.com/bizhi1/";
    private final static String configbaseURL1 = baseURL1 + "%s/";
    private final static String configbaseURL2 = baseURL2 + "%s/";
    private final static String configbaseURL3 = baseURL3 + "%s/";

    public static ConfigBean configBean;
    public static PublicConfigBean publicConfigBean;
    public static List<ADBean> selfadBeans = new ArrayList<ADBean>();
    public static List<WXGZHBean> wxgzhBeans = new ArrayList<WXGZHBean>();

    /**
     * 联网初始化广告配置
     * 在启动页进行初始化
     *
     * @param context
     */
    public static void Init(Context context) {

        initUmengKey(context);
        initConfigJson(context);
        initPublicConfigJson(context);
        initselfadJson(context);
        initwxgzhJson(context);
        InitLocal(context);
    }

    /**
     * 初始化本地参数配置
     * 在Application 进行初始化
     *
     * @param context
     */
    public static void initUmengKey(Context context) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.versioncode = GetVersionCode(context);
            AppConfig.APPKEY = appInfo.metaData.getString("UMENG_APPKEY");
            AppConfig.Channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }



    private static String GetVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(info.versionCode); //获取版本cood
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static void InitLocal(Context context) {

        initConfigBean(context);
//        initVideoBean(context);
        initselfadBeans(context);
        initwxgzhBeans(context);
        //initvideosourceVersion(context);这个没有必要初始化
    }

    public static ConfigBean getConfigBean(String configJson) {
        ConfigBean bean = new ConfigBean();

        try {
            final JSONObject jo = new JSONObject(configJson);

            if (haveKey(jo, "ad_banner_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_banner_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_banner_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_kp_id")) {
                JSONObject jo_ad_kp_id = jo.getJSONObject("ad_kp_id");
                Iterator<String> keys = jo_ad_kp_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_kp_idMap.put(key, jo_ad_kp_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_cp_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_cp_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_cp_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "ad_tp_id")) {
                JSONObject jo_ad_banner_id = jo.getJSONObject("ad_tp_id");
                Iterator<String> keys = jo_ad_banner_id.keys();
                while (keys.hasNext()) { // 只要一个
                    String key = keys.next();
                    bean.ad_tp_idMap.put(key, jo_ad_banner_id.getString(key));
                }
            }
            if (haveKey(jo, "cpuidorurl")) {
                bean.cpuidorurl = jo.getString("cpuidorurl");
            }

            if (haveKey(jo, "channel")) {
                JSONObject jo_channel = jo.getJSONObject("channel");
                if (haveKey(jo_channel, Channel)) {
                    JSONObject jo_channelInfo = jo_channel.getJSONObject(Channel);

                    if (haveKey(jo_channelInfo, "nomeinvchannel")) {
                        bean.nomeinvchannel = jo_channelInfo.getString("nomeinvchannel");
                    }
                    if (haveKey(jo_channelInfo, "nocpuadchannel")) {
                        bean.nocpuadchannel = jo_channelInfo.getString("nocpuadchannel");
                    }
                    if (haveKey(jo_channelInfo, "nofenxiang")) {
                        bean.nofenxiang = jo_channelInfo.getString("nofenxiang");
                    }
                    if (haveKey(jo_channelInfo, "nosearch")) {
                        bean.nosearch = jo_channelInfo.getString("nosearch");
                    }
                    if (haveKey(jo_channelInfo, "nohaoping")) {
                        bean.nohaoping = jo_channelInfo.getString("nohaoping");
                    }
                    if (haveKey(jo_channelInfo, "noadbannerchannel")) {
                        bean.noadbannerchannel = jo_channelInfo.getString("noadbannerchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadkpchannel")) {
                        bean.noadkpchannel = jo_channelInfo.getString("noadkpchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadtpchannel")) {
                        bean.noadtpchannel = jo_channelInfo.getString("noadtpchannel");
                    }
                    if (haveKey(jo_channelInfo, "noadcpchannel")) {
                        bean.noadcpchannel = jo_channelInfo.getString("noadcpchannel");
                    }
                    if (haveKey(jo_channelInfo, "noupdatechannel")) {
                        bean.noupdatechannel = jo_channelInfo.getString("noupdatechannel");
                    }
                    if (haveKey(jo_channelInfo, "noselfadchannel")) {
                        bean.noselfadchannel = jo_channelInfo.getString("noselfadchannel");
                    }
                    if (haveKey(jo_channelInfo, "noaddvideochannel")) {
                        bean.noaddvideochannel = jo_channelInfo.getString("noaddvideochannel");
                    }
                    if (haveKey(jo_channelInfo, "noadVideowebchannel")) {
                        bean.noadVideowebchannel = jo_channelInfo.getString("noadVideowebchannel");
                    }
                    bean.playonwebchannel = jo_channelInfo.optString("playonwebchannel");

                    if (haveKey(jo_channelInfo, "nogzhchannel")) {
                        bean.nogzhchannel = jo_channelInfo.getString("nogzhchannel");
                    }
                    if (haveKey(jo_channelInfo, "bannertype")) {
                        bean.bannertype = jo_channelInfo.getString("bannertype");
                    }
                    if (haveKey(jo_channelInfo, "cptype")) {
                        bean.cptype = jo_channelInfo.getString("cptype");
                    }
                    if (haveKey(jo_channelInfo, "kptype")) {
                        bean.kptype = jo_channelInfo.getString("kptype");
                    }
                    if (haveKey(jo_channelInfo, "tptype")) {
                        bean.tptype = jo_channelInfo.getString("tptype");
                    }

                } else {
                    bean = null;//连channel都没有，这可能是服务器异常
                }
            }
        } catch (Exception e) {
            bean = null;
        }
        return bean;
    }


    public static List<ADBean> getSelfAdBeans(String selfadJson) {
        List<ADBean> beans = new ArrayList<ADBean>();

        try {
            final JSONArray ja = new JSONArray(selfadJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                ADBean bean = new ADBean();
                bean.setAd_name(jo.optString("name"));
                bean.setAd_description(jo.optString("description"));
                bean.setAd_iconurl(jo.optString("iconurl"));
                bean.setAd_iconscal((float) jo.optDouble("iconscal", bean.getAd_iconscal()));
                bean.setAd_thumbnail(jo.optString("thumbnail"));
                bean.setAd_thumbnailscal((float) jo.optDouble("thumbnailscal", bean.getAd_thumbnailscal()));
                bean.setAd_banner(jo.optString("banner"));
                bean.setAd_kp(jo.optString("kp"));
                bean.setAd_apkurl(jo.optString("apkurl"));
                bean.setAd_packagename(jo.optString("packagename"));
                bean.setAd_isConfirm(jo.optBoolean("isConfirm"));
                bean.setAd_type(jo.optInt("type"));
                bean.setAd_versioncode(jo.optInt("versioncode"));
                bean.setAd_platform("ad");
                beans.add(bean);
//                if (haveKey(jo, "displayName") && haveKey(jo, "secondConfirm") && haveKey(jo, "adtype") && haveKey(jo, "scal") && haveKey(jo, "iconthunb1") && haveKey(jo, "url") && haveKey(jo, "packageName")) {
//                    bean.displayName = jo.getString("displayName");
//                    bean.secondConfirm = jo.getString("secondConfirm");
//                    bean.adtype = jo.getString("adtype");
//                    bean.scal = (float) jo.getDouble("scal");
//                    bean.iconthunb1 = jo.getString("iconthunb1");
//                    bean.url = jo.getString("url");
//                    bean.packageName = jo.getString("packageName");
//                    beans.add(bean);
//                }
            }

        } catch (Exception e) {
        }
        return beans;
    }

    private static String getConfigJson(String url) {
        String ConfigJson = "";
        try {
            ConfigJson = HttpUtil.getJson(url);
            ConfigBean bean1 = getConfigBean(ConfigJson);
            if (bean1 == null) {
                ConfigJson = "";

            }
        } catch (Exception ex) {
            ConfigJson = "";
        }
        return ConfigJson;
    }

    public static void initPublicConfigJson(Context context) {
        String ConfigJson = "";
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        ConfigJson = getPubConfigJson(baseURL1 + "publicconfig.json");
        if (ConfigJson.isEmpty()) {
            ConfigJson = getPubConfigJson(baseURL2 + "publicconfig.json");
        }
        if (ConfigJson.isEmpty()) {
            ConfigJson = getPubConfigJson(baseURL3 + "publicconfig.json");
        }
        if (!ConfigJson.isEmpty()) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("publicConfigJson", ConfigJson);
            editor.commit();
        }

        initPublicConfigBean(context);
    }

    public static void initPublicConfigBean(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String ConfigJson = mSettings.getString("publicConfigJson", "");
        try {
            PublicConfigBean bean1 = getpublicConfigBean(ConfigJson);
            publicConfigBean = bean1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getPubConfigJson(String url) {
        String getpubConfigJson = "";
        try {
            getpubConfigJson =
                    new HttpUtil().getJson(url);
            PublicConfigBean bean1 = getpublicConfigBean(getpubConfigJson);
            if (bean1 == null) {
                getpubConfigJson = "";

            }
        } catch (Exception ex) {
            getpubConfigJson = "";
        }
        return getpubConfigJson;
    }

    public static PublicConfigBean getpublicConfigBean(String configJson) {
        PublicConfigBean bean = new PublicConfigBean();

        try {
            final JSONObject jo = new JSONObject(configJson);

            if (haveKey(jo, "videosourceVersion")) {
                bean.videosourceVersion = jo.getString("videosourceVersion");
            }
            if (haveKey(jo, "selfadVersion")) {
                bean.selfadVersion = jo.getString("selfadVersion");
            }
            if (haveKey(jo, "wxgzhversion")) {
                bean.wxgzhversion = jo.getString("wxgzhversion");
            }
            if (haveKey(jo, "goodPinglunVersion")) {
                bean.goodPinglunVersion = jo.getString("goodPinglunVersion");
            }
            if (haveKey(jo, "onlineVideoParseVersion")) {
                bean.onlineVideoParseVersion = jo.getString("onlineVideoParseVersion");
            }
            if (haveKey(jo, "baiduCpuId")) {
                bean.baiduCpuId = jo.getString("baiduCpuId");
            }
            if (haveKey(jo, "qqKey")) {
                bean.qqKey = jo.getString("qqKey");
            }
            if (haveKey(jo, "Information")) {
                bean.Information = jo.getString("Information");
            }
            if (haveKey(jo, "fenxiangInfo")) {
                bean.fenxiangInfo = jo.getString("fenxiangInfo");
            }
        } catch (Exception e) {
            bean = null;
        }
        return bean;
    }

    public static void initConfigJson(Context context) {

//        new OkHttpUtil().startDownloadJson(INDEX_HTML_LOCAL_PATH);

        String ConfigJson = "";
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        ConfigJson = getConfigJson(String.format(configbaseURL1, APPKEY) + "config.json");
        if (ConfigJson.isEmpty()) {
            ConfigJson = getConfigJson(String.format(configbaseURL2, APPKEY) + "config.json");
        }
        if (ConfigJson.isEmpty()) {
            ConfigJson = getConfigJson(String.format(configbaseURL3, APPKEY) + "config.json");
        }
        if (!ConfigJson.isEmpty()) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("ConfigJson", ConfigJson);
            editor.commit();
        }

        initConfigBean(context);
    }


    public static void initConfigBean(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String ConfigJson = mSettings.getString("ConfigJson", "");
        try {
            ConfigBean bean1 = getConfigBean(ConfigJson);
            configBean = bean1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSelfadJson(String url) {
        String SelfadJson = "";
        try {
            SelfadJson = HttpUtil.getJson(url);
            List<ADBean> currentSelfAdBeans = getSelfAdBeans(SelfadJson);
            if (currentSelfAdBeans.size() == 0) {
                SelfadJson = "";
            }
        } catch (IOException e) {
            SelfadJson = "";
        }
        return SelfadJson;
    }

    public static void initselfadJson(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        if (publicConfigBean != null && !"".equals(publicConfigBean.selfadVersion) && !publicConfigBean.selfadVersion.equals(mSettings.getString("selfadVersion", ""))) {//需要更新
            String SelfadJson = getSelfadJson(baseURL1 + "selfad/selfad.json");

            if (SelfadJson.isEmpty()) {
                SelfadJson = getSelfadJson(baseURL2 + "selfad/selfad.json");
            }
            if (SelfadJson.isEmpty()) {
                SelfadJson = getSelfadJson(baseURL3 + "selfad/selfad.json");
            }

            if (!SelfadJson.isEmpty()) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("SelfadJson", SelfadJson);
                editor.putString("selfadVersion", publicConfigBean.selfadVersion);
                editor.apply();
            }

        }
        initselfadBeans(context);
    }

    public static void initselfadBeans(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String SelfadJson = mSettings.getString("SelfadJson", "");
        try {
            List<ADBean> currentSelfAdBeans = getSelfAdBeans(SelfadJson);
            if (currentSelfAdBeans.size() == 0) {
                SelfadJson = "";
            }
            selfadBeans = currentSelfAdBeans;
        } catch (Exception e) {

        }
    }

    private static String getWXGZHJson(String url) {
        String wxgzhJson = "";
        try {
            wxgzhJson = new HttpUtil().getJson(url);
            List<WXGZHBean> currentSelfAdBeans = getWXGZHBeans(wxgzhJson);
            if (currentSelfAdBeans.size() == 0) {
                wxgzhJson = "";
            }
        } catch (IOException e) {
            wxgzhJson = "";
        }
        return wxgzhJson;
    }

    public static void initwxgzhJson(Context context) {
        if (context == null) return;
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        if (publicConfigBean != null && !"".equals(publicConfigBean.wxgzhversion) && !publicConfigBean.wxgzhversion.equals(mSettings.getString("wxgzhversion", ""))) {//需要更新
            String wxgzhJson = getWXGZHJson(baseURL1 + "wxgzh/wxgzh.json");
            if (wxgzhJson.isEmpty()) {
                wxgzhJson = getWXGZHJson(baseURL2 + "wxgzh/wxgzh.json");
            }
            if (wxgzhJson.isEmpty()) {
                wxgzhJson = getWXGZHJson(baseURL3 + "wxgzh/wxgzh.json");
            }

            if (!wxgzhJson.isEmpty()) {
                List<WXGZHBean> currentSelfAdBeans = getWXGZHBeans(wxgzhJson);
                for (WXGZHBean bean : currentSelfAdBeans) {
                    initGZHPic(bean);
                }
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("wxgzhJson", wxgzhJson);
                editor.putString("wxgzhversion", publicConfigBean.wxgzhversion);
                editor.apply();
            }
        }
        String wxgzhJson = mSettings.getString("wxgzhJson", "");
        List<WXGZHBean> currentSelfAdBeans = getWXGZHBeans(wxgzhJson);
        for (WXGZHBean bean : currentSelfAdBeans) {
            // Boolean isSuccess = true;//成功与否不重要，不成功的不用就是
            if (!new File(GZHPath + bean.id + ".jpg").exists()) {//如果文件不存在
                initGZHPic(bean);
            }
        }
        initwxgzhBeans(context);//初始化之后需要判断图片是否存在
    }

    public static List<WXGZHBean> getWXGZHBeans(String wxgzhJson) {
        List<WXGZHBean> beans = new ArrayList<WXGZHBean>();

        try {
            final JSONArray ja = new JSONArray(wxgzhJson);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                WXGZHBean bean = new WXGZHBean();
                if (haveKey(jo, "displayName") && haveKey(jo, "introduction") && haveKey(jo, "url") && haveKey(jo, "id") && haveKey(jo, "thumb") && haveKey(jo, "type")) {
                    bean.displayName = jo.getString("displayName");
                    bean.id = jo.getString("id");
                    bean.type = jo.getString("type");
                    bean.introduction = jo.getString("introduction");
                    bean.thumb = jo.getString("thumb");
                    bean.url = jo.getString("url");
                    if (new File(GZHPath + bean.id + ".jpg").exists()) {
                        bean.isPicExist = true;
                    }
                    beans.add(bean);
                }
            }

        } catch (Exception e) {
        }
        return beans;
    }

    private static void initGZHPic(WXGZHBean bean) {

        try {
            downloadgzhjpg(bean, bean.thumb);
        } catch (Exception ethumb) {//这一步则表示下载失败
            deleteFile(GZHPath + bean.id + ".jpg");

            try {
                downloadgzhjpg(bean, baseURL1 + "wxgzh/" + bean.id + ".jpg");
            } catch (Exception e1) {
                deleteFile(GZHPath + bean.id + ".jpg");
                try {
                    downloadgzhjpg(bean, baseURL2 + "wxgzh/" + bean.id + ".jpg");
                } catch (Exception e2) {
                    deleteFile(GZHPath + bean.id + ".jpg");
                    try {
                        downloadgzhjpg(bean, baseURL3 + "wxgzh/" + bean.id + ".jpg");
                    } catch (Exception e3) {//这一步则表示下载失败
                        // isSuccess = false;
                        deleteFile(GZHPath + bean.id + ".jpg");
                    }
                }
            }
        }
    }

    public static void downloadgzhjpg(WXGZHBean bean, String jpgurl) throws Exception {
        deleteFile(GZHPath + bean.id + ".jpg");// 如果存在就先删除
        URL url = new URL(jpgurl);
        URLConnection con = url.openConnection();
        int contentLength = con.getContentLength();
        InputStream is = con.getInputStream();
        byte[] bs = new byte[1024];
        int len;
        OutputStream os = new FileOutputStream(GZHPath + bean.id + ".jpg");
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        is.close();

    }

    public static void initwxgzhBeans(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        String wxgzhJson = mSettings.getString("wxgzhJson", "");
        try {
            List<WXGZHBean> currentSelfAdBeans = getWXGZHBeans(wxgzhJson);
            if (currentSelfAdBeans.size() == 0) {
                wxgzhJson = "";

            }
            wxgzhBeans = currentSelfAdBeans;
        } catch (Exception e) {

        }
    }


    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {
        }

    }

    private static boolean haveKey(JSONObject jo, String key) {
        return jo.has(key) && !jo.isNull(key);
    }

    private static List<Integer> GetRandomList(int size, int max) {
        Random r = new Random();
        List<Integer> list = new ArrayList<Integer>();
        int i;
        while (list.size() < size) {
            i = r.nextInt(max);
            if (!list.contains(i)) {
                list.add(i);
            }
        }
        Collections.sort(list);
        return list;
    }

    /*
    * 随机取广告，size表示取的数量
    * */
    public static List<ADBean> GetSelfADByCount(Context context, int size, String event_id) {
        List<ADBean> selfADs = new ArrayList<ADBean>();
        List<ADBean> ok_selfadBeans = new ArrayList<ADBean>();
        for (ADBean selfad : selfadBeans) {//过滤掉已经安装了的app
            if (!PackageUtil.isInstallApp(context, selfad.getAd_packagename())) {
                ok_selfadBeans.add(selfad);
            }

        }
        if (size >= ok_selfadBeans.size()) {
            selfADs.addAll(ok_selfadBeans);
        } else {
            //建立一个size大的0-selfadBeans.size()之间不重复的list
            List<Integer> positionList = GetRandomList(size, ok_selfadBeans.size());
            for (int i : positionList) {
                selfADs.add(ok_selfadBeans.get(i));
            }
        }
        for (ADBean bean : selfADs) {
            Map<String, String> map_ekv = new HashMap<String, String>();
            map_ekv.put("show", bean.getAd_name());
            MobclickAgent.onEvent(context, event_id, map_ekv);
        }
        return selfADs;
    }


    public static void openAD(final Context context, final ADBean adbean, String tag) {//如果本条是广告

        Map<String, String> map_ekv = new HashMap<String, String>();
        map_ekv.put("click", adbean.getAd_name());
        MobclickAgent.onEvent(context, tag, map_ekv);


        int type = adbean.getAd_type();
        if (type == 1)//下载
        {
            if (PackageUtil.isInstallApp(context, adbean.getAd_packagename()))//已经安装直接打开
            {
                PackageUtil.startApp(context, adbean.getAd_packagename());
                return;
            }
            if (adbean.isAd_isConfirm()) {
                new AlertDialog.Builder(context).setTitle("确定下载：" + adbean.getAd_name() + "?")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“确认”后的操作
                                if (DownLoaderAPK.getInstance(context).addDownload(adbean)) {
                                    Toast.makeText(context, "开始下载:" + adbean.getAd_name(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, adbean.getAd_name() + " 已经在下载了:", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“返回”后的操作,这里不设置没有任何操作
                            }
                        }).show();
            } else {

                if (DownLoaderAPK.getInstance(context).addDownload(adbean)) {
                    Toast.makeText(context, "开始下载:" + adbean.getAd_name(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, adbean.getAd_name() + " 已经在下载了:", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (type == 2)//打开网页
        {
            if (adbean.getAd_apkurl().contains(".taobao.com"))//是淘宝链接
            {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW"); //
                    String url = "";
                    if (adbean.getAd_apkurl().startsWith("http://")) {
                        url = adbean.getAd_apkurl().replaceFirst("http://", "taobao://");
                    } else {
                        url = adbean.getAd_apkurl().replaceFirst("https://", "taobao://");
                    }
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
                }
            } else if (adbean.getAd_apkurl().contains("item.jd.com/"))//是淘宝链接
            {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW"); //
                    int begin = adbean.getAd_apkurl().indexOf("item.jd.com/") + "item.jd.com/".length();
                    int end = adbean.getAd_apkurl().indexOf(".html");
                    String id = adbean.getAd_apkurl().substring(begin, end);
                    String url = "openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22" + id + "%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D";

                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
                }
            } else {

                PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
            }
        } else if (type == 3)//打开公众号
        {
            WXGZHBean wxgzhbean = new WXGZHBean();
            if (AppConfig.wxgzhBeans != null && AppConfig.wxgzhBeans.size() > 0) {
                wxgzhbean.type = AppConfig.wxgzhBeans.get(0).type;
            } else {
                wxgzhbean.type = "pengyouquan,pengyou,putong";
            }

            wxgzhbean.thumb = adbean.getAd_thumbnail();
            wxgzhbean.displayName = adbean.getAd_name();
            wxgzhbean.id = adbean.getAd_packagename();
            wxgzhbean.url = adbean.getAd_apkurl();
            wxgzhbean.introduction = adbean.getAd_description();
        } else {
            PackageUtil.qidongLiulanqi((Activity) context, adbean.getAd_apkurl());
        }

    }

    public static String getKPType() {
        if (configBean == null){
            return "";
        }
        for (String str : configBean.kptype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)){//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "";

    }

    public static String getCPType() {
        if (configBean == null){
            return "";
        }
        for (String str : configBean.cptype.split(",")) {
            String[] a = str.split(":");
            if (a.length == 2) {
                String versionItem = a[0];
                String adNameItem = a[1];
                if (versioncode.equals(versionItem)){//平台与版本对应了，因为渠道已经选定了
                    return adNameItem;
                }

            }
        }
        return "";

    }

    public static String getCPId1() {
        String cpType = AppConfig.getCPType();//获取开屏广告类型，baidu，gdt，google
        String kp_String = AppConfig.configBean.ad_cp_idMap.get(cpType);
        if (!TextUtils.isEmpty(kp_String)) {
            String[] a = kp_String.split(",");
            if (a.length == 2) {
                String appid = a[0];
                String adplaceid = a[1];
                return appid;
            }
        }
        return "";
    }
    public static String getCPId2() {
        String cpType = AppConfig.getCPType();//获取开屏广告类型，baidu，gdt，google
        String kp_String = AppConfig.configBean.ad_cp_idMap.get(cpType);
        if (!TextUtils.isEmpty(kp_String)) {
            String[] a = kp_String.split(",");
            if (a.length == 2) {
                String appid = a[0];
                String adplaceid = a[1];
                return adplaceid;
            }
        }
        return "";
    }

    public static String getKPId1() {
        String cpType = AppConfig.getCPType();//获取开屏广告类型，baidu，gdt，google
        String kp_String = AppConfig.configBean.ad_kp_idMap.get(cpType);
        if (!TextUtils.isEmpty(kp_String)) {
            String[] a = kp_String.split(",");
            if (a.length == 2) {
                String appid = a[0];
                String adplaceid = a[1];
                return adplaceid;
            }
        }
        return "";
    }
    public static String getKPId2() {
        String cpType = AppConfig.getCPType();//获取开屏广告类型，baidu，gdt，google
        String kp_String = AppConfig.configBean.ad_kp_idMap.get(cpType);
        if (!TextUtils.isEmpty(kp_String)) {
            String[] a = kp_String.split(",");
            if (a.length == 2) {
                String appid = a[0];
                String adplaceid = a[1];
                return adplaceid;
            }
        }
        return "";
    }
}
