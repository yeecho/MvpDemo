package com.yuanye.a2150.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yuanye.a2150.R;

import java.util.ArrayList;

public class AudioFlyView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private String tag = "AudioFlyView";
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint,paint2;
    private boolean isDrawing;
    private int vol = 0;
    private int count = 0;
    private ArrayList vols = new ArrayList();
    int width,height;

    public AudioFlyView(Context context) {
        this(context, null);
    }

    public AudioFlyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioFlyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioFlyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(false);
        setFocusableInTouchMode(false);
        this.setKeepScreenOn(true);
//        setLayerType(LAYER_TYPE_HARDWARE, null);
        setBackgroundResource(R.drawable.audio_fly_bg);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1f);
        paint.setTextSize(64);
        paint2 = new Paint();
        paint2.setColor(Color.RED);
        paint2.setStrokeWidth(2f);
        paint2.setTextSize(48);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        Log.d(tag, "w*h:"+width+"*"+height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(tag, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(tag, "surfaceChanged:"+">i:"+i+" >i1"+i1+" >i2"+i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(tag, "surfaceDestroyed");
    }

    @Override
    public void run() {
//        clearCanvas();
        vols.clear();
        while (isDrawing){
            draw();
        }
    }

    private void draw() {
        try {
            if (vols.size() >= width/2){
                vols.remove(0);
            }
            vols.add(vol);
            canvas = surfaceHolder.lockCanvas();
            if (canvas != null){
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawLine(0, height/2, width, height/2, paint);
                for (int i = 0; i < vols.size(); i++){
                    canvas.drawLine(width-i,height/2-(int) vols.get(i), width-i, height/2+(int) vols.get(i),paint);
                }
                canvas.drawLine(width/2, 0, width/2, height, paint2);
//                canvas.drawText("Vol:"+vol,width/6, height/4, paint2);
            }
            Thread.sleep(33);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (canvas != null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void setVol(double vol){
        this.vol = (int) vol;
        double v = vol/20 * (vol/20) *6;
        this.vol = (int) v;
    }

    public boolean isReady(){
        return isDrawing;
    }

    public void start(){
        isDrawing = true;
        new Thread(this).start();
    }

    public void stop(){
        isDrawing = false;
    }

    public void clearCanvas(){
        canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}
