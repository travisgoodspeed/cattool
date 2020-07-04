package com.kk4vcz.goodspeedscattool;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.preference.ListPreference;

import com.kk4vcz.codeplug.RadioConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/* This class wraps an Android RFCOMM Bluetooth socket for use with the generic codeplugtool
 * library, so that the radio drivers can be written on a Linux desktop well away from a phone.
 */

public class BTConnection implements RadioConnection {
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public static BTConnection getConnection(String adr) throws IOException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("BTConnect", "Device doesn't support bluetooth!");
        }else{
            if(bluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                //We don't search for devices, but rather expect that devices are already paired.
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        Log.v("BTConnect", String.format("BT Devices: %s at %s", deviceName, deviceHardwareAddress));

                        if(deviceHardwareAddress.equals(adr)){
                            Log.v("BTConnect", String.format("Connecting to %s at %s", deviceName, deviceHardwareAddress));
                            BluetoothSocket socket=device.createRfcommSocketToServiceRecord(DEVICE_UUID);
                            return new BTConnection(socket);
                        }
                    }
                }else{
                    Log.e("BTConnect", "No devices are paired.");
                }
            }else{
                Log.e("BTConnect", "BT is disabled.");
            }
        }

        return null;
    }

    BluetoothSocket socket;

    public BTConnection(BluetoothSocket socket) throws IOException {
        socket.connect();
        Log.v("BTConnect", "connected");
        this.socket=socket;
    }

    @Override
    public InputStream getInputStream()  throws IOException {
        return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream()  throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public int setBaudRate(int baudrate) {
        // No such thing as a baud rate in TCP.  Better hope your server knows it.
        return 0;
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public void close() throws IOException {
        Log.v("BTConnect", "Disconnecting.");
        socket.close();
    }
}
