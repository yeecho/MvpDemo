package com.yuanye.recorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanye.recorder.utils.Yuanye;

public class SettingActivity extends Activity{
    private TextView sampleRate,address,about;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = this.getSharedPreferences(Yuanye.SP_NAME, MODE_PRIVATE);
        initTitle();
        initView();
    }

    private void initTitle() {
        TextView title = findViewById(R.id.page_title);
        title.setText("设置");
        ImageView back = findViewById(R.id.page_left);
        back.setBackgroundResource(R.drawable.left_arrow_selector);
    }

    private void initView() {
        sampleRate = findViewById(R.id.current_sample_rate);
        sampleRate.setText(sp.getInt(Yuanye.SP_SAMPLE_RATE, 24*1000)+"");
        address = findViewById(R.id.current_address);
        address.setText(sp.getString(Yuanye.SP_ADDRESS, ""));
    }

    public void mainClick(View view){
        switch (view.getId()){
            case R.id.page_left:
                finish();
                break;
            case R.id.setting_sample_rate:
                break;
            case R.id.setting_address:
                break;
            case R.id.setting_about:
                break;
        }
    }
}
