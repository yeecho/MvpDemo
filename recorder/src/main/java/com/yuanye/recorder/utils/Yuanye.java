package com.yuanye.recorder.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Yuanye {

    public static final String CUSTOM_PATH = "sdcard/a_myrecord/";
    public static final String SP_NAME = "shared_data";
    public static final String SP_SAMPLE_RATE = "sample_rate";
    public static final String SP_ADDRESS = "address";
    public static final long SAMPLE_RATE_8Hz = 8000;
    public static final long SAMPLE_RATE_24Hz = 24000;
    public static final long SAMPLE_RATE_48Hz = 48000;


    public static String translateTime(int i){
        String time = "时间过长";
        String minute = "";
        String second = "";
        if (i < 3600){
            int m = i / 60;
            if (m < 10){
                minute = "0"+m;
            }else{
                minute = ""+m;
            }
            int s = i % 60;
            if (s < 10){
                second = "0"+s;
            }else{
                second = ""+s;
            }
        }
        time = minute + ":" + second;
        return time;
    }

    public static void writeWav(String fileTargetName, String fileWavName, long longSampleRate) {
        File fileTarget = new File(CUSTOM_PATH, fileTargetName);
        File fileWav = makeFileFromPath(CUSTOM_PATH, fileWavName+".wav");
        if (!fileTarget.exists()) {
            Log.e("yuanye", "目标文件不存在");
            return;
        }
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileTarget)));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileWav)));
            int len = dataInputStream.available();
            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            int channels = 1;
            long byteRate = 16 * longSampleRate * channels / 8;
            //写wav头部
            writeWavHeader(dataOutputStream, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            byte[] bytes = new byte[1024];
            int lenthg = -1;
            while ((lenthg = dataInputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes, 0, lenthg);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] writeWavHeader(DataOutputStream dataOutputStream, long totalAudioLen, long totalDataLen, long longSampleRate,
                                  int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        //RIFF WAVE Chunk
        // RIFF标记占据四个字节
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        //数据大小表示，由于原始数据为long型，通过四次计算得到长度
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        //WAVE标记占据四个字节
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f';
        // 'fmt '标记符占据四个字节
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data标记符
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        //数据长度
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        dataOutputStream.write(header, 0, 44);
        return header;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    // 生成文件
    public static File makeFileFromPath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static List getMyRecordFiles() {
        File file = new File(CUSTOM_PATH);
        List list = new ArrayList();
        if (file.exists()){
            File[] files = file.listFiles();
            if (files.length > 0){
                for (File f : files){
                    if (f.getName().endsWith(".wav"))
                        list.add(f.getName());
                }
                return list;
            }
        }

        return null;
    }

    private static Toast toast;
    /**
     * 显示Toast
     * @param context 上下文
     * @param content 要显示的内容
     */
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /**
     * 显示Toast
     * @param context 上下文
     * @param resId 要显示的资源id
     */
    public static void showToast(Context context, int resId) {
        showToast(context, (String) context.getResources().getText(resId));
    }
}
