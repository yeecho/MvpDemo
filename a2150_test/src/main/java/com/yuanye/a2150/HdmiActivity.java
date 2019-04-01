package com.yuanye.a2150;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yuanye.a2150.util.Yeecho;

public class HdmiActivity extends AppCompatActivity{

    private VideoView mVideoView;
    private MediaController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("HDMI测试");
        setContentView(R.layout.activity_hdmi);
        mVideoView = findViewById(R.id.videoview);
        controller = new MediaController(this);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.video_test;
//        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.setMediaController(controller);
        controller.setAnchorView(mVideoView);
        setupVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mVideoView.isPlaying()){
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.canPause()){
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaybackVideo();
    }

    private void setupVideo() {
//            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mVideoView.start();
//            }
//        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaybackVideo();
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                stopPlaybackVideo();
                return true;
            }
        });

        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.video_test);
            mVideoView.setVideoURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPlaybackVideo() {
        try {
            mVideoView.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Hdmi, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Hdmi, intent);
                finish();
                break;
            case R.id.btnTest:
//                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.start();
                break;
        }

    }
}
