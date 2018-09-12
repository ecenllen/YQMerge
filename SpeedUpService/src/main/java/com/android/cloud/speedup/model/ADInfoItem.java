package com.android.cloud.speedup.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yingyongduoduo on 2018/4/11.
 */

public class ADInfoItem implements Parcelable{

    private String type;
    private String appId;
    private String adPlaceId;

    public ADInfoItem(){

    }

    protected ADInfoItem(Parcel in) {
        appId = in.readString();
        adPlaceId = in.readString();
        type = in.readString();
    }

    public static final Creator<ADInfoItem> CREATOR = new Creator<ADInfoItem>() {
        @Override
        public ADInfoItem createFromParcel(Parcel in) {
            return new ADInfoItem(in);
        }

        @Override
        public ADInfoItem[] newArray(int size) {
            return new ADInfoItem[size];
        }
    };

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAdPlaceId() {
        return adPlaceId;
    }

    public void setAdPlaceId(String adPlaceId) {
        this.adPlaceId = adPlaceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appId);
        dest.writeString(adPlaceId);
        dest.writeString(type);
    }
}
