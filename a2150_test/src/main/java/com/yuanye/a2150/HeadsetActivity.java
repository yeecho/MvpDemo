package com.yuanye.a2150;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanye.a2150.util.Yeecho;

public class HeadsetActivity extends AppCompatActivity {

    private String tag = HeadsetActivity.class.getName();
    private TextView txt;
    private Button button;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("耳机测试");
        setContentView(R.layout.activity_headset);
        txt = findViewById(R.id.txt);
        button = findViewById(R.id.btnTest);
        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        mediaPlayer.setLooping(false);
        mediaPlayer.setVolume(0.5f, 0.5f);
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPlugReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(headsetPlugReceiver);
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Headset, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Headset, intent);
                finish();
                break;
            case R.id.btnTest:
                if (!isPlaying){
                    isPlaying = true;
                    button.setText("停止");
                    mediaPlayer.start();
                }else{
                    isPlaying = false;
                    button.setText("测试");
                    mediaPlayer.pause();
                }
                break;
        }

    }

    class HeadsetPlugReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", 0);//0:无；1：有
            String name = intent.getStringExtra("name"); //耳机类型
            int microphone  = intent.getIntExtra("microphone", 0); //0:无；1：有
            Log.d(tag,"state:"+state+" name:"+name+" microphone:"+microphone);
            if(intent.hasExtra("state")){
                if(intent.getIntExtra("state", 0)==0){
                    txt.setText("未检测到耳机");
                    Toast.makeText(context, "headset not connected", Toast.LENGTH_SHORT).show();
                }else if(intent.getIntExtra("state", 0)==1){
                    txt.setText("检测到耳机插入");
                    Toast.makeText(context, "headset  connected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
