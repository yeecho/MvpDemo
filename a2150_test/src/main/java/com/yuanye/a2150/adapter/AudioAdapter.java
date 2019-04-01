package com.yuanye.a2150.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanye.a2150.R;
import com.yuanye.a2150.util.Yuanye;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class AudioAdapter extends BaseAdapter{

    private String tag = AudioAdapter.class.getName();
    private Context context;
    private List<String> list;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isPause = false;
    private Handler handler;

    public AudioAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        handler = new Handler();
    }

    public void updateList(List list){
        this.list = list;
        notifyDataSetChanged();
    }

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isPlaying = true;
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(tag, "播放完了");
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_recordfile, null);
            holder.play = view.findViewById(R.id.play);
            holder.stop = view.findViewById(R.id.stop);
            holder.fileName = view.findViewById(R.id.file_name);
            holder.currentTIme = view.findViewById(R.id.current_position);
            holder.durationTime = view.findViewById(R.id.duration);
            holder.delete = view.findViewById(R.id.delete);
            holder.progress = view.findViewById(R.id.progress);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        final String filePath = list.get(i).toString();
        holder.fileName.setText(Yuanye.pathToName(filePath));
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                int progress = 0;
                if (duration != 0){
                    progress = currentPosition * 100 /duration;
                }
                holder.currentTIme.setText(Yuanye.translateTime(currentPosition/1000));
                holder.durationTime.setText(Yuanye.translateTime(duration/1000));
                holder.progress.setProgress(progress);
                if (progress < 100){
                    handler.postDelayed(this, 1000);
                }else{
                    isPlaying = false;
                    holder.progress.setProgress(0);
                    holder.currentTIme.setText(Yuanye.translateTime(0));
                    holder.play.setImageResource(R.drawable.item_play);
                    handler.removeCallbacks(this);
                    Log.d(tag,"播放完了。。移除runnable");
                }

            }
        };
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    isPlaying = false;
                    mediaPlayer.pause();
                    holder.play.setImageResource(R.drawable.item_play);
                    handler.removeCallbacks(runnable);
                }else{
                    playForDetail(filePath);
                    isPlaying = true;
                    holder.play.setImageResource(R.drawable.item_pause);
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 1000);
                }
            }
        });
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                isPlaying = false;
                holder.progress.setProgress(0);
                holder.currentTIme.setText(Yuanye.translateTime(0));
                holder.play.setImageResource(R.drawable.item_play);
                handler.removeCallbacks(runnable);
                Toast.makeText(context,"停止播放", Toast.LENGTH_SHORT).show();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Yuanye.deleteFile(filePath)){
                    updateList(Yuanye.getMyRecordFiles(context));
                }
            }
        });
        return view;
    }

    private void playForDetail(String filePath) {
        if (mediaPlayer == null){
            initMediaPlayer();
        }
        if (isPause){
            isPause = false;
            mediaPlayer.start();
        }else{
            mediaPlayer.reset();
            try {
                FileInputStream fis = new FileInputStream(new File(filePath));
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ViewHolder{
        ImageView play, stop;
        TextView fileName,currentTIme,durationTime;
        Button delete;
        ProgressBar progress;
    }
}
