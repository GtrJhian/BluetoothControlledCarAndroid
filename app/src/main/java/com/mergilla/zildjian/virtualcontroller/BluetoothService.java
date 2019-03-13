
package com.mergilla.zildjian.virtualcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Thread{
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final byte[] DEVICE_ADDRESS={0x20,0x18,0x08,0x23,0x45,0x37};
    private static final int SIGNAL = 0x69;
    private byte[] byteBuffer = {0,0};

    public BluetoothService(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Log.d(Global.TAG,"No bluetooth adapter found.");
        }
        _connect();
    }

    private void _connect(){
        try{
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            if(!bluetoothSocket.isConnected()) bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        }
        catch(Exception e){
            Log.d(Global.TAG, "Exception thrown while connecting: ", e);
            _connect();
        }
        start();
    }
    @Override
    public void run(){
        while(true){
            if(!bluetoothSocket.isConnected()) _connect();
            try {
                if (inputStream.available()>0) {
                    if(inputStream.read() == SIGNAL){
                        Log.d(Global.TAG, "Writing Bytes: "+(int)byteBuffer[0]+" | "+(int)byteBuffer[1]);
                        outputStream.write(byteBuffer);
                    }
                }
            }
            catch(IOException e){
                Log.d(Global.TAG, "Exception thrown on thread. ", e);
                _connect();
            }
        }
    }
    public void setBuffer(byte[] bytes){
        byteBuffer = bytes;
    }
}