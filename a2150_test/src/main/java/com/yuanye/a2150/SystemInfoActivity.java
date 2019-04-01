package com.yuanye.a2150;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yuanye.a2150.util.Yeecho;
import com.yuanye.a2150.util.Yuanye;

public class SystemInfoActivity extends AppCompatActivity{
    private TextView pixel,memory,emmc,sn,mac;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("系统信息");
        setContentView(R.layout.activity_system_info);
        hideTestButton();
        pixel = findViewById(R.id.pixel);
        pixel.setText("分辨率：" + getDisPlayPixel());
        memory = findViewById(R.id.memory);
        emmc = findViewById(R.id.emmc);
        memory.setText("运存：" + getRamInfo(this));
        emmc.setText("存储：" + getRomInfo());
        sn = findViewById(R.id.sn);
        sn.setText("序列号：" + Yuanye.getSerialNumber());
        mac = findViewById(R.id.mac);
        mac.setText("MAC地址：" + Yuanye.getMac(this));
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
        setResult(Yeecho.ResultCode_SystemInfo, intent);
        finish();
    }

    private String getDisPlayPixel(){
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.x + " X " + point.y;
    }

    //获取内存信息
    private String getRamInfo(Context context){
        long gb = 1024 * 1024 * 1024;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        float f = (float) info.totalMem / gb;
        String s = "%.2f GB";
        return String.format(s, f);
    }

    //获取ROM内存信息
    private String getRomInfo(){
        long gb = 1024 * 1024 * 1024;
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalCounts = statFs.getBlockCountLong();//总共的block数
        long availableCounts = statFs.getAvailableBlocksLong() ; //获取可用的block数
        long size = statFs.getBlockSizeLong(); //每格所占的大小，一般是4KB==
        long availROMSize = availableCounts * size;//可用内部存储大小
        long totalROMSize = totalCounts *size; //内部存储总大小
        float f= (float) totalROMSize / gb;
        String s = "%.2f GB";
        return String.format(s, f);
    }

    public void hideTestButton() {
        findViewById(R.id.btnTest).setVisibility(View.GONE);
    }
}
