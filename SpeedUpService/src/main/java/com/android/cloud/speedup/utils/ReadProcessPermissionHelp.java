package com.android.cloud.speedup.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.android.cloud.speedup.R;
import com.android.cloud.speedup.wiget.OpenPermissionDialog;


/**
 * Created by yingyongduoduo on 2018/1/30.
 */

public class ReadProcessPermissionHelp {

    public static boolean hasReadPermission(final Activity context) {
        boolean granted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//是21以及以后的android版本
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
            granted = mode == AppOpsManager.MODE_ALLOWED;
            if (granted) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void goReadPermission(final Activity context, String tips) {
        //                new AlertDialog.Builder(context).setTitle("使用本功能需要权限")
//                        .setMessage("\t\t使用本功能必须申请[访问使用记录权限]权限，请点击确定在弹出的界面选择我们的应用，点击进入之后选择允许!")
//                        .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                String msg = String.format("1.找到【%s】\n2.打开对应开关!", context.getResources().getString(R.string.app_name));
//                                PermissionHelper.ShowHelper(context, "使用帮助", msg);
//                                Intent intentGetPermission = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                                intentGetPermission.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivityForResult(intentGetPermission, MainActivity.REQUEST_CODE_READ_PERMISSION);
//
//                            }
//                        }).setCancelable(false).show();

//                if(dialog != null) {
//                    dialog.dismiss();
//                    dialog = null;
//                }
        OpenPermissionDialog dialog = new OpenPermissionDialog(context).setImageView1Res(R.drawable.permission_read1).setImageView2Res(R.drawable.permission_read2)
                .setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentGetPermission = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        intentGetPermission.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentGetPermission);
                    }
                });
        if (TextUtils.isEmpty(tips)) {
            dialog.setTitleRes(R.string.string_permission_read);
        } else {
            dialog.setTitleRes(tips);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
    }


    public static boolean goBootPermission(final Activity context) {
        return goBootPermission(context, null, null);
    }

    public static boolean goBootPermission(final Activity context, String tips, final View.OnClickListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            OpenPermissionDialog dialog = new OpenPermissionDialog(context).setImageView1Res(R.drawable.permission_boot1).setImageView2Res(R.drawable.permission_boot2)
                    .setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobileInfoUtils.jumpStartInterface(context);
                            if(listener != null)
                                listener.onClick(v);
                        }
                    });
            if (TextUtils.isEmpty(tips)) {
                dialog.setTitleRes(R.string.string_permission_boot);
            } else {
                dialog.setTitleRes(tips);
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();
        }
        return true;
    }
}
