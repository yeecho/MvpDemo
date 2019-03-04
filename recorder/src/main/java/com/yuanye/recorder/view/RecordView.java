package com.yuanye.recorder.view;

public interface RecordView {

    void setImgRecording(boolean b);

    void showTime(int i);

    void showSaveDialog();

    void showLoading(boolean b, String msg);
}
