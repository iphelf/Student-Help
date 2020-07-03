package com.thirtyseven.studenthelp.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.thirtyseven.studenthelp.R;

public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_publish);
        setTitle(R.string.title_publish);
        Button buttonPublish = findViewById(R.id.button_publish);
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String[] tags = {
                "全部", "代领快递", "寻物启事", "二手交易", "组队征集", "学习辅导", "问卷调查", "其他"
        };
        Spinner spinnerTag = findViewById(R.id.spinner_tag);
        ArrayAdapter<String> arrayAdapterTag = new ArrayAdapter<>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                tags
        );
        spinnerTag.setAdapter(arrayAdapterTag);

    }
}