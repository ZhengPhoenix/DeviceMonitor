package com.phoenix.devicemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "PreferenceFragment";

    public Context mContext;
    private static final String SEND_ACCOUNT = "sendaccount";
    private static final String PASSWORD = "password";
    private static final String RECEIVER_ACCOUNT = "sendto";

    private String mSendAccount;
    private String mPsw;
    private String mReceiverAccount;

    SharedPreferences preferences;
    Preference mSendPre;
    Preference mPassword;
    Preference mReceiverPre;

    Button mTestBtn;

    public void PreferenceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        //load preference configure
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

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
                return false;
            }
        });

        mSendAccount = preferences.getString(SEND_ACCOUNT, "");
        mPsw = preferences.getString(PASSWORD, "");
        mReceiverAccount = preferences.getString(RECEIVER_ACCOUNT, "");

        mSendPre = (Preference) findPreference(SEND_ACCOUNT);
        mReceiverPre = (Preference) findPreference(RECEIVER_ACCOUNT);
        if (!"".equals(mSendAccount) && !"sending account".equals(mSendAccount)) {
            mSendPre.setSummary(mSendAccount);
        }
        if (!"".equals(mReceiverAccount) && !"receiving account".equals(mReceiverAccount)) {
            mReceiverPre.setSummary(mReceiverAccount);
        }

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
            case SEND_ACCOUNT:
                mSendAccount = sharedPreferences.getString(SEND_ACCOUNT, "");
                mSendPre.setSummary(mSendAccount);
                break;
            case PASSWORD:

                break;
            case RECEIVER_ACCOUNT:
                mReceiverAccount = sharedPreferences.getString(RECEIVER_ACCOUNT, "");
                mReceiverPre.setSummary(mReceiverAccount);
                break;

            default:
                break;
        }
    }
}
