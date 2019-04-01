package com.yuanye.a2150.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanye.a2150.R;

import java.io.File;
import java.util.List;

public class FilePreviewAdapter extends BaseAdapter {

    private Context context;
    private List<File> list;

    public FilePreviewAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    public void upateData(List<File> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_file_adapter, null);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.imageview);
            holder.textView = view.findViewById(R.id.textview);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        File file = list.get(i);
        if (file.isDirectory()){
            holder.imageView.setImageResource(R.drawable.folder);
        }else{
            holder.imageView.setImageResource(R.drawable.file);
        }
        holder.textView.setText(file.getName());
        return view;
    }

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
