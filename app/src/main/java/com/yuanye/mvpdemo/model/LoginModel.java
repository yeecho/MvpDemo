package com.yuanye.mvpdemo.model;

import com.yuanye.mvpdemo.bean.User;

public interface LoginModel{

    void login(String name, String password, OnLoginListener onLoginListener);

    interface OnLoginListener{

        void onLoginSuccess(User user);

        void onLoginFailed(String s);
    }
}
