package com.yuanye.a2150.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanye.a2150.AudioRecordActivity;
import com.yuanye.a2150.BatteryActivity;
import com.yuanye.a2150.BlueToothActivity;
import com.yuanye.a2150.CameraActivity;
import com.yuanye.a2150.DisplayActivity;
import com.yuanye.a2150.EtherActivity;
import com.yuanye.a2150.HdmiActivity;
import com.yuanye.a2150.HeadsetActivity;
import com.yuanye.a2150.LoudspeakerActivity;
import com.yuanye.a2150.R;
import com.yuanye.a2150.SdcardActivity;
import com.yuanye.a2150.SystemInfoActivity;
import com.yuanye.a2150.TpActivity;
import com.yuanye.a2150.WifiActivity;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.VH>{

    private String tag = "MainAdapter";
    private Activity activity;
    private List<String> names, states;
    private Class[] classes = {SystemInfoActivity.class, DisplayActivity.class, TpActivity.class, CameraActivity.class, WifiActivity.class,
            BlueToothActivity.class, LoudspeakerActivity.class, HeadsetActivity.class, AudioRecordActivity.class, BatteryActivity.class,
            SdcardActivity.class, EtherActivity.class, HdmiActivity.class};

    public MainAdapter(Activity activity, List<String> names, List<String> states){
        this.activity = activity;
        this.names = names;
        this.states = states;
    }

    public void updateStates(List<String> states){
        this.states = states;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_main_adapter, null);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, final int i) {
        vh.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.main_adapter_item_bg));
        if (states.get(i).equals("通过")){
            vh.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.main_adapter_item_bg_pass));
        }else if (states.get(i).equals("失败")){
            vh.relativeLayout.setBackground(activity.getResources().getDrawable(R.drawable.main_adapter_item_bg_fail));
        }
        vh.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTest(i);
            }
        });
        vh.name.setText(names.get(i));
        vh.state.setText(states.get(i));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class VH extends RecyclerView.ViewHolder{
        public RelativeLayout relativeLayout;
        public TextView name,state;

        public VH(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            name = itemView.findViewById(R.id.testName);
            state = itemView.findViewById(R.id.testState);
        }
    }

    void goTest(int i){
        Intent intent = new Intent(activity, classes[i]);
        activity.startActivityForResult(intent, 100);
    }

}
