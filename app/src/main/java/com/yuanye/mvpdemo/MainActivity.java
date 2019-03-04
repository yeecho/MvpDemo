package com.yuanye.mvpdemo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yuanye.mvpdemo.bean.User;
import com.yuanye.mvpdemo.presenter.LoginPresenter;
import com.yuanye.mvpdemo.utils.Yuanye;
import com.yuanye.mvpdemo.view.LoginView;
import com.yuanye.yeecho.base.BaseActivity;


public class MainActivity extends BaseActivity<LoginPresenter> implements LoginView{

    private EditText edtUsername,edtPassword;
    private ProgressBar pgb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected LoginPresenter creatPresenter() {
        return new LoginPresenter();
    }

    private void initView() {
        edtUsername = findViewById(R.id.main_et_username);
        edtPassword = findViewById(R.id.main_et_password);
        pgb = findViewById(R.id.main_pgb);
    }

    public void LoginClick(View view){
        mPresenter.login();
    }

    public void ClearClick(View view){
        mPresenter.clean();
    }

    @Override
    public String getUsername() {
        return edtUsername.getText().toString();
    }

    @Override
    public String getPassword() {
        return edtPassword.getText().toString();
    }

    @Override
    public void showSuccessMsg(User user) {
        Yuanye.showToast(this, "欢迎"+user.getUsername());
    }

    @Override
    public void showFailedMsg(String s) {
        Yuanye.showToast(this, s);
    }

    @Override
    public void showLoading() {
        pgb.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        pgb.setVisibility(View.GONE);
    }


    @Override
    public void clear() {
        edtUsername.setText("");
        edtPassword.setText("");
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024*1024));
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 8;
        Toast.makeText(this, "cacheSize:"+cacheSize, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return checkBackAction() || super.onKeyDown(keyCode, event);
    }

    private boolean mFlag = false;
    private long mTimeout = -1;
    public boolean checkBackAction() {
        boolean flag = mFlag;
        long time = 2000;
        mFlag = true;
        boolean timeout = (mTimeout == -1 || (System.currentTimeMillis() - mTimeout) > time);
        if (mFlag && (mFlag != flag || timeout)) {
            mTimeout = System.currentTimeMillis();
            Yuanye.showToast(this, "再点击一次回到桌面");
            return true;
        }
        return !mFlag;
    }
}
