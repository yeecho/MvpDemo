package com.yuanye.a2150;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yuanye.a2150.util.Yeecho;

public class TpActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Tp测试");
        setContentView(R.layout.activity_tp);
        findViewById(R.id.btnTest).setVisibility(View.GONE);
        Settings.System.putInt(getContentResolver(),  "show_touches", 1);
        Settings.System.putInt(getContentResolver(),  "pointer_location", 1);
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_TP, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_TP, intent);
                finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Settings.System.putInt(getContentResolver(),  "show_touches", 0);
        Settings.System.putInt(getContentResolver(),  "pointer_location", 0);
    }
}
