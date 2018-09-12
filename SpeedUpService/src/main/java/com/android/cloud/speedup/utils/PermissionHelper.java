package com.android.cloud.speedup.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cloud.speedup.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by yuminer on 2017/8/26.
 */

public class PermissionHelper {
    public static void ShowHelper(Activity context, String title, String msg) {

        //Inflater意思是充气
//LayoutInflater这个类用来实例化XML文件到其相应的视图对象的布局
        Toast toast = new Toast(context);
        LayoutInflater inflater = context.getLayoutInflater();
//通过制定XML文件及布局ID来填充一个视图对象
        View layout = View.inflate(context, R.layout.toastview, null);


        TextView tv_title = (TextView) layout.findViewById(R.id.tv_title);
//设置标题
        ViewGroup.LayoutParams params = tv_title.getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        params.width = dm.widthPixels;
        tv_title.setText(title);
        tv_title.setLayoutParams(params);
        TextView tv_msg = (TextView) layout.findViewById(R.id.tv_msg);
        tv_msg.setLayoutParams(params);
//设置内容
        tv_msg.setText(msg);

        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void gotoPermission(Context context) {
        String brand = Build.BRAND;
        if ("huawei".equals(brand.toLowerCase())) {
            gotoHuaweiPermission(context);
        } else if ("xiaomi".equals(brand.toLowerCase())) {
            gotoMiuiPermission(context);
        } else if ("meizu".equals(brand.toLowerCase())) {
            gotoMeizuPermission(context);
        } else if ("vivo".equals(brand.toLowerCase())) {
            gotoVivoPermission(context);
        } else if ("oppo".equals(brand.toLowerCase())) {
            gotoOppoPermission(context);
        } else if ("suoni".equals(brand.toLowerCase())) {
            gotoSuoniPermission(context);
        } else if ("360".equals(brand.toLowerCase())) {
            goto360Permission(context);
        } else if ("lg".equals(brand.toLowerCase())) {
            gotoLGPermission(context);
        } else if ("leshi".equals(brand.toLowerCase())) {
            gotoLeSHIPermission(context);
        } else {
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    //获取MIUI系统版本
    public static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception ex) {

            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("MIUI:" + line);
        return line;
    }

    /**
     * 跳转到miui的权限管理页面
     */
    public static void gotoMiuiPermission(Context context) {
//        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//        i.setComponent(componentName);
//        i.putExtra("extra_pkgname", context.getYqPackageName());
//        try {
//            context.startActivity(i);
//        } catch (Exception e) {
//            e.printStackTrace();
//           // context.startActivity(getAppDetailSettingIntent(context));
//        }
        if ("V5".equalsIgnoreCase(getMiuiVersion())) {
            try {
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                context.startActivity(intent);
            } catch (Exception ex) {
                context.startActivity(getAppDetailSettingIntent(context));
            }
        } else if ("V6".equalsIgnoreCase(getMiuiVersion()) || "V7".equalsIgnoreCase(getMiuiVersion())) {

            try {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
            } catch (Exception ex2) {
                context.startActivity(getAppDetailSettingIntent(context));
            }
        } else if ("V8".equalsIgnoreCase(getMiuiVersion())) {

            try {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
            } catch (Exception ex2) {
                context.startActivity(getAppDetailSettingIntent(context));
            }
        } else {

            context.startActivity(getAppDetailSettingIntent(context));
        }


    }

    /**
     * 跳转到vivo的权限管理页面
     */
    public static void gotoVivoPermission(Context context) {

        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");//华为权限管理

            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }


    }

    /**
     * 跳转到魅族的权限管理系统
     */
    public static void gotoMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 跳转到suoni的权限管理系统
     */
    public static void gotoSuoniPermission(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 跳转到OPPO的权限管理系统
     */
    public static void gotoOppoPermission(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        //    com.oppo.safe/.permission_boot1.PermissionAppAllPermissionActivity
        ComponentName comp = new ComponentName("com.oppo.safe", "com.oppo.safe.permission_boot1.PermissionSettingsActivity");
//        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission_boot1.PermissionManagerActivity");
        intent.setComponent(comp);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 华为的权限管理页面
     */
    public static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * LG的权限管理页面
     */
    public static void gotoLGPermission(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * 乐视手机权限管理界面
     */
    public static void gotoLeSHIPermission(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * 360手机权限管理界面
     */
    public static void goto360Permission(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }
}
