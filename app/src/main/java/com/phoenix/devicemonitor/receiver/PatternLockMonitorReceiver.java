package com.phoenix.devicemonitor.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.phoenix.devicemonitor.service.CaptureService;


/**
 * Created by hui1.zheng on 8/27/2015.
 */
public class PatternLockMonitorReceiver extends DeviceAdminReceiver{

    private final String TAG = "MonitorReceiver";


    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

        Log.d(TAG, "Enabled");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Log.d(TAG, "PasswordFailed");

        Intent i = new Intent();
        i.setComponent(new ComponentName("com.phoenix.devicemonitor", "com.phoenix.devicemonitor.service.CaptureService"));
        i.setAction(CaptureService.ACTION_SINGLE_PIC);
        context.startService(i);

    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);

        Log.d(TAG, "PasswordSucceeded");
    }
}
