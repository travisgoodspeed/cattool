package com.kk4vcz.goodspeedscattool.ui.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.kk4vcz.codeplug.Main;
import com.kk4vcz.goodspeedscattool.R;

import java.util.Set;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Load preferences from XML.
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("Settings", "Device doesn't support bluetooth!");
        }else{
            // We could harass the user to enable it, but I think that's rude so I won't.

            /*
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
             */

            ListPreference btDevicesList = (ListPreference) findPreference("btdevices");

            if(bluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                //We don't search for devices, but rather expect that devices are already paired.
                if (pairedDevices.size() > 0) {
                    CharSequence[] entries=new CharSequence[pairedDevices.size()];
                    CharSequence[] entryvalues=new CharSequence[pairedDevices.size()];

                    int i=0;
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        Log.v("Settings", String.format("BT Devices: %s at %s", deviceName, deviceHardwareAddress));

                        entries[i]=device.getName();
                        entryvalues[i]=device.getAddress();

                        i++;
                    }

                    btDevicesList.setEntries(entries);
                    btDevicesList.setEntryValues(entryvalues);
                }else{
                    Log.e("Settings", "No devices are paired.");
                    CharSequence[] entries=new CharSequence[1];
                    CharSequence[] entryvalues=new CharSequence[1];
                    entries[0]="none";
                    entryvalues[0]="none";

                    btDevicesList.setEntries(entries);
                    btDevicesList.setEntryValues(entryvalues);
                }
            }else{
                Log.e("Settings", "BT is disabled.");
                CharSequence[] entries=new CharSequence[1];
                CharSequence[] entryvalues=new CharSequence[1];
                entries[0]="disabled";
                entryvalues[0]="disabled";

                btDevicesList.setEntries(entries);
                btDevicesList.setEntryValues(entryvalues);
            }
        }
    }


}