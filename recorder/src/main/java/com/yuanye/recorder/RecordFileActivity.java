package com.yuanye.recorder;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yuanye.recorder.utils.Yuanye;
import com.yuanye.yeecho.base.BaseActivity;
import com.yuanye.yeecho.base.BasePresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFileActivity extends Activity{

    private RecyclerView recyclerView;
    private List list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordfile);
        recyclerView = findViewById(R.id.recycleview);
        if (list == null){
            list = Yuanye.getMyRecordFiles();
        }
    }

}
