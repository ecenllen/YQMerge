package com.android.cloud.speedup.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by shanlin on 2018/1/5.
 * private String engineMainClass; // 主函数class
 * private String engineInitMethod; // 初始化函数
 * private String engineStartMethod; // 开始函数
 * private String engineStopMethod; // 结束函数
 * private String engineUninitMethod; // 反初始化函数
 */

public class DexUtil {

    private DexClassLoader dexClassLoader;

    private static DexUtil dexUtil;

    public static DexUtil getInstance() {
        if (dexUtil == null) {
            synchronized (DexUtil.class) {
                if (dexUtil == null) {
                    dexUtil = new DexUtil();
                }
            }
        }
        return dexUtil;
    }

    public void Init(Context context, String dexPath, String engineMainClass, String engineInitMethod) throws Exception {
        File file = new File(dexPath);
        if (!file.exists()) {
            Log.i("DexUtil", "Init apk not exit!");
            return;
        }

        dexClassLoader = new DexClassLoader(dexPath, file.getParent(), null, context.getClassLoader());
        Class<?> clazz = dexClassLoader.loadClass(engineMainClass);
        Method method = clazz.getMethod(engineInitMethod, Context.class);
        if (method != null) {
            method.setAccessible(true);
            method.invoke(null, context);
        }
    }

    public void Unnit(String engineMainClass, String engineUninitMethod) {
        if (dexClassLoader == null) {
            return;
        }
        try {
            Class<?> clazz = dexClassLoader.loadClass(engineMainClass);
            Method method = clazz.getMethod(engineUninitMethod, Void.class);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void Start(String engineMainClass, String engineStartMethod) {
        if (dexClassLoader == null) {
            return;
        }
        try {
            Class<?> clazz = dexClassLoader.loadClass(engineMainClass);
            Method method = clazz.getMethod(engineStartMethod);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void ShowCpAD(Activity activity, ViewGroup viewGroup, ViewGroup appInfo, TextView skipView, String engineMainClass, String engineShowCPADMethod, String userdata) throws Exception {
        if (dexClassLoader == null || TextUtils.isEmpty(engineMainClass) || TextUtils.isEmpty(engineShowCPADMethod)) {
            return;
        }
        Class<?> clazz = dexClassLoader.loadClass(engineMainClass);
        Method method = clazz.getMethod(engineShowCPADMethod, Activity.class, ViewGroup.class, ViewGroup.class, TextView.class, String.class);
        if (method != null) {
            method.setAccessible(true);
            method.invoke(null, activity, viewGroup, appInfo, skipView, userdata);
        }

    }

    public void Stop(String engineMainClass, String engineStopMethod) {
        if (dexClassLoader == null) {
            return;
        }
        try {
            Class<?> clazz = dexClassLoader.loadClass(engineMainClass);
            Method method = clazz.getMethod(engineStopMethod);
            if (method != null) {
                method.setAccessible(true);
                method.invoke(null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
