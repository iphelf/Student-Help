package com.thirtyseven.studenthelp.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle(R.string.title_history);

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
        ListView listViewErrandList = findViewById(R.id.listView_errandList);
        listViewErrandList.setAdapter(simpleAdapter);
        listViewErrandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(HistoryActivity.this, ErrandActivity.class);
                startActivity(intent);
            }
        });
    }
}