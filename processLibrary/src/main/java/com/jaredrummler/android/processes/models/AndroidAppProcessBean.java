package com.jaredrummler.android.processes.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yingyongduoduo on 2018/1/11.
 */

public class AndroidAppProcessBean implements Serializable{
    private List<String> packageList;
    private int flag;// 1为系统应用，2为其他应用

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<String> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }

}
