package com.smodj.offchatbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by smj on 6/9/15.
 */
public class Settings extends AppCompatActivity {

    //Button save;
    EditText id;
    public static final String ID = "Null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        id = (EditText) findViewById(R.id.editTextId);
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
}
