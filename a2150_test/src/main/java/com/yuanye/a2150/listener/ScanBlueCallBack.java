package com.yuanye.a2150.listener;

import android.bluetooth.BluetoothDevice;

public interface ScanBlueCallBack {
    void onScanStarted();
    void onScanFinished();
    void onScanning(BluetoothDevice device);
}
