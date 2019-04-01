package com.yuanye.a2150;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yuanye.a2150.util.Yeecho;

public class BatteryActivity extends AppCompatActivity{

    private TextView mTvVoltage;
    private TextView mTvTemperature;
    private TextView mTvLevel;
    private TextView mTvStatus;
    private TextView mTvHealth;
    private TextView mTvTechnology;
    private BatteryStateReceiver receiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("电池测试");
        setContentView(R.layout.activity_battery);
        initView();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryStateReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initView() {
        findViewById(R.id.btnTest).setVisibility(View.GONE);
        mTvVoltage = findViewById(R.id.tv_voltage);
        mTvTemperature = findViewById(R.id.tv_temperature);
        mTvTemperature.setVisibility(View.GONE);
        mTvLevel = findViewById(R.id.tv_level);
        mTvStatus = findViewById(R.id.tv_state);
        mTvHealth = findViewById(R.id.tv_health);
        mTvTechnology = findViewById(R.id.tv_technology);
    }

    public void topClick(View v){
        boolean b = false;
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                b = true;
                break;
            case R.id.btnFail:
                b = false;
                break;
        }
        intent.putExtra(Yeecho.TEST_RESULT, b);
        setResult(Yeecho.ResultCode_Battery, intent);
        finish();
    }

    class BatteryStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int voltage=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            mTvVoltage.setText("电压：" + voltage / 1000 + "." + voltage % 1000 + "V");
            mTvVoltage.setTextColor(Color.BLUE);
            int temperature=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            mTvTemperature.setText("温度：" + temperature / 10 + "." + temperature % 10 + "℃");
            if (temperature >= 300) {
                mTvTemperature.setTextColor(Color.RED);
            } else {
                mTvTemperature.setTextColor(Color.BLUE);
            }
            int level=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int scale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int levelPercent = (int)(((float)level / scale) * 100);
            mTvLevel.setText("电量：" + levelPercent + "%");
            if (level <= 10) {
                mTvLevel.setTextColor(Color.RED);
            } else {
                mTvLevel.setTextColor(Color.BLUE);
            }

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            String strStatus = "未知状态";;
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "充电中……";
                    mTvStatus.setTextColor(Color.BLUE);
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "放电中……";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "未充电";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "充电完成";
                    break;
            }
            mTvStatus.setText("状态：" + strStatus);

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
            String strHealth = "未知 :(";
            mTvHealth.setTextColor(Color.BLACK);
            switch (status) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    strHealth = "好 :)";
                    mTvHealth.setTextColor(Color.GREEN);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    strHealth = "过热！";
                    mTvHealth.setTextColor(Color.RED);
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD: // 未充电时就会显示此状态，这是什么鬼？
                    strHealth = "良好";
                    mTvHealth.setTextColor(Color.GREEN);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    strHealth = "电压过高！";
                    mTvHealth.setTextColor(Color.RED);
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    strHealth = "未知 :(";
                    break;
                case BatteryManager.BATTERY_HEALTH_COLD:
                    strHealth = "过冷！";
                    mTvHealth.setTextColor(Color.RED);
                    break;
            }
            mTvHealth.setText("健康状况：" + strHealth);

            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            mTvTechnology.setText("电池技术：" + technology);

        }
    }
}
