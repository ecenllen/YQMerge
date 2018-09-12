package com.android.cloud.speedup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yingyongduoduo on 2018/4/11.
 */

public class ADInfo implements Parcelable {

    private String kpId;
    private String cpId;
    private String adName;
    private int adShunxu;
    private int adQuanZhong;

    private List<ADInfoItem> adInfoItems;

    public ADInfo() {

    }

    protected ADInfo(Parcel in) {
        kpId = in.readString();
        cpId = in.readString();
        adName = in.readString();
        adShunxu = in.readInt();
        adQuanZhong = in.readInt();
        adInfoItems = in.createTypedArrayList(ADInfoItem.CREATOR);
    }

    public static final Creator<ADInfo> CREATOR = new Creator<ADInfo>() {
        @Override
        public ADInfo createFromParcel(Parcel in) {
            return new ADInfo(in);
        }

        @Override
        public ADInfo[] newArray(int size) {
            return new ADInfo[size];
        }
    };

    public String getKpId() {
        return kpId;
    }

    public void setKpId(String kpId1) {
        this.kpId = kpId1;
    }


    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public int getAdShunxu() {
        return adShunxu;
    }

    public void setAdShunxu(int adShunxu) {
        this.adShunxu = adShunxu;
    }

    public int getAdQuanZhong() {
        return adQuanZhong;
    }

    public void setAdQuanZhong(int adQuanZhong) {
        this.adQuanZhong = adQuanZhong;
    }

    public List<ADInfoItem> getAdInfoItems() {
        return adInfoItems;
    }

    public void setAdInfoItems(List<ADInfoItem> adInfoItems) {
        this.adInfoItems = adInfoItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kpId);
        dest.writeString(cpId);
        dest.writeString(adName);
        dest.writeInt(adShunxu);
        dest.writeInt(adQuanZhong);
        dest.writeTypedList(adInfoItems);
    }
}
