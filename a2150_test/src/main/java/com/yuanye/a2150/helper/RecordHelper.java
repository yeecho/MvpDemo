package com.yuanye.a2150.helper;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.yuanye.a2150.util.Yuanye;
import com.yuanye.a2150.view.AudioFlyView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordHelper {
    private static String TAG = "RecordHelper";
    private volatile RecordState state = RecordState.IDLE;
    private AudioRecordThread audioRecordThread;
    private File tmpFile = null;
    private int sampleRate;
    private AudioFlyView audioFlyView;
    private Context context;

    public RecordHelper(Context context, int sampleRate, AudioFlyView audioFlyView){
        this.context = context;
        this.sampleRate = sampleRate;
        this.audioFlyView = audioFlyView;
    }

    public void start(String fileName) {
        if (state != RecordState.IDLE) {
            Log.e(TAG, "状态异常当前状态：" + state.name());
            return;
        }
        tmpFile = new File(context.getExternalFilesDir(null), fileName);
        //1.开启录音线程并准备录音
        audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }

    public void stop() {
        if (state == RecordState.IDLE) {
            Log.e(TAG, "状态异常,当前状态： " + state.name());
            return;
        }
        state = RecordState.STOP;
    }

    private class AudioRecordThread extends Thread {
        private AudioRecord audioRecord;
        private int bufferSize;

        AudioRecordThread() {
            //2.根据录音参数构造AudioRecord实体对象
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);//buffersize:1920/24KHz 3840/48KHz 640/8KHz
            Log.d(TAG, "bufferSize:"+bufferSize);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);
        }

        @Override
        public void run() {
            super.run();
            state = RecordState.RECORDING;
            Log.d(TAG, "开始录制");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                audioRecord.startRecording();
                byte[] byteBuffer = new byte[bufferSize];       //用来保存到本地的buffer
                audioFlyView.start();
                while (state == RecordState.RECORDING) {
                    //3.不断读取录音数据并保存至文件中
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    fos.write(byteBuffer, 0, end);
                    fos.flush();
                    if (audioFlyView.isReady()){
                        audioFlyView.setVol(Yuanye.calculateVolume(byteBuffer));
                    }
                }
                //4.当执行stop()方法后state != RecordState.RECORDING，终止循环，停止录音
                audioRecord.stop();
                audioFlyView.stop();//停止绘图
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            state = RecordState.IDLE;
            Log.d(TAG, "录音结束");
        }
    }

    enum RecordState{
        IDLE,RECORDING,PAUSE,STOP
    }
}