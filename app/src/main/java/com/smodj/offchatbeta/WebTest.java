package com.smodj.offchatbeta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by smj on 6/9/15.
 */
public class WebTest extends AppCompatActivity {

    WebView site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        site=(WebView) findViewById(R.id.webView1);

        site.setWebViewClient(new WebViewClient());
        site.setWebViewClient(new MyBrowser());
        site.getSettings().setLoadsImagesAutomatically(true);
        site.getSettings().setJavaScriptEnabled(true);
        site.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        site.loadUrl("http://10.42.0.1/smj");
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.site.canGoBack()) {
            this.site.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
