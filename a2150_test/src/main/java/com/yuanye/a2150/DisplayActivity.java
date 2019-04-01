package com.yuanye.a2150;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanye.a2150.util.Yeecho;

public class DisplayActivity extends AppCompatActivity {

    private int[] colors = new int[]{R.color.white, R.color.red, R.color.orange, R.color.yellow,
                                    R.color.green, R.color.cyan, R.color.blue, R.color.purple, R.color.black};
    private int count = 0;
    private Handler handler;
    private TextView textView;
    private View custom_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("显示屏测试");
        setContentView(R.layout.activity_display);
        textView = findViewById(R.id.textview);
        custom_toolbar = findViewById(R.id.custom_toolbar);
        handler = new Handler();
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Display, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Display, intent);
                finish();
                break;
            case R.id.btnTest:
                enterTestModel();
                break;
        }

    }

    private void enterTestModel(){
        getSupportActionBar().hide();//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
        custom_toolbar.setVisibility(View.GONE);
        textView.setText("");
        textView.setBackground(getDrawable(colors[0]));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < 8){
                    count++;
                }else{
                    quitTestModel();
                }
                textView.setBackground(getDrawable(colors[count]));
            }
        });
    }

    private void quitTestModel() {
        count = 0;
        custom_toolbar.setVisibility(View.VISIBLE);
        getSupportActionBar().show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN); // 显示状态栏
        textView.setText(getResources().getText(R.string.test_tips));
        textView.setBackground(getDrawable(colors[0]));
        textView.setOnClickListener(null);
    }
}
