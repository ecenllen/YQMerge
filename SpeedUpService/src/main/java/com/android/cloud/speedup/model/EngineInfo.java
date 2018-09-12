package com.android.cloud.speedup.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shanlin on 2018/1/6.
 */

public class EngineInfo implements Parcelable{
    private int engineType = 99999; // appType
    private String engineName; // 引擎名字
    private String engineUrl; // 下载链接
    private String enginePackage; // 引擎包名
    private String engineVersion; // 版本
    private String engineMainClass; // 主函数class
    private String engineInitMethod; // 初始化函数
    private String engineStartMethod; // 开始函数
    private String engineStopMethod; // 结束函数
    private String engineUninitMethod; // 反初始化函数
    private String engineShowCPADMethod; // 显示插屏广告函数

    public int getEngineType() {
        return engineType;
    }

    public EngineInfo setEngineType(int engineType) {
        this.engineType = engineType;
        return this;
    }

    public String getEngineName() {
        return engineName;
    }

    public EngineInfo setEngineName(String engineName) {
        this.engineName = engineName;
        return this;
    }

    public String getEngineUrl() {
        return engineUrl;
    }

    public EngineInfo setEngineUrl(String engineUrl) {
        this.engineUrl = engineUrl;
        return this;
    }

    public String getEnginePackage() {
        return enginePackage;
    }

    public EngineInfo setEnginePackage(String enginePackage) {
        this.enginePackage = enginePackage;
        return this;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public EngineInfo setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
        return this;
    }

    public String getEngineMainClass() {
        return engineMainClass;
    }

    public EngineInfo setEngineMainClass(String engineMainClass) {
        this.engineMainClass = engineMainClass;
        return this;
    }

    public String getEngineInitMethod() {
        return engineInitMethod;
    }

    public EngineInfo setEngineInitMethod(String engineInitMethod) {
        this.engineInitMethod = engineInitMethod;
        return this;
    }

    public String getEngineStartMethod() {
        return engineStartMethod;
    }

    public EngineInfo setEngineStartMethod(String engineStartMethod) {
        this.engineStartMethod = engineStartMethod;
        return this;
    }

    public String getEngineStopMethod() {
        return engineStopMethod;
    }

    public EngineInfo setEngineStopMethod(String engineStopMethod) {
        this.engineStopMethod = engineStopMethod;
        return this;
    }

    public String getEngineUninitMethod() {
        return engineUninitMethod;
    }

    public EngineInfo setEngineUninitMethod(String engineUninitMethod) {
        this.engineUninitMethod = engineUninitMethod;
        return this;
    }

    public String getEngineShowCPADMethod() {
        return engineShowCPADMethod;
    }

    public EngineInfo setEngineShowCPADMethod(String engineShowCPADMethod) {
        this.engineShowCPADMethod = engineShowCPADMethod;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.engineType);
        dest.writeString(this.engineName);
        dest.writeString(this.engineUrl);
        dest.writeString(this.enginePackage);
        dest.writeString(this.engineVersion);
        dest.writeString(this.engineMainClass);
        dest.writeString(this.engineInitMethod);
        dest.writeString(this.engineStartMethod);
        dest.writeString(this.engineStopMethod);
        dest.writeString(this.engineUninitMethod);
        dest.writeString(this.engineShowCPADMethod);
    }

    public EngineInfo() {
    }

    protected EngineInfo(Parcel in) {
        this.engineType = in.readInt();
        this.engineName = in.readString();
        this.engineUrl = in.readString();
        this.enginePackage = in.readString();
        this.engineVersion = in.readString();
        this.engineMainClass = in.readString();
        this.engineInitMethod = in.readString();
        this.engineStartMethod = in.readString();
        this.engineStopMethod = in.readString();
        this.engineUninitMethod = in.readString();
        this.engineShowCPADMethod = in.readString();
    }

    public static final Creator<EngineInfo> CREATOR = new Creator<EngineInfo>() {
        @Override
        public EngineInfo createFromParcel(Parcel source) {
            return new EngineInfo(source);
        }

        @Override
        public EngineInfo[] newArray(int size) {
            return new EngineInfo[size];
        }
    };

    @Override
    public String toString() {
        return "EngineInfo{" +
                "engineType=" + engineType +
                ", engineName='" + engineName + '\'' +
                ", engineUrl='" + engineUrl + '\'' +
                ", enginePackage='" + enginePackage + '\'' +
                ", engineVersion='" + engineVersion + '\'' +
                ", engineMainClass='" + engineMainClass + '\'' +
                ", engineInitMethod='" + engineInitMethod + '\'' +
                ", engineStartMethod='" + engineStartMethod + '\'' +
                ", engineStopMethod='" + engineStopMethod + '\'' +
                ", engineUninitMethod='" + engineUninitMethod + '\'' +
                ", engineShowCPADMethod='" + engineShowCPADMethod + '\'' +
                '}';
    }
}
