package com.yuanye.recorder.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.yuanye.recorder.model.RecordModel;
import com.yuanye.recorder.model.RecordModelCarry;
import com.yuanye.recorder.utils.Yuanye;
import com.yuanye.recorder.view.RecordView;
import com.yuanye.yeecho.base.BasePresenter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AudioRecordPresenter extends BasePresenter<RecordView>{

    private String fileName = "record_cache.pcm";
    private Context context;
    private RecordModel audioModel;
    private Handler mHandler = new Handler();
    private boolean isRecoding = false;
    private int time = 0;
    private OkHttpClient okHttpClient;
    private SharedPreferences sp;
    private int sampleRate;

    public AudioRecordPresenter(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(Yuanye.SP_NAME, Context.MODE_PRIVATE);
        sampleRate = sp.getInt(Yuanye.SP_SAMPLE_RATE, 24*1000);
        this.audioModel = new RecordModelCarry(sampleRate);
        initOkHttpClient();
    }

    public void btn1(){
        if (!isRecoding){
            startRecord();
        }else{
            stopRecord();
        }
    }

    private void pauseRecord() {
        isRecoding = false;
        mView.setImgRecording(isRecoding);
        audioModel.pause();
        mHandler.removeCallbacks(showTimeRunnable);
    }

    private void stopRecord() {
        isRecoding = false;
        mView.setImgRecording(isRecoding);
        audioModel.stop();
        mHandler.removeCallbacks(showTimeRunnable);
        mView.showSaveDialog();
    }

    private void startRecord() {
        isRecoding = true;
        mView.setImgRecording(isRecoding);
        audioModel.start(fileName);
        mHandler.postDelayed(showTimeRunnable, 1000);
    }

    Runnable showTimeRunnable = new Runnable() {
        @Override
        public void run() {
            time++;
            mView.showTime(time);
            mHandler.postDelayed(showTimeRunnable, 1000);
        }
    };

    public void btn2(){
        pauseRecord();
        mView.showSaveDialog();
    }

    public void btn3(){
    }

    public void save(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Yuanye.writeWav(fileName, "test00", sampleRate);
            }
        }).start();
        try{
            send(name);
        }catch (Exception e){
            e.printStackTrace();
            mView.showLoading(false, "发送异常");
        }
        clearTime();
    }

    public void send(String name){
        mView.showLoading(true, "正在发送数据");
        File file = new File(Yuanye.CUSTOM_PATH, fileName);
        RequestBody body = RequestBody.create(MediaType.parse("file/pcm"), file);
        Request request = new Request.Builder()
                .url(name)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.showLoading(false, "发送失败");
                    }
                });
                Log.d("yuanye", "数据发送失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.showLoading(false, "发送成功");
                    }
                });
                Log.d("yuanye", response.body().string());
            }
        });
    }

    public void clearTime(){
        time = 0;
        mView.showTime(time);
    }

    private void initOkHttpClient() {
        File sdcache = context.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        okHttpClient = builder.build();
    }
}
