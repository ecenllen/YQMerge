package com.android.cloud.speedup.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.cloud.speedup.ISpeedUpService;
import com.android.cloud.speedup.R;
import com.android.cloud.speedup.ad.ADControl;
import com.android.cloud.speedup.ad.EngineValue;
import com.android.cloud.speedup.ad.interfaceimpl.KPAdListener;
import com.android.cloud.speedup.model.EngineInfo;
import com.android.cloud.speedup.utils.AppConfiguration;
import com.android.cloud.speedup.utils.AppIconHandle;
import com.android.cloud.speedup.utils.LogUtil;
import com.android.cloud.speedup.utils.Utils;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpeedUpActivity extends Activity {


    private static final String ACTION_SPEEDUP_SERVICE = "com.android.cloud.speedup.SpeedUpService";
    public static final String EXTRA_USERDATA = "EXTRA_USERDATA";
    public static final String EXTRA_AD_MESSAGE = "EXTRA_AD_MESSAGE";
    public static final String EXTRA_PACKAGE_NAME = "Extra_PackageName";
    public static final String EXTRA_LISTENER = "Extra_Listener";
    public static final String EXTRA_PARAMS = "Extra_Params";

    private static final String TAG = "AD";
    public static final String CP_AD = "cpAd";
    public static final String KP_AD = "kpAd";
    public static final String CUSTOM_AD = "customAd";
    private static final String SCHEME_PNAME = "pname";

    private ISpeedUpService iSpeedUpService;

    private ViewGroup container;
    private ViewGroup appInfoContainer;
    private RelativeLayout baseContainer;
    private TextView skipView;
    private ImageView splashHolder;
    private LinearLayout appInfo;
    private ImageView appIcon;
    private TextView appName;
    EngineInfo info;
    String userdata;
    HashMap<String, String> adMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || getIntent().getExtras() == null) {
            finish();
            return;
        }
        info = getIntent().getExtras().getParcelable(EXTRA_AD_MESSAGE);
        userdata = getIntent().getExtras().getString(EXTRA_USERDATA);
        adMap = (HashMap<String, String>) getIntent().getExtras().getSerializable(EXTRA_PARAMS);
        if (KP_AD.equals(userdata)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_speed_up);
        initView();

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else
            showAD();
//            bindSpeedUpService();
    }

    private void initView() {
        baseContainer = findViewById(R.id.rl_content);
        container = (ViewGroup) this.findViewById(R.id.splash_container);
        appInfoContainer = (ViewGroup) this.findViewById(R.id.app_info);
        splashHolder = this.findViewById(R.id.splash_holder);
        skipView = (TextView) findViewById(R.id.skip_view);
        appInfo = findViewById(R.id.app_info);
        appName = findViewById(R.id.tv_app_name);
        appIcon = findViewById(R.id.iv_app_icon);

    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            showAD();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }


    private void showAD() {
        try {
            ShowCPAD(SpeedUpActivity.this, container, appInfoContainer, skipView, userdata);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }

    /**
     * 广点通广告，后台配置
     *
     * @param activity         引擎的activity
     * @param group            展示广告容器
     * @param skipView         跳过按钮
     * @param appInfoContainer 应用信息父容器
     * @param adType           广告类型：开屏广告，插屏广告
     */
    public void ShowCPAD(Activity activity, ViewGroup group, final ViewGroup appInfoContainer, final TextView skipView, String adType) throws Exception {

        //开屏广告
        if (KP_AD.equals(adType)) {
            initAppInfoView(AppConfiguration.getInstance().getShowingPackageName());
            ADControl.ShowKp(activity, group, kpAdListener);
        } else if (CP_AD.equals(adType)) { // 插屏广告
            baseContainer.removeAllViews();
            ADControl.ShowCp(this);
        }
    }

    /**
     * 设置打开应用图标，名称，版本信息
     * @param showingPackageName
     */
    private void initAppInfoView(String showingPackageName) {
        appInfo.setVisibility(View.VISIBLE);
        appName.setText(Utils.getName(this, showingPackageName));
        try {
            Glide.with(this)
                    //                                .load(Uri.parse(SCHEME_PNAME + ":" + showingPackageName))
                    .load(new AppIconHandle(this).getFullResIcon(showingPackageName))
                    .into(appIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    KPAdListener kpAdListener = new KPAdListener() {
        @Override
        public void onAdPresent() {
            EngineValue.shunxu_position = 1;
        }

        @Override
        public void onAdDismissed() {
            finish();
        }

        @Override
        public void onAdFailed(String var1) {
            LogUtil.e(var1);
            finish();
        }

        @Override
        public void onAdClick() {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

}
