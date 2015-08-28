package com.phoenix.devicemonitor.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);

        Log.d(TAG, "PasswordSucceeded");
    }
}
