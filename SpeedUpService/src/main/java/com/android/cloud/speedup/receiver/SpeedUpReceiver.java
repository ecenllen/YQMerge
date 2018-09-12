package com.android.cloud.speedup.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.cloud.speedup.SpeedUpService;

public class SpeedUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SpeedUpService.startSpeedUpService(context);
    }
}
