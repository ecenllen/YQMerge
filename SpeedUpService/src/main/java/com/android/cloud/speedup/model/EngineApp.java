package com.android.cloud.speedup.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yingyongduoduo on 2018/4/11.
 */

public class EngineApp implements Serializable{

    /** 是否展示引擎，相当于总开关*/
    private boolean showYQ;
    /**是否诱导好评*/
    private boolean adYouDao;

    /**单独、hunhe混合展示*/
    private String adShowType;

    /** 什么网络下展示广告*/
    private String network;
    /** 轮询重复时间，毫秒*/
    private int repeatTime;
    /** 间隔多长时间展示广告,分钟*/
    private int intervalTime;

    private int updateTime;

    private int downTimeHour;

    private List<ADInfo> adInfoList;



    private String versionName;
    private int versionCode;
    private String downloadUrl;
    private String yqPackageName;

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public int getDownTimeHour() {
        return downTimeHour;
    }

    public void setDownTimeHour(int downTimeHour) {
        this.downTimeHour = downTimeHour;
    }

    public boolean isAdYouDao() {
        return adYouDao;
    }

    public void setAdYouDao(boolean adYouDao) {
        this.adYouDao = adYouDao;
    }

    public String getAdShowType() {
        return adShowType;
    }

    public void setAdShowType(String adShowType) {
        this.adShowType = adShowType;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public List<ADInfo> getAdInfoList() {
        return adInfoList;
    }

    public void setAdInfoList(List<ADInfo> adInfoList) {
        this.adInfoList = adInfoList;
    }

    public boolean isShowYQ() {
        return showYQ;
    }

    public void setShowYQ(boolean showYQ) {
        this.showYQ = showYQ;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getYqPackageName() {
        return yqPackageName;
    }

    public void setYqPackageName(String yqPackageName) {
        this.yqPackageName = yqPackageName;
    }
}
