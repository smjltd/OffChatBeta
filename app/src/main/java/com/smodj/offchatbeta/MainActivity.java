package com.smodj.offchatbeta;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {
    //TextView show;
    EditText msg;
    WebView site;
    public static final String ID = "Null";
    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start initialization of views
        msg = (EditText) findViewById(R.id.editTextMsg);
        site=(WebView) findViewById(R.id.webView2);
        mSocket.connect();
        // end


        //start WebView Set
        site.getSettings().setJavaScriptEnabled(true);
        site.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        site.loadUrl("http://10.42.0.1/smj/android_index.html");
        //end



        //Enabling and Checking Bluetooth
        BluetoothAdapter btAdapter;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // Device does not support Bluetooth
            Debugg.msg(this, "Device does not support bluetooth");
        } else {
            if (!btAdapter.isEnabled()) {
                btAdapter.enable();
                //Debugg.msg(this,"Bluetooth switched ON");


            }
            //end

            if (isNetworkAvailable()) {
                // Debugg.msg(this,"");
            } else {
                Debugg.msg(this, "Offline Mode");
            }
        }
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
        if(temp.isEmpty() || temp.equals("Null"))
        {
            Debugg.msg(this, "Set ID in Settings First!");
        }
        else {
            try {
                jsonTest(temp, message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            msg.setText("");
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // test for connection
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }


   /* private void attemptSend(String name,String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        //String sendText="{\"name\":"+name+", \"message\":"+message+"}";
        String sendText="name:"+name+"message:"+msg;
        mSocket.emit("input", sendText);

    }*/
    public void jsonTest(String nam,String msg) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", nam);
        obj.put("message", msg);
        mSocket.emit("input", obj);
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.42.0.1:8080");
        } catch (URISyntaxException e) {}
    }

}
