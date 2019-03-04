package com.yuanye.recorder.model;

import com.yuanye.recorder.bean.AudioFile;

public interface RecordModel {

    void start(String name);

    void pause();

    void stop();

    void send(AudioFile audioFile, OnAudioSendListener onAudioSendListener);

    interface OnAudioSendListener{

        void onSendSuccess();

        void onSendFailed();
    }
}
