package com.yuanye.a2150;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanye.a2150.adapter.WifiAdapter;
import com.yuanye.a2150.util.Yeecho;

import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends AppCompatActivity {

    private String tag = "WifiActivity";
    private WifiManager wifiManager;
    private WifiBroadCastReceiver receiver;
    private Button test;
    private TextView text_state;
    private ListView listView;
    private List<ScanResult> results;
    private WifiAdapter adapter;
    private WifiConfiguration config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WIFI测试");
        setContentView(R.layout.activity_wifi);
        test = findViewById(R.id.btnTest);
        text_state = findViewById(R.id.wifi_state);
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new ItemClickListener());
        //初始化数据
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        receiver = new WifiBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(wifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(wifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
        wifiManager.setWifiEnabled(false);
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Wifi, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Wifi, intent);
                finish();
                break;
            case R.id.btnTest:
                wifiManager.setWifiEnabled(wifiManager.isWifiEnabled() ? false : true);
                break;
        }

    }

    public WifiConfiguration createConfiguration(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";//必须多引号，有点特殊
        if (type == Yeecho.WIFI_ENCRYPTION_TYPE_WEP) {
            int i = password.length();
            if (((i == 10 || (i == 26) || (i == 58))) && (password.matches("[0-9A-Fa-f]*"))) {
                config.wepKeys[0] = password;
            } else {
                config.wepKeys[0] = "\"" + password + "\"";
            }
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == Yeecho.WIFI_ENCRYPTION_TYPE_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    class WifiBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(tag, "ACTION:"+intent.getAction());
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_DISABLED);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            test.setText("测试");
                            listView.setAdapter(null);
                            Log.d(tag, "WIFI_STATE_DISABLED");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.d(tag, "WIFI_STATE_ENABLED");
                            test.setText("关闭");
                            wifiManager.startScan();
                            break;
                    }
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    results = wifiManager.getScanResults();
                    adapter = new WifiAdapter(context, results);
                    listView.setAdapter(adapter);
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        text_state.setText("暂无连接");
                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        text_state.setText("已连接到网络:" + wifiInfo.getSSID());
                    }else {
                        NetworkInfo.DetailedState state = info.getDetailedState();
                        if (state == state.CONNECTING) {
                            text_state.setText("连接中...");
                        } else if (state == state.AUTHENTICATING) {
                            text_state.setText("正在验证身份信息...");
                        } else if (state == state.OBTAINING_IPADDR) {
                            text_state.setText("正在获取IP地址...");
                        } else if (state == state.FAILED) {
                            text_state.setText("连接失败");
                        }
                    }
                    break;
            }
        }
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            wifiManager.disconnect();
            final ScanResult scanResult = results.get(i);
            String capabilities = scanResult.capabilities;
            int type = Yeecho.WIFI_ENCRYPTION_TYPE_NONE;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    type = Yeecho.WIFI_ENCRYPTION_TYPE_WPA;
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    type = Yeecho.WIFI_ENCRYPTION_TYPE_WEP;
                } else {
                    type = Yeecho.WIFI_ENCRYPTION_TYPE_NONE;
                }
            }
//            config = isExsits(scanResult.SSID);
//            if (config == null) {
                if (type != Yeecho.WIFI_ENCRYPTION_TYPE_NONE) {//需要密码
                    final EditText editText = new EditText(WifiActivity.this);
                    final int finalType = type;
                    new AlertDialog.Builder(WifiActivity.this)
                            .setTitle("请输入Wifi密码")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.w("AAA", "editText.getText():" + editText.getText());
                                    config = createConfiguration(scanResult.SSID, editText.getText().toString(), finalType);
                                    connect(config);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    config = createConfiguration(scanResult.SSID, "", type);
                    connect(config);
                }
//            } else {
//                connect(config);
//            }
        }
    }

    private void connect(WifiConfiguration config) {
        int wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

}
