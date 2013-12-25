package net.itmgmedia.webviewapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String CURRENT_URL_KEY = "CURRENT_URL";

    private String currentUrl;
    private View progressBar;
    private WebView webView;
    private View errorLayout;
    private TextView errorText;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webview);
        errorLayout = findViewById(R.id.errorLayout);
        errorText = (TextView) findViewById(R.id.errorText);
        findViewById(R.id.retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadCurrentUrl();
            }
        });
        webView.setWebViewClient(new MainWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (savedInstanceState != null) {
            currentUrl = savedInstanceState.getString(CURRENT_URL_KEY);
        }
        if (currentUrl == null) {
            currentUrl = Customizaton.START_URL;
        }
        webView.loadUrl(currentUrl);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_URL_KEY, currentUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                currentUrl = Customizaton.START_URL;
                reloadCurrentUrl();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showView(View view) {
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    private void reloadCurrentUrl() {
        showView(progressBar);
        webView.loadUrl(currentUrl);
    }

    private class MainWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Customizaton.IS_ALL_HOSTS_ACCEPTED){
                return false;
            }

            Uri uri = Uri.parse(url);
            String host = uri.getHost();

            if (host != null && (host.equals(Customizaton.WEB_HOST) ||
                    host.endsWith(Customizaton.WEB_HOST_SUFFIX))) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            currentUrl = url;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (errorLayout.getVisibility() != View.VISIBLE) {
                showView(webView);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            errorText.setText(getMessageResourceId(errorCode));
            showView(errorLayout);
        }

        private int getMessageResourceId(int errorCode) {
            switch (errorCode) {
                case ERROR_UNKNOWN:
                    return R.string.ERROR_UNKNOWN;
                case ERROR_HOST_LOOKUP:
                    return R.string.ERROR_HOST_LOOKUP;
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    return R.string.ERROR_UNSUPPORTED_AUTH_SCHEME;
                case ERROR_AUTHENTICATION:
                    return R.string.ERROR_AUTHENTICATION;
                case ERROR_PROXY_AUTHENTICATION:
                    return R.string.ERROR_PROXY_AUTHENTICATION;
                case ERROR_CONNECT:
                    return R.string.ERROR_CONNECT;
                case ERROR_IO:
                    return R.string.ERROR_IO;
                case ERROR_TIMEOUT:
                    return R.string.ERROR_TIMEOUT;
                case ERROR_REDIRECT_LOOP:
                    return R.string.ERROR_REDIRECT_LOOP;
                case ERROR_UNSUPPORTED_SCHEME:
                    return R.string.ERROR_UNSUPPORTED_SCHEME;
                case ERROR_FAILED_SSL_HANDSHAKE:
                    return R.string.ERROR_FAILED_SSL_HANDSHAKE;
                case ERROR_BAD_URL:
                    return R.string.ERROR_BAD_URL;
                case ERROR_FILE_NOT_FOUND:
                    return R.string.ERROR_FILE_NOT_FOUND;
                case ERROR_TOO_MANY_REQUESTS:
                    return R.string.ERROR_TOO_MANY_REQUESTS;
                default:
                    return R.string.ERROR_UNKNOWN;
            }
        }
    }
}
