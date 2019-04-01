package com.yuanye.a2150;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanye.a2150.adapter.BlueAdapter;
import com.yuanye.a2150.helper.BlueToothHelper;
import com.yuanye.a2150.listener.ScanBlueCallBack;
import com.yuanye.a2150.util.Yeecho;

import java.util.ArrayList;
import java.util.List;

public class BlueToothActivity extends AppCompatActivity {

    private String tag = "BlueToothActivity";
    private ListView listView;
    private TextView blue_state;
    private Button button;
    private BlueToothHelper blueToothHelper;
    private ScanBlueReceiver scanBlueReceiver;
    private List<BluetoothDevice> list;
    private BlueAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("蓝牙测试");
        setContentView(R.layout.activity_bluetooth);
        blue_state = findViewById(R.id.blue_state);
        list = new ArrayList<>();
        adapter = new BlueAdapter(this, list);
        listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
        button = findViewById(R.id.btnTest);
        blueToothHelper = new BlueToothHelper(this);
        if (blueToothHelper.isSupportBlue()) {
            IntentFilter filter = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            ScanBlueCallBack scanBlueCallBack = new ScanBlueCallBack() {
                @Override
                public void onScanStarted() {
                    blue_state.setText("开始扫描");
                }

                @Override
                public void onScanFinished() {
                    blue_state.setText("扫描结束");
                }

                @Override
                public void onScanning(BluetoothDevice device) {
//                    blue_state.setText(device.getName());
                    list.add(device);
                    adapter.updateList(list);
                }
            };
            scanBlueReceiver = new ScanBlueReceiver(scanBlueCallBack);
            registerReceiver(scanBlueReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        blueToothHelper.closeBlue();
        if (scanBlueReceiver != null) {
            unregisterReceiver(scanBlueReceiver);
        }
    }

    public void topClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_BlueTooth, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_BlueTooth, intent);
                finish();
                break;
            case R.id.btnTest:
                if (!blueToothHelper.isBlueEnable()) {
                    button.setText("关闭");
                    button.setBackgroundColor(getResources().getColor(R.color.button_color_unclickable));
                    button.setClickable(false);
//                    blueToothHelper.openBlueSync(Yeecho.BLUETOOTH_REQUEST_CODE);
                    blueToothHelper.openBlueAsyn();
                } else {
                    button.setText("测试");
                    blueToothHelper.closeBlue();
                    list.clear();
                    adapter.updateList(list);
                }
                break;
        }

    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Yeecho.BLUETOOTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                button.setText("CLOSE");
                button.setFocusable(true);
                blueToothHelper.scanBlue();
            }else{
                button.setFocusable(true);
            }
        }
    }*/

    class ScanBlueReceiver extends BroadcastReceiver {
        private String TAG = ScanBlueReceiver.class.getName();
        private ScanBlueCallBack callBack;

        public ScanBlueReceiver(ScanBlueCallBack callBack) {
            this.callBack = callBack;
        }

        //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action:" + action);

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.d(TAG, "状态便了...");
                    button.setBackgroundColor(getResources().getColor(R.color.button_color));
                    button.setClickable(true);
                    blueToothHelper.scanBlue();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "开始扫描...");
                    callBack.onScanStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "结束扫描...");
                    callBack.onScanFinished();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d(TAG, "发现设备...");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    callBack.onScanning(device);
                    break;
            }
        }
    }
}
