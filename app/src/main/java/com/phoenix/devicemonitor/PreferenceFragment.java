package com.phoenix.devicemonitor;

import android.app.Activity;
import android.app.FragmentManager;
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
    public static final String KEEP_PICTURE = "keep_picture";
    public static final String TEN_SEC_DELAY = "ten_sec_delay";

    private String mSendAccount;
    private String mPsw;
    private String mReceiverAccount;
    private boolean mKeepPicture;

    private ComponentName mAdminReceiver;
    DevicePolicyManager mPolicyManager;

    SharedPreferences mPreferences;

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
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mAdminReceiver = new ComponentName(mContext, PatternLockMonitorReceiver.class);
        mPolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);

        mSendAccount = SEND_ACCOUNT;
        mPsw = PASSWORD;
        mReceiverAccount = mPreferences.getString(RECEIVER_ACCOUNT, "");
        mKeepPicture = mPreferences.getBoolean(KEEP_PICTURE, false);

        mReceiverPre = (Preference) findPreference(RECEIVER_ACCOUNT);
        if (!"".equals(mReceiverAccount) && !"receiving account".equals(mReceiverAccount)) {
            mReceiverPre.setSummary(mReceiverAccount);
        }

        mMonitorEnabled = (Preference) findPreference(ENABLE_ADMIN);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preference_fragment_layout, container, false);

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
        if(RECEIVER_ACCOUNT.equals(key)) {
            mReceiverAccount = sharedPreferences.getString(RECEIVER_ACCOUNT, "");
            mReceiverPre.setSummary(mReceiverAccount);
        } else if(ENABLE_ADMIN.equals(key)) {
            if(sharedPreferences.getBoolean(ENABLE_ADMIN, false)) {
                activeAdminManager();
            } else {
                Log.d(TAG, "disable device monitor");
                mPolicyManager.removeActiveAdmin(mAdminReceiver);
            }
        } else if(KEEP_PICTURE.equals(key)) {

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

            WarningDialogFragment dialogFragment = new WarningDialogFragment();
            dialogFragment.show(getFragmentManager(), null);
        } else if(resultCode == Activity.RESULT_CANCELED) {
            ((TwoStatePreference) mMonitorEnabled).setChecked(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
