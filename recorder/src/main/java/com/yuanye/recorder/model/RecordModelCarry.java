package com.yuanye.recorder.model;

import android.content.Context;

import com.yuanye.recorder.bean.AudioFile;
import com.yuanye.recorder.helper.RecordHelper;
import com.yuanye.recorder.manager.AudioRecordManager;
import com.yuanye.recorder.utils.Yuanye;

public class RecordModelCarry implements RecordModel {

    AudioRecordManager audioRecordManager;
    RecordHelper recordHelper;

    public RecordModelCarry(int sampleRate ) {
        audioRecordManager = AudioRecordManager.getInstance();
        recordHelper = new RecordHelper(sampleRate);
    }

    @Override
    public void start(String name) {
//        audioRecordManager.startRecord(name);
        recordHelper.start(name);
    }

    @Override
    public void pause() {
//        audioRecordManager.stopRecord();
    }

    @Override
    public void stop() {
//        audioRecordManager.stopRecord();
        recordHelper.stop();
    }

    @Override
    public void send(AudioFile audioFile, OnAudioSendListener onAudioSendListener) {

    }
}
