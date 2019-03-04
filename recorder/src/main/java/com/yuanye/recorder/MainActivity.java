package com.yuanye.recorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanye.recorder.dialog.SaveDialog;
import com.yuanye.recorder.presenter.AudioRecordPresenter;
import com.yuanye.recorder.utils.Yuanye;
import com.yuanye.recorder.view.RecordView;
import com.yuanye.yeecho.base.BaseActivity;
import com.yuanye.yeecho.base.BasePresenter;

import java.util.zip.Inflater;

public class MainActivity extends BaseActivity<AudioRecordPresenter> implements RecordView{

    private TextView txtTime;
    private ImageView imgAudio;
    private Group loadingGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitle();
        initView();
    }

    private void initView() {
        txtTime = findViewById(R.id.main_txt);
        imgAudio = findViewById(R.id.main_click1);
        loadingGroup = findViewById(R.id.group_loading);
    }

    private void initTitle() {
        TextView title = findViewById(R.id.page_title);
        title.setText("录音终端");
        ImageView setting = findViewById(R.id.page_right);
        setting.setBackgroundResource(R.drawable.setting2);
    }

    @Override
    protected AudioRecordPresenter creatPresenter() {
        return new AudioRecordPresenter(this);
    }

    public void mainClick(View view){
        switch (view.getId()){
            case R.id.main_click1:
                mPresenter.btn1();
                break;
            case R.id.page_right:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
    }

    @Override
    public void setImgRecording(boolean b) {
        if (b){
            imgAudio.setImageResource(R.drawable.stop);
        }else{
            imgAudio.setImageResource(R.drawable.start);
        }
    }

    @Override
    public void showTime(int i) {
        txtTime.setText(Yuanye.translateTime(i));
    }


    private Dialog dialog;
    @Override
    public void showSaveDialog() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_save, null);
        final EditText edt = view.findViewById(R.id.dialog_save_edt);
        dialog = new AlertDialog.Builder(this)
                .setTitle("发送录音")
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            dialog.dismiss();
                            mPresenter.clearTime();
                        }
                    }
                }).setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null){
                            dialog.dismiss();
                        }
                        mPresenter.save(edt.getText().toString());
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void showLoading(boolean b, String msg) {
         loadingGroup.setVisibility(b ? View.VISIBLE : View.GONE);
         Yuanye.showToast(this, msg);
    }
}
