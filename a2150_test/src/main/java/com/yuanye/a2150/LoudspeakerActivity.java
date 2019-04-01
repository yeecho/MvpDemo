package com.yuanye.a2150;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yuanye.a2150.util.Yeecho;

import java.io.IOException;

public class LoudspeakerActivity extends AppCompatActivity {

    private String tag = "LoudspeakerActivity";
    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("扬声器测试");
        setContentView(R.layout.activity_loudspeaker);
        button = findViewById(R.id.btnTest);
        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        mediaPlayer.setLooping(false);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Loudspeaker, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Loudspeaker, intent);
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
}
