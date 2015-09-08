package com.smodj.offchatbeta;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by smj on 6/5/15.
 */
public class Debugg {
    public static void msg(Context con,String str)
    {

        Toast.makeText(con, str, Toast.LENGTH_LONG).show();

    }

}
