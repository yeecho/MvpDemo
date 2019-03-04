package com.yuanye.mvpdemo.model;

import android.os.Handler;

import com.yuanye.mvpdemo.bean.User;

public class LoginModelImpl implements LoginModel {

    @Override
    public void login(final String name, final String password, final OnLoginListener onLoginListener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (name.equals("")){
                    onLoginListener.onLoginFailed("名字是空的");
                }else if (password.equals("")){
                    onLoginListener.onLoginFailed("密码是空的");
                }else if (name.equals("yuanye") && password.equals("123")){
                    onLoginListener.onLoginSuccess(new User(name, password));
                }else{
                    onLoginListener.onLoginFailed("帐号或者密码不对");
                }
            }
        }, 1500);
    }
}
