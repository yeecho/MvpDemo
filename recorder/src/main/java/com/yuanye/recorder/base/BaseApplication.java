package com.yuanye.recorder.base;

import android.app.Application;

public class BaseApplication extends Application{

    private static BaseApplication baseApplication;

    public BaseApplication(){};

    public static BaseApplication getInstance(){
        if (baseApplication == null){
            synchronized (BaseApplication.class){
                if (baseApplication == null){
                    baseApplication = new BaseApplication();
                }
            }
        }
       return baseApplication;
    }
}
