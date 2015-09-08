package com.smodj.offchatbeta;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;
import java.lang.reflect.Method;

public class Experimental extends Activity
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exp);
        Debugg.msg(this, "Offline Mode Experimental");
    }

    public void wifiOff() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //wifiManager.setWifiEnabled(true);
        wifiManager.setWifiEnabled(false);
    }
    public void btON() {
        //Enabling and Checking Bluetooth
        BluetoothAdapter btAdapter;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // Device does not support Bluetooth
            //Debugg.msg(this, "Device does not support bluetooth");
        } else {
            if (!btAdapter.isEnabled()) {
                btAdapter.enable();
            }
            //end
        }
    }
    public boolean mobileData(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {

        }
        return mobileDataEnabled;
    }
    public void showToast(final String toast)// Toast used when toasting inside a thread
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Experimental.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}