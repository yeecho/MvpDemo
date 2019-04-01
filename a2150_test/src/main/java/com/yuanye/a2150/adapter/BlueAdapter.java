package com.yuanye.a2150.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuanye.a2150.R;

import java.util.List;

public class BlueAdapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> list;

    public BlueAdapter(Context context, List<BluetoothDevice> list) {
        this.context = context;
        this.list = list;
    }

    public void updateList(List<BluetoothDevice> list){
        this.list = list;
        notifyDataSetChanged();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_blue_adapter, null);
            holder.blueName = view.findViewById(R.id.blue_name);
            holder.blueAddress = view.findViewById(R.id.blue_address);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        String name = list.get(i).getName();
        if (name == null || name.equals("")){
            name = "未知设备";
        }
        holder.blueName.setText(name);
        holder.blueAddress.setText(list.get(i).getAddress());
        return view;
    }

    class ViewHolder{
        TextView blueName;
        TextView blueAddress;
    }
}
