package com.phoenix.devicemonitor;

import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.phoenix.camera.CameraTestActivity;
import com.phoenix.devicemonitor.receiver.PatternLockMonitorReceiver;

import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class DeviceMonitorConfigurePanelFragment extends Fragment{

    private final String TAG = "ConfigurePanel";

    private Context mContext;

    private Switch mAdminSwitch;

    private ComponentName mAdminReceiver;
    DevicePolicyManager mPolicyManager;

    public DeviceMonitorConfigurePanelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");


        mContext = getActivity();
        mAdminReceiver = new ComponentName(mContext, PatternLockMonitorReceiver.class);
        mPolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_monitor_configure_panel, container, false);

        mAdminSwitch = (Switch) view.findViewById(R.id.pattern_switch);

        mAdminSwitch.setChecked(isActiveAdmin());

        mAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activeAdminManager();
                } else {
                    mPolicyManager.removeActiveAdmin(mAdminReceiver);
                    Log.d(TAG, "de-active Admin");
                }
            }
        });

        ((Button) view.findViewById(R.id.start_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CameraTestActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void activeAdminManager(){
        Log.d(TAG, "active Admin Manager");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getActivity().getString(R.string.admin_warning_descript));

        getActivity().startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "on Result, resultCode: " + resultCode);
    }

    private boolean isActiveAdmin(){
        if (mPolicyManager != null) {
            return mPolicyManager.isAdminActive(mAdminReceiver);
        }

        return false;
    }
}


