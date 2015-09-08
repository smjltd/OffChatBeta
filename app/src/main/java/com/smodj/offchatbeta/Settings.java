package com.smodj.offchatbeta;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by smj on 6/9/15.
 */
public class Settings extends Activity {

    //Button save;
    EditText id;
    public static final String ID = "Null";
    private static final String EXTRA_CUSTOM_TABS_SESSION_ID = "android.support.CUSTOM_TABS:session_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        id = (EditText) findViewById(R.id.editTextId);
    }
    @Override
    protected void onDestroy() {
        CustomTabActivityManager customTabManager = CustomTabActivityManager.getInstance(this);
        customTabManager.unbindService();
        super.onDestroy();
    }
    public void save(View v) {
        // start Using SharedPerefrences to load data
        SharedPreferences sP1 = getSharedPreferences("TestData",
                Context.MODE_PRIVATE);
        String temp = sP1.getString("ID", ID);
        // end
        if (temp.equals("Null")) {
            // start Using SharedPerefrences to store data
            SharedPreferences.Editor editor = sP1.edit();
            String str = id.getText().toString();
            str.replaceAll("\\s", "");// removing spaces
            editor.putString("ID", str);
            editor.commit();
            // end
            Debugg.msg(this, "Saved");
        } else {
            Debugg.msg(this, "Your ID is:" + temp);
        }
    }
    public void site(View v){
        Intent a = new Intent(Settings.this, WebTest.class);
        startActivity(a);
    }
    public void site2(View v) {
        try {

            //customtabtest(v);
            ctt();

        }
        catch (Exception e)
        {

        }
    }

    private void ctt() {
        String url = "http://smj.nismona.in/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.putExtra(EXTRA_CUSTOM_TABS_SESSION_ID, -1); // -1 or any valid session id returned from newSession() call
    }

    private void customtabtest(View v) {
        CustomTabActivityManager customTabManager = CustomTabActivityManager.getInstance(this);
        String url = "http://smj.nismona.in/";
        int viewId = v.getId();
        customTabManager.setNavigationCallback(new CustomTabActivityManager.NavigationCallback() {
            @Override
            public void onUserNavigationStarted(String url, Bundle extras) {
                //Log.w(TAG, "onUserNavigationStarted: url = " + url);
            }
            @Override
            public void onUserNavigationFinished(String url, Bundle extras) {
                //  Log.w(TAG, "onUserNavigationFinished: url = " + url);
            }
        });
        //customTabManager.bindService();
        //customTabManager.warmup();
        //if (viewId == R.id.may_launch_button) {
        //customTabManager.mayLaunchUrl(url, null);
        //}
        if (viewId == R.id.btnWebView2) {
            CustomTabUiBuilder uiBuilder = new CustomTabUiBuilder();
            uiBuilder.setToolbarColor(Color.BLUE);
            prepareMenuItems(uiBuilder);
            prepareActionButton(uiBuilder);
            uiBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
            uiBuilder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
            customTabManager.loadUrl(url, uiBuilder);
        }
    }

    private void prepareMenuItems(CustomTabUiBuilder uiBuilder) {
        Intent menuIntent = new Intent();
        menuIntent.setClass(getApplicationContext(), this.getClass());
        // Optional animation configuration when the user clicks menu items.
        Bundle menuBundle = ActivityOptions.makeCustomAnimation(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right).toBundle();
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, menuIntent, 0,
                menuBundle);
        uiBuilder.addMenuItem("Menu entry 1", pi);
    }

    private void prepareActionButton(CustomTabUiBuilder uiBuilder) {
        // An example intent that sends an email.
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("*/*");
        actionIntent.putExtra(Intent.EXTRA_EMAIL, "example@example.com");
        actionIntent.putExtra(Intent.EXTRA_SUBJECT, "example");
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, actionIntent, 0);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.arryn);
        uiBuilder.setActionButton(icon, pi);
    }
    public void experiment(View v)
    {
        Intent a = new Intent(Settings.this, Experimental.class);
        startActivity(a);
    }
}
