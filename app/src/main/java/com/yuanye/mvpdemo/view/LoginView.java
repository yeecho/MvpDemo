package com.yuanye.mvpdemo.view;

import com.yuanye.mvpdemo.bean.User;
public interface LoginView{

    void showLoading();

    void hideLoading();

    String getUsername();

    String getPassword();

    void showSuccessMsg(User user);

    void showFailedMsg(String s);

    void clear();
}
