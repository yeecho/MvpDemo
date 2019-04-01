package com.yuanye.a2150;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuanye.a2150.util.Yeecho;
import com.yuanye.a2150.view.CameraPreview;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CameraActivity extends AppCompatActivity{

    private CameraPreview cameraPreview;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("相机测试");
        setContentView(R.layout.activity_camera);
        cameraPreview = findViewById(R.id.camera_preview);
        cameraPreview.setShowPhotoHandler(new showPhotoHandler());
        imageView = findViewById(R.id.imageview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreview.onResume(this);
    }

    @Override
    protected void onPause() {
        cameraPreview.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void topClick(View v){
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnPass:
                intent.putExtra(Yeecho.TEST_RESULT, true);
                setResult(Yeecho.ResultCode_Camera, intent);
                finish();
                break;
            case R.id.btnFail:
                intent.putExtra(Yeecho.TEST_RESULT, false);
                setResult(Yeecho.ResultCode_Camera, intent);
                finish();
                break;
            case R.id.btnTest:
                try{
                    cameraPreview.takePicture();
                }catch (Exception e){
//                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    class showPhotoHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImageBitmap(getLoacalBitmap(getExternalFilesDir(null)+ "/pic.jpg"));
        }
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
