package com.yuanye.a2150.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuanye.a2150.R;

import java.util.List;

public class WifiAdapter extends BaseAdapter {
    private Context context;
    private List<ScanResult> list;

    public WifiAdapter(Context context, List<ScanResult> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<ScanResult> list){
        this.list = list;
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
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_wifi_adapter, null);
            holder.name = view.findViewById(R.id.wifi_name);
            holder.level = view.findViewById(R.id.wifi_level);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(list.get(i).SSID);
        holder.level.setText(translateLevel(list.get(i).level));
        return view;
    }

    String translateLevel(int level){
        String signal;
        if (level <= 0 && level >= -50) {
            signal = "信号很好";
        } else if (level < -50 && level >= -70) {
            signal = "信号较好";
        } else if (level < -70 && level >= -80) {
            signal = "信号一般";
        } else if (level < -80 && level >= -100) {
            signal = "信号较差";
        } else {
            signal = "信号很差";
        }
        return signal;
    }

    class ViewHolder{
        TextView name;
        TextView level;
    }
}
