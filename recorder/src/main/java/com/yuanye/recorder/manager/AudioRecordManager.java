package com.yuanye.recorder.manager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.yuanye.recorder.utils.Yuanye;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AudioRecordManager {
    public static final String TAG = "AudioRecordManager";
    private static AudioRecordManager mInstance;
    private AudioRecord mRecorder;
    private int bufferSize;
    private DataOutputStream dos;
    private boolean isStart = false;
    private Thread recordThread;

    private AudioRecordManager(){
        bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2);
    }

    /**
     * 单例模式（双重判断）
     * @return
     */
    public static AudioRecordManager getInstance(){
        if (mInstance == null){
            synchronized (AudioRecordManager.class){
                if (mInstance == null){
                    mInstance = new AudioRecordManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 开始录音
     * @param name
     */
    public void startRecord(String name){
        try{
            setPath(name);
            startThread();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("yuanye", "dos创建失败");
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord(){
        try {
            destroyThread();
            if (mRecorder != null) {
                if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    mRecorder.stop();
                }
                if (mRecorder != null) {
                    mRecorder.release();
                }
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动录音线程
     */
    private void startThread(){
        destroyThread();
        isStart = true;
        if (recordThread == null){
            recordThread = new Thread(recordRunnable);
        }
        recordThread.start();
    }

    /**
     * 销毁录音线程
     */
    private void destroyThread(){
        isStart = false;
        if (recordThread != null && recordThread.getState() == Thread.State.RUNNABLE){
            try {
                Thread.sleep(500);
                recordThread.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
                recordThread = null;
            }
            recordThread = null;
        }
    }

    /**
     * 录音线程
     */
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try{
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int bytesRecord;
                byte[] tempBuffer = new byte[bufferSize];
                if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.d("yuanye", "mRecorder.getState():"+mRecorder.getState());
                    stopRecord();
                    return;
                }
                mRecorder.startRecording();
                Log.d("yuanye", "come here");
                while (isStart) {
                    if (null != mRecorder) {
                        bytesRecord = mRecorder.read(tempBuffer, 0, bufferSize);
                        if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (bytesRecord != 0 && bytesRecord != -1) {
                            //在此可以对录制音频的数据进行二次处理 比如变声，压缩，降噪，增益等操作
                            //我们这里直接将pcm音频原数据写入文件 这里可以直接发送至服务器 对方采用AudioTrack进行播放原数据
                            dos.write(tempBuffer, 0, bytesRecord);
                        } else {
                            break;
                        }
                    }
                }
                mRecorder.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void setPath(String cacheName) throws Exception{
        File file = new File(Yuanye.CUSTOM_PATH, cacheName);
        if (file.exists()){
            file.delete();
        }
        file = Yuanye.makeFileFromPath(Yuanye.CUSTOM_PATH, cacheName);
        dos = new DataOutputStream(new FileOutputStream(file, true));
    }
}
