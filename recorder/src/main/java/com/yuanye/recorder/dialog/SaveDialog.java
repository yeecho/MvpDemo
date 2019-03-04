package com.yuanye.recorder.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.yuanye.recorder.R;

public class SaveDialog extends AlertDialog{

    private EditText editText;

    protected SaveDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save);
        editText = findViewById(R.id.dialog_save_edt);
        setTitle("保存录音");
    }

    public String getName(){
        return editText.getText().toString();
    }
}
