package com.android.cloud.speedup.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/1 17:01.
 */
public class Screen {

    private static int width;
    private static int height;

    public static void init(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager ().getDefaultDisplay ().getMetrics (metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    /**
     * @return 屏幕宽度 in pixel
     */
    public static int getWidth () {
        return width;
    }

    /**
     * @return 屏幕高度 in pixel
     */
    public static int getHeight () {
        return height;
    }

    @Override
    public String toString () {
        return "Screen{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
