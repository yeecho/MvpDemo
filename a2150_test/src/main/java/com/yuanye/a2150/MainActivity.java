package com.yuanye.a2150;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.yuanye.a2150.adapter.MainAdapter;
import com.yuanye.a2150.util.Yeecho;
import com.yuanye.a2150.util.Yuanye;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private String tag = "MainActivity";
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private List<String> names,states;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("A2150测试");
        setContentView(R.layout.activity_main);
        recyclerView  = findViewById(R.id.recyclerView);
        names = Yuanye.getTestItems(this);
        states = Yuanye.initStates(names.size());
        mainAdapter = new MainAdapter(this, names, states);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mainAdapter);
//        initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= M) {
            String permissions[] = {
                    //Android m中 扫描wifi和蓝牙需要位置权限
                    //Manifest.permission.ACCESS_FINE_LOCATION,
                    // Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    //Manifest.permission.READ_EXTERNAL_STORAGE,
                    //Manifest.permission.CALL_PHONE,
                    Manifest.permission.CAMERA,
//                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.INTERNET
                    //  Manifest.permission.ACCESS_NETWORK_STATE,
                    //  Manifest.permission.CHANGE_NETWORK_STATE,
                    //  Manifest.permission.ACCESS_WIFI_STATE,
                    //  Manifest.permission.CHANGE_WIFI_STATE
            };

            ArrayList<String> toApplyList = new ArrayList<String>();
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                    //进入到这里代表没有权限.
                    toApplyList.add(perm);
                    Log.d(tag,"try to request:" + perm);
                }
            }

            if (!toApplyList.isEmpty()) {
                String tmpList[] = new String[toApplyList.size()];
                ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), Yeecho.PERMISSION_REQUEST_CODE);
            }else{
                recyclerView.setAdapter(mainAdapter);
            }
        }else {
            recyclerView.setAdapter(mainAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Yeecho.PERMISSION_REQUEST_CODE:
                boolean hasAllPermssion = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //未获得权限
                        Toast.makeText(this, "get Permission failed", Toast.LENGTH_LONG).show();
                        hasAllPermssion = false;
                    }
                }
                if (hasAllPermssion){
                    recyclerView.setAdapter(mainAdapter);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "requestCode:"+requestCode+"***"+"resultCode:"+resultCode);
        if (resultCode != 0){
            boolean b = data.getBooleanExtra(Yeecho.TEST_RESULT, false);
            states.set(resultCode % 800, b ? "通过" : "失败");
            mainAdapter.updateStates(states);
        }
    }
}
