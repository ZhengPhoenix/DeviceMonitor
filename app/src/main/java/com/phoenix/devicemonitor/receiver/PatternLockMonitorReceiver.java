package com.phoenix.devicemonitor.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.phoenix.devicemonitor.PreferenceFragment;
import com.phoenix.devicemonitor.service.CaptureService;


/**
 * Created by hui1.zheng on 8/27/2015.
 */
public class PatternLockMonitorReceiver extends DeviceAdminReceiver{

    private final String TAG = "MonitorReceiver";

    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";


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
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "receive with action :" + action);
        super.onReceive(context, intent);
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(preferences.getBoolean(PreferenceFragment.TEN_SEC_DELAY, false)) {
            Intent i = new Intent();
            i.setComponent(new ComponentName("com.phoenix.devicemonitor", "com.phoenix.devicemonitor.service.CaptureService"));
            i.setAction(CaptureService.BLOCK_SEND_DELAY);
            context.startService(i);
        }

        Log.d(TAG, "PasswordSucceeded");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d(TAG, "Disabled");
    }
}
