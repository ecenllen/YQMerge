package com.android.cloud.speedup.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by yingyongduoduo on 2018/1/26.
 */

public class AppIconHandle {

    private Bitmap defaultAppIcon;
    private final PackageManager pm;
    private final int dpi;

    public AppIconHandle(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        dpi = am.getLauncherLargeIconDensity();
        pm = context.getPackageManager();
    }

    public Bitmap getFullResIcon(String packageName) throws PackageManager.NameNotFoundException {
        return getFullResIcon(pm.getApplicationInfo(packageName, 0));
    }

    private Bitmap getFullResIcon(ApplicationInfo info) {
        try {
            Resources resources = pm.getResourcesForApplication(info.packageName);
            if (resources != null) {
                int iconId = info.icon;
                if (iconId != 0) {
                    return getFullResIcon(resources, iconId);
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return getFullResDefaultActivityIcon();
    }

    private Bitmap getFullResIcon(Resources resources, int iconId) {
        final Drawable drawable;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                drawable = resources.getDrawableForDensity(iconId, dpi, null);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                drawable = resources.getDrawableForDensity(iconId, dpi);
            } else {
                drawable = resources.getDrawable(iconId);
            }
        } catch (Resources.NotFoundException e) {
            return getFullResDefaultActivityIcon();
        }
        return Utils.drawableToBitmap(drawable);
    }

    private Bitmap getFullResDefaultActivityIcon() {
        if (defaultAppIcon == null) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                drawable = Resources.getSystem().getDrawableForDensity(
                        android.R.mipmap.sym_def_app_icon, dpi);
            } else {
                drawable = Resources.getSystem().getDrawable(
                        android.R.drawable.sym_def_app_icon);
            }
            defaultAppIcon = Utils.drawableToBitmap(drawable);
        }
        return defaultAppIcon;
    }
}
