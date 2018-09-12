package com.android.cloud.speedup.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.cloud.speedup.ISpeedUpService;
import com.android.cloud.speedup.ISpeedUpServiceCallback;
import com.android.cloud.speedup.service.MyJobService;
import com.android.cloud.speedup.SpeedUpService;
import com.android.cloud.speedup.utils.ReadProcessPermissionHelp;
import com.android.cloud.speedup.utils.Screen;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class MainActivity extends Activity {

    /**
     * 如果想自定义显示提示语，则打开Intent的时候传这两个参数过来，
     * 读取手机权限弹框
     * 自启动权限
     **/
    public static final String EXTRAL_BOOT_TIPS = "extral_boot_tips";
    public static final String EXTRAL_READ_TIPS = "extral_read_tips";
    public static String openPermissionBootTips;
    public static String openPermissionReadTips;


    private com.android.cloud.speedup.ISpeedUpService iSpeedUpService;
    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Screen.init(MainActivity.this);
        initIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (ReadProcessPermissionHelp.hasReadPermission(MainActivity.this)) {
//            ReadProcessPermissionHelp.goBootPermission(MainActivity.this, openPermissionBootTips);
            SpeedUpService.allReady = true;
            bindSpeedUpService();
            bindMyJobService();
        } else {
            SpeedUpService.allReady = false;
//            ReadProcessPermissionHelp.goReadPermission(this, openPermissionReadTips);
            Intent permissionIntent = new Intent(this, ReadPermissionActivity.class);
            startActivityForResult(permissionIntent, 0);
        }
    }

    private void initIntent() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            openPermissionReadTips = getIntent().getExtras().getString(EXTRAL_READ_TIPS);
            openPermissionBootTips = getIntent().getExtras().getString(EXTRAL_BOOT_TIPS);
        }
    }

    private void bindMyJobService() {
        if (Build.VERSION.SDK_INT >= 21) {
            Intent i3 = new Intent(this, MyJobService.class);
            startService(i3);
        }
    }

    private void bindSpeedUpService() {
        Intent intent = new Intent();
        intent.setAction(SpeedUpService.ACTION_PACKAGENAME_LOCAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent eintent = createExplicitFromImplicitIntent(this, intent);
        if (eintent == null) {
            finish();
//            Toast.makeText(MainActivity.this, "引擎2.0未安装", Toast.LENGTH_SHORT).show();
        } else {
            bindService(new Intent(eintent), speedUpServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
//        if (resolveInfo == null || resolveInfo.size() != 1) {
//            return null;
//        }
        ResolveInfo serviceInfo = null;
        if (resolveInfo == null || resolveInfo.size() == 0)
            return null;
        if (resolveInfo.size() >= 2) {
            for (ResolveInfo info : resolveInfo) {
                if (info.serviceInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                    serviceInfo = info;
                    break;
                }
            }
        } else {
            // Get component info and create ComponentName
            serviceInfo = resolveInfo.get(0);
        }

        if(serviceInfo == null)
            serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    private ServiceConnection speedUpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iSpeedUpService = ISpeedUpService.Stub.asInterface(service);
            isFirstIn = false;
            finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iSpeedUpService = null;
        }
    };

    private ISpeedUpServiceCallback callback = new ISpeedUpServiceCallback.Stub() {
        @Override
        public void onSpeedUpServiceMessage(String json) throws RemoteException {

        }

        @Override
        public void onSpeedUpServiceLessTime(long time) throws RemoteException {

        }

        @Override
        public void onSpeedUpServiceDismissed() throws RemoteException {

        }

        @Override
        public void onSpeedUpServicePresent() throws RemoteException {

        }

        @Override
        public void onSpeedUpServiceError(String message) throws RemoteException {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isFirstIn) {
            //读取应用列表权限
//            if (ReadProcessPermissionHelp.hasReadPermission(MainActivity.this, openPermissionReadTips)) {
////                ReadProcessPermissionHelp.goBootPermission(MainActivity.this, openPermissionReadTips);
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iSpeedUpService != null)
            unbindService(speedUpServiceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1024)
            finish();
    }
}
