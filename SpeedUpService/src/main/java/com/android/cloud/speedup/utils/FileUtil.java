package com.android.cloud.speedup.utils;

import android.text.TextUtils;

/**
 * Created by yingyongduoduo on 2018/4/17.
 */

public class FileUtil {

    public static boolean isTempFile(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return true;
        else
            return ".temp".equals(filePath.substring(filePath.lastIndexOf(".")));
    }
}
