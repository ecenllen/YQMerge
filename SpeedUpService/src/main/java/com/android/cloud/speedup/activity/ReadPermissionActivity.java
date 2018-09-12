package com.android.cloud.speedup.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.cloud.speedup.R;
import com.android.cloud.speedup.SpeedUpService;
import com.android.cloud.speedup.utils.ReadProcessPermissionHelp;

public class ReadPermissionActivity extends Activity implements View.OnClickListener {

    private RelativeLayout readPhone;
    private RelativeLayout backMain;
    private RelativeLayout autoStart;
    private ImageView ivReadPhone;
    private ImageView ivAutoStart;
    private TextView tvTopTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_permission);

        init();
        initView();
        initListener();
    }

    private void init() {
        readPhone = findViewById(R.id.rl_read_phone);
        backMain = findViewById(R.id.rl_back_main);
        autoStart = findViewById(R.id.rl_auto_start);
        ivReadPhone = findViewById(R.id.iv_read_phone);
        ivAutoStart = findViewById(R.id.iv_auto_start);
        tvTopTips = findViewById(R.id.tv_tips);
    }

    private void initView() {
        tvTopTips.setText(TextUtils.isEmpty(MainActivity.openPermissionReadTips) ? "需要开启以下权限！" : MainActivity.openPermissionReadTips);
    }

    private void initListener() {
        readPhone.setOnClickListener(this);
        autoStart.setOnClickListener(this);
        backMain.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ReadProcessPermissionHelp.hasReadPermission(ReadPermissionActivity.this)) {
            setHasRead(true);
        } else {
            setHasRead(false);
        }
        setHasAuto(isHasAuto);
    }

    private boolean isHasAuto = false;

    private void setHasRead(boolean hasRead) {
        SpeedUpService.allReady = hasRead;
        if (hasRead) { //已授权
            readPhone.setClickable(false);
            backMain.setEnabled(true);
            backMain.setClickable(true);
            ivReadPhone.setImageResource(R.mipmap.checked);
        } else { // 未授权
            readPhone.setClickable(true);
            backMain.setEnabled(false);
            backMain.setClickable(false);
            ivReadPhone.setImageResource(R.mipmap.cancel);
        }
    }
    private void setHasAuto(boolean hasAuto) {
        ivAutoStart.setImageResource(hasAuto ? R.mipmap.checked : R.mipmap.cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_read_phone: //查看使用情况
                ReadProcessPermissionHelp.goReadPermission(this, MainActivity.openPermissionReadTips);

                break;
            case R.id.rl_auto_start: //开机自启动
                ReadProcessPermissionHelp.goBootPermission(this, MainActivity.openPermissionBootTips, ReadPermissionActivity.this);
                break;

            case R.id.rl_back_main:
                backDialog("提示", "确保已添加 [开机自启动] 权限，否则软件将无法正常运行！ ", "去添加", "已添加");
                break;

            case R.id.open_service: //点击[开机启动]授权按钮
                isHasAuto = true;//只要点击过授权开机启动，默认为已开启自启动权限
                break;

            default:
        }
    }

    @Override
    public void onBackPressed() {
//        if (!isHasRead) {
//            backDialog("提示", "确保已添加 [读取应用列表]、[开机自启动] 权限，确定软件正常运行！ ", "去添加", "已添加");
//        } else {
        if(!SpeedUpService.allReady) {
            backDialog("提示", "确保已添加 [读取应用列表] 权限，否则软件将无法正常运行！ ", "去添加", "已添加");
        } else
            backDialog("提示", "确保已添加 [开机自启动] 权限，否则软件将无法正常运行！ ", "去添加", "已添加");

//        }


    }

    private void backDialog(String title, String message, String NeutralButton, String negativeButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setResult(1024);
                        finish();
                    }
                })
                .setCancelable(true);
//        if(!isHasAuto || !SpeedUpService.allReady) {
            builder.setNeutralButton(NeutralButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!SpeedUpService.allReady) {
                        ReadProcessPermissionHelp.goReadPermission(ReadPermissionActivity.this, MainActivity.openPermissionReadTips);
                    } else {
                        ReadProcessPermissionHelp.goBootPermission(ReadPermissionActivity.this, MainActivity.openPermissionBootTips, ReadPermissionActivity.this);
                    }
                    dialog.dismiss();
                }
            });
//        }
        builder.show();
    }
}
