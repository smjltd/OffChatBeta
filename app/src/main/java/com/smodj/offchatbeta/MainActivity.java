package com.smodj.offchatbeta;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    public static final String ID = "Null";
    public boolean offlineModeFlag=false;
    private final static int REQUEST_ENABLE_BT = 1;
    //TextView show;
    EditText msg;
    WebView site;
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://195.154.128.104:8080");
        } catch (URISyntaxException e) {
        }
    }

    public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiApControl apControl = WifiApControl.getApControl(wifiManager);
        if (apControl != null) {
            apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                    isTurnToOn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start initialization of views
        msg = (EditText) findViewById(R.id.editTextMsg);
        site = (WebView) findViewById(R.id.webView2);
        mSocket.connect();
        // end

       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // start WebView Set
                site.getSettings().setJavaScriptEnabled(true);
                site.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                if (Build.VERSION.SDK_INT >= 19) {
                site.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                }
                else {
                site.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                site.loadUrl("http://195.154.128.104/android_index.html");
                //end
            }
        });*/

        //start WebView Set
        site.getSettings().setJavaScriptEnabled(true);
        site.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //site.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        site.loadUrl("http://smj.nismona.in/android_index.html");
        //end

        Thread thread = new Thread() {
            public void run() {
                init();
            }
        };
        thread.start();

        //reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent a = new Intent(MainActivity.this, Settings.class);
            startActivity(a);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("input");
    }

    public void send(View v) {
        //user.replaceAll("\\s", "");// removing spaces
        String message = msg.getText().toString().trim();
        // start Using SharedPerefrences to load data
        SharedPreferences sP1 = getSharedPreferences("TestData",
                Context.MODE_PRIVATE);
        String temp = sP1.getString("ID", ID);
        // end
        if (temp.isEmpty() || temp.equals("Null")) {
            Debugg.msg(this, "Set ID in Settings First!");
        } else {
            try {
                jsonTest(temp, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.setText("");
        }
    }


    public void init() {
        //Start Checking Internet Connection
        haveNetworkConnection();
        if ((haveConnectedMobile && !haveConnectedWifi) || (mobileData(this) && !haveConnectedWifi)) {
            // Debugg.msg(this, "Mobile Data ON");
            showToast("Mobile Data ON");
            if (isOnline()) {
                //Debugg.msg(this, "Online");
                showToast("Online");
                wifiTeth(true);
            } else {
                offlineMode(this);
            }

        } else if ((haveConnectedMobile && haveConnectedWifi) || (mobileData(this) && haveConnectedWifi)) {
            Thread thread = new Thread() {
                public void run() {
                    complex();
                }
            };
            thread.start();


        } else if (haveConnectedWifi && !haveConnectedMobile) {
            //Bluetooth tethering...
            if (mobileData(this)) {
                wifiOff();
                //Debugg.msg(this, "Mobile Data ON");
                showToast("Mobile Data ON");
                if (isOnline()) {
                    //Debugg.msg(this, "Online");
                    showToast("Online");
                    wifiTeth(true);
                }
            } else {
                //Debugg.msg(this, "Wifi ON");
                showToast("Wifi On");
                if (isOnline()) {
                    //Debugg.msg(this, "Online");
                    showToast("Online");
                    btON();
                    btTeth();
                } else {
                    offlineMode(this);
                }
            }
        } else if (!haveConnectedMobile && !haveConnectedWifi) {

            if (!isOnline()) {
                offlineMode(this);
            }
        } else {
            offlineMode(this);
        }
        // End

    }

    public void reload()
    {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        if(offlineModeFlag)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    site.reload();
                }
            });

        }

    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {

            // Debugg.msg(this, "" + e);
        }
        return false;
    }

    /* private void attemptSend(String name,String msg) {
         if (TextUtils.isEmpty(msg)) {
             return;
         }
         //String sendText="{\"name\":"+name+", \"message\":"+message+"}";
         String sendText="name:"+name+"message:"+msg;
         mSocket.emit("input", sendText);

     }*/
    public void jsonTest(String nam, String msg) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", nam);
        obj.put("message", msg);
        mSocket.emit("input", obj);
    }

    public void wifiOff() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //wifiManager.setWifiEnabled(true);
        wifiManager.setWifiEnabled(false);
    }

    public void wifiOn() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        //wifiManager.setWifiEnabled(false);
    }

    public void wifiTeth(boolean Enabled) {
        if (Enabled) {
            wifiOff();
            appConfig(this);
            turnOnOffHotspot(this, Enabled);
        } else {
            turnOnOffHotspot(this, Enabled);
            wifiOn();//Experimental Feature
        }
    }

    public void offlineMode(Context context) {
        //Debugg.msg(this, "Offline Mode Activated");
        showToast("Offline Mode Activated");
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiApControl wc = new WifiApControl(wifi);
        if (wc.isWifiApEnabled()) {
            Thread thread = new Thread() {
                public void run() {
                    wifiTeth(false);//Experimental Feature
                    //try{ Thread.sleep(3000); }catch(InterruptedException e){ }
                }
            };
            thread.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
        wifiOn();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        //Start-1 Tutorial to connect to specific wifi network or ssid
        String networkSSID = "Offline";
        String networkPass = ""; //pass password or "" for empty and it's used only if encryption is used

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

        //Then, for WEP network you need to do this:

            /*conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);*/

        //For WPA network you need to add passphrase like this:

            /*conf.preSharedKey = "\""+ networkPass +"\"";*/

        //For Open network you need to do this:

        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        //Then, you need to add it to Android wifi manager settings:

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        //And finally, you might need to enable it, so Android connects to it:

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
        //End-1

        offlineModeFlag = true;
        reload();

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
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                btAdapter.setName("Offline");
                //btDiscoverable();                                         //Switch it on later after enabling BtServer
            }
            // setBtName("Offline");
        }
    }

    public void btDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 9000);
        startActivity(discoverableIntent);
    }

    public void btTeth() {
        //Debugg.msg(this, "Bluetooth Hotspot");
        showToast("Started BTServer");
        //Sharing net through bluetooth
        //or
        //start communication with other bluetooth server (the hardest part)

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

    public void appConfig(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = "Offline";
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        ///netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        //netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
            }
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    public void complex() {
        if (isOnline()) {
            //Debugg.msg(this, "Online");
            showToast("Online");
            btON();
            btTeth();
        } else {
            wifiOff();
            if (isOnline()) {
                // Debugg.msg(this, "Online");
                showToast("Online");
                wifiTeth(true);
            } else {
                offlineMode(this);
            }
            //offlineMode(this);
        }
    }

    public void showToast(final String toast)// Toast used when toasting inside a threadhttp
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }


}