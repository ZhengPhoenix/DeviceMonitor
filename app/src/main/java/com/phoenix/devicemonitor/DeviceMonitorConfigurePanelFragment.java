package com.phoenix.devicemonitor;

import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * A placeholder fragment containing a simple view.
 */
public class DeviceMonitorConfigurePanelFragment extends Fragment{

    private Switch mAdminSwitch;

    public DeviceMonitorConfigurePanelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_monitor_configure_panel, container, false);

        mAdminSwitch = (Switch) view.findViewById(R.id.pattern_switch);

        mAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    activeAdminManager();
                }
            }
        });

        return view;
    }

    private void activeAdminManager(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        startActivityForResult(intent, 0);
    }
}


