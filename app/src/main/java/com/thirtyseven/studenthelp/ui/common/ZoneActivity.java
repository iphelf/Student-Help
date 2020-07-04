package com.thirtyseven.studenthelp.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String[] fields = {
                "Thumbnail", "Title", "State", "Author", "Preview", "Money"
        };
        int[] fieldIds = {
                R.id.imageView_thumbnail,
                R.id.textView_title,
                R.id.textView_state,
                R.id.textView_author,
                R.id.textView_preview,
                R.id.textView_money
        };
        List<Map<String, Object>> mapList = new ArrayList<>();
        int n = 50;
        for (int i = 0; i < n; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(fields[0], R.drawable.ic_logo);
            for (int j = 1; j < fields.length; j++) map.put(fields[j], fields[j]);
            mapList.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                mapList,
                R.layout.listviewitem_errand,
                fields,
                fieldIds
        );
        ListView listViewZoneList = findViewById(R.id.listView_zoneList);
        listViewZoneList.setAdapter(simpleAdapter);
        listViewZoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ZoneActivity.this, ErrandActivity.class);
                startActivity(intent);
            }
        });
    }
}