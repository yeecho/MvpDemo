package com.yuanye.mvpdemo.presenter;

import com.yuanye.mvpdemo.bean.User;
import com.yuanye.mvpdemo.model.LoginModel;
import com.yuanye.mvpdemo.model.LoginModelImpl;
import com.yuanye.mvpdemo.view.LoginView;
import com.yuanye.yeecho.base.BasePresenter;

public class LoginPresenter extends BasePresenter<LoginView> {

    private LoginModel loginModel;

    public LoginPresenter(){
        loginModel = new LoginModelImpl();
    }

    public void login(){
        mView.showLoading();
        loginModel.login(mView.getUsername(), mView.getPassword(), new LoginModel.OnLoginListener() {
            @Override
            public void onLoginSuccess(User user) {
                if (isViewAttached()){
                    mView.hideLoading();
                    mView.showSuccessMsg(user);
                }
            }

            @Override
            public void onLoginFailed(String s) {
                if (isViewAttached()){
                    mView.hideLoading();
                    mView.showFailedMsg(s);
                }
            }
        });
    }

    public void clean(){
        mView.clear();
    }
}
