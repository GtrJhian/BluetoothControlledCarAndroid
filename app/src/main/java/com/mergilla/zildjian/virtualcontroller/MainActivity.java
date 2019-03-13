package com.mergilla.zildjian.virtualcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    final String TAG = "APP_DEBUG_TAG";
    private BluetoothService bluetoothService = new BluetoothService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JoystickView joystick = findViewById(R.id.joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                //Log.d(TAG,"Angle: "+angle+" Strength: "+strength);
                double rad = Math.toRadians(angle);
                byte[] bytes = new byte[2];
                //==============left motor
                int temp;
                if( angle<=90 && angle>=0 ){
                    temp =  strength;
                }
                else if( angle>=270 && angle<360 ){
                    temp = -strength;
                }
                else if( angle > 90 && angle <=180){
                    temp = -(int)(strength * Math.cos(2*rad));
                }
                else{
                    temp = (int)(strength * Math.cos(2*rad));
                }
                bytes[0] = (byte) (temp >= 0 ? temp : -temp | 0x80);
                //==================right motor;
                if( angle<=90 && angle>=0 ){
                    temp = (int)(-strength * Math.cos(2*rad));
                }
                else if( angle>=270 && angle<360 ){
                    temp = (int)(strength * Math.cos(2*rad));
                }
                else if( angle > 90 && angle <=180){
                    temp =  strength;
                }
                else{
                    temp = -strength;
                }
                bytes[1] = (byte) (temp >= 0 ? temp : -temp | 0x80);
                bluetoothService.setBuffer(bytes);
            }
        });
    }
}
