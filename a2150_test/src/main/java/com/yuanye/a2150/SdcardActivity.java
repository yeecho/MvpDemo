package com.yuanye.a2150;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanye.a2150.adapter.FilePreviewAdapter;
import com.yuanye.a2150.bean.StorageBean;
import com.yuanye.a2150.util.StorageUtil;
import com.yuanye.a2150.util.Yeecho;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SdcardActivity extends AppCompatActivity{

    private String tag = "SdcardActivity";
    private TextView txtVolumeName;
    private ListView listView;
    private UsbReceiver usbReceiver;
    private List<File> list;
    private FilePreviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SD卡、U盘测试");
        setContentView(R.layout.activity_sdcard);
        findViewById(R.id.btnTest).setVisibility(View.GONE);
        txtVolumeName = findViewById(R.id.volume_name);
        listView = findViewById(R.id.lsv_preview);
        list = new ArrayList<>();
        adapter = new FilePreviewAdapter(this,list);
        listView.setAdapter(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        usbReceiver = new UsbReceiver();
        this.registerReceiver(usbReceiver, filter);
        checkSDcard(StorageUtil.getStorageData(this));
    }

    private void checkSDcard(List<StorageBean> volumes) {
        if (volumes.size()>0){
            for (StorageBean storageBean : volumes){
                if (storageBean.getRemovable()){
                    txtVolumeName.setText("外置存储："+storageBean.getPath());
                    File[] files = new File(storageBean.getPath()).listFiles();
                    if (files != null){
                        if (files.length>0){
                            for (File f : files){
                                list.add(f);
                            }
                            adapter.upateData(list);
                        }
                    }

                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
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
        setResult(Yeecho.ResultCode_Sdcard, intent);
        finish();
    }

    class UsbReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(tag, "ACTION:"+intent.getAction().toString());
//            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
            switch (intent.getAction()){
                case Intent.ACTION_MEDIA_CHECKING:
                    txtVolumeName.setText("设备检测中..");
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    txtVolumeName.setText("设备已挂载");
                    checkSDcard(StorageUtil.getStorageData(SdcardActivity.this));
                    break;
                case Intent.ACTION_MEDIA_EJECT:
                    adapter.clear();
                    txtVolumeName.setText("设备强制移除");
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                    adapter.clear();
                    txtVolumeName.setText("设备正常移除");
                    break;
            }
        }
    }
}
