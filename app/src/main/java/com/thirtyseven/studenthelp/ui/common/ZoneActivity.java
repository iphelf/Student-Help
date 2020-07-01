package com.thirtyseven.studenthelp.ui.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.me.HistoryActivity;

public class ZoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        setTitle(R.string.title_zone);
        Button buttonDetail = findViewById(R.id.button_detail);
        buttonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ZoneActivity.this, ErrandActivity.class);
                startActivity(intent);
            }
        });
    }
}