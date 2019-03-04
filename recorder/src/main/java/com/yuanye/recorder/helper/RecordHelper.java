package com.yuanye.recorder.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.yuanye.recorder.utils.Yuanye;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordHelper {
    //0.此状态用于控制线程中的循环操作，应用volatile修饰，保持数据的一致性
    private static String TAG = "RecordHelper";
    private volatile RecordState state = RecordState.IDLE;
    private AudioRecordThread audioRecordThread;
    private File tmpFile = null;
    private int sampleRate;

    public RecordHelper(int sampleRate){
        this.sampleRate = sampleRate;
    }

    public void start(String fileName) {
        if (state != RecordState.IDLE) {
            Log.e(TAG, "状态异常当前状态：" + state.name());
            return;
        }
        tmpFile = Yuanye.makeFileFromPath(Yuanye.CUSTOM_PATH, fileName);
        //1.开启录音线程并准备录音
        audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }

    public void stop() {
        if (state == RecordState.IDLE) {
            Log.e(TAG, "状态异常当前状态： " + state.name());
            return;
        }

        state = RecordState.STOP;
    }

    private class AudioRecordThread extends Thread {
        private AudioRecord audioRecord;
        private int bufferSize;

        AudioRecordThread() {
            //2.根据录音参数构造AudioRecord实体对象
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
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
                byte[] byteBuffer = new byte[bufferSize];

                while (state == RecordState.RECORDING) {
                    //3.不断读取录音数据并保存至文件中
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    fos.write(byteBuffer, 0, end);
                    fos.flush();
                }
                //4.当执行stop()方法后state != RecordState.RECORDING，终止循环，停止录音
                audioRecord.stop();
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