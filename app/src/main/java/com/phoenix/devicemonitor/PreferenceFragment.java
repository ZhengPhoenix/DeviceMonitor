package com.phoenix.devicemonitor;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.phoenix.devicemonitor.receiver.PatternLockMonitorReceiver;
import com.phoenix.devicemonitor.service.MailSender;


public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "PreferenceFragment";

    public Context mContext;
    public static final String SEND_ACCOUNT = "test@test.com";
    public static final String PASSWORD = "password";
    public static final String RECEIVER_ACCOUNT = "sendto";
    public static final String ENABLE_ADMIN = "monitor_enable";

    private String mSendAccount;
    private String mPsw;
    private String mReceiverAccount;

    private ComponentName mAdminReceiver;
    DevicePolicyManager mPolicyManager;

    SharedPreferences preferences;

    Preference mReceiverPre;
    Preference mMonitorEnabled;

    MailSender sender;

    public void PreferenceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        //load preference configure
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mAdminReceiver = new ComponentName(mContext, PatternLockMonitorReceiver.class);
        mPolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);

        sender = new MailSender("342972949@qq.com", "phoenix_zh@foxmail.com", "Subject", "Text Body", "<b>Html Body<b>");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preference_fragment_layout, container, false);

        Preference testBtn = (Preference) findPreference("connection_test");
        testBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "connection test");
                sender.execute();
                return false;
            }
        });

        mSendAccount = SEND_ACCOUNT;
        mPsw = PASSWORD;
        mReceiverAccount = preferences.getString(RECEIVER_ACCOUNT, "");

        mReceiverPre = (Preference) findPreference(RECEIVER_ACCOUNT);
        if (!"".equals(mReceiverAccount) && !"receiving account".equals(mReceiverAccount)) {
            mReceiverPre.setSummary(mReceiverAccount);
        }

        mMonitorEnabled = (Preference) findPreference(ENABLE_ADMIN);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDetach();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged, key:" + key);
        switch (key) {
            case RECEIVER_ACCOUNT:
                mReceiverAccount = sharedPreferences.getString(RECEIVER_ACCOUNT, "");
                mReceiverPre.setSummary(mReceiverAccount);
                break;

            case ENABLE_ADMIN:
                if(sharedPreferences.getBoolean(ENABLE_ADMIN, false)) {
                    activeAdminManager();
                } else {
                    Log.d(TAG, "disable device monitor");
                    mPolicyManager.removeActiveAdmin(mAdminReceiver);
                }
            default:
                break;
        }
    }

    private void activeAdminManager(){
        Log.d(TAG, "active Admin Manager");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getActivity().getString(R.string.admin_warning_descript));

        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "on result, result: " + resultCode);
        if(resultCode == Activity.RESULT_OK) {
            ((TwoStatePreference) mMonitorEnabled).setChecked(true);
        } else if(resultCode == Activity.RESULT_CANCELED) {
            ((TwoStatePreference) mMonitorEnabled).setChecked(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
