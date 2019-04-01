package com.yuanye.a2150;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanye.a2150.adapter.AudioAdapter;
import com.yuanye.a2150.helper.RecordHelper;
import com.yuanye.a2150.listener.AudioSaveCallBack;
import com.yuanye.a2150.util.Yeecho;
import com.yuanye.a2150.util.Yuanye;
import com.yuanye.a2150.view.AudioFlyView;

import java.io.File;
import java.util.List;

public class AudioRecordActivity extends AppCompatActivity {

    private String tag = AudioRecordActivity.class.getName();

    private TextView txtTime;
    private AudioFlyView audioFlyView;
    private Button btnTest;
    private ListView listView;
    private boolean isRecording = false;
    private int time = 0;
    private Handler handler;
    private RecordHelper recordHelper;
    private String recordCache = "record_cache.pcm";
    private List<String> list;
    private AudioAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("录音测试");
        setContentView(R.layout.activity_audio_record);
        initView();
        handler = new Handler();
        recordHelper = new RecordHelper(this,8000, audioFlyView);
        list = Yuanye.getMyRecordFiles(this);
        adapter = new AudioAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        recordHelper.stop();
        super.onDestroy();
    }

    private void initView() {
        txtTime = findViewById(R.id.txt_time);
        audioFlyView = findViewById(R.id.audio_fly);
        btnTest = findViewById(R.id.btnTest);
        listView = findViewById(R.id.listview);
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_AudioRecord, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_AudioRecord, intent);
                finish();
                break;
            case R.id.btnTest:
                if (!isRecording){
                    recordHelper.start(recordCache);
                    handler.postDelayed(showTimeRunnable, 1000);
                    isRecording = true;
                    btnTest.setText("停止");
                }else{
                    recordHelper.stop();
                    handler.removeCallbacks(showTimeRunnable);
                    showSaveDialog();
                    isRecording = false;
                    btnTest.setText("测试");
                }
                break;
        }

    }

    Runnable showTimeRunnable = new Runnable() {
        @Override
        public void run() {
            time++;
            showTime(time);
            handler.postDelayed(showTimeRunnable, 1000);
        }
    };


    Dialog dialog;
    private void showSaveDialog() {
        View view = LayoutInflater.from(AudioRecordActivity.this).inflate(R.layout.dialog_save, null);
        final EditText edt = view.findViewById(R.id.dialog_save_edt);
        dialog = new AlertDialog.Builder(this)
                .setTitle("录音")
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            dialog.dismiss();
                            time = 0;
                            showTime(time);
                        }
                    }
                }).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            dialog.dismiss();
                            time = 0;
                            showTime(time);
                        }
                        save(edt.getText().toString());
                    }
                }).create();
        dialog.show();
    }

    private void showTime(int count) {
        txtTime.setText(Yuanye.translateTime(count));
    }

    private void save(String name) {
        final File target = new File(getExternalFilesDir(null), recordCache);
        final File wav = new File(getExternalFilesDir(null), name+".wav");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Yuanye.writeWav(target, wav, 8000, new AudioSaveCallBack() {
                    @Override
                    public void onSaveCompleted() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showAudioFiles();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void showAudioFiles() {
        list = Yuanye.getMyRecordFiles(this);
        adapter.updateList(list);
    }


}
