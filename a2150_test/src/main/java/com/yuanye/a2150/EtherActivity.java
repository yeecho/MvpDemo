package com.yuanye.a2150;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuanye.a2150.util.Yeecho;

public class EtherActivity extends AppCompatActivity{

    private TextView textView;
    private WebView webView;
    private ProgressBar progressBar;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("以太网测试");
        setContentView(R.layout.activity_ether);
        textView = findViewById(R.id.textview);
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null){
            if (info.isConnected()){
                textView.setText("已经联网");
            }
        }
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Ether, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Ether, intent);
                finish();
                break;
            case R.id.btnTest:
                showSaveDialog();
                break;
        }

    }

    Dialog dialog;
    private void showSaveDialog() {
        View view = LayoutInflater.from(EtherActivity.this).inflate(R.layout.dialog_web, null);
        final EditText edt = view.findViewById(R.id.dialog_web_edt);
        dialog = new AlertDialog.Builder(this)
                .setTitle("联网请求")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            webView.setWebViewClient(webViewClient);
                            webView.loadUrl("http://"+edt.getText().toString());
                            dialog.dismiss();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            dialog.dismiss();
                        }
                    }
                }).create();
        dialog.show();
    }

    WebViewClient webViewClient = new WebViewClient(){

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    };
}
