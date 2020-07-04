package com.thirtyseven.studenthelp.ui.me;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle(R.string.title_account);

        String[] values = {
                "家乡", "生日", "性别", "姓名", "学号"
        };
        String[] fields = {"Title"};
        int[] fieldIds = {R.id.textView_tag};
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (String value : values) {
            Map<String, Object> map = new HashMap<>();
            map.put(fields[0], value);
            mapList.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                mapList,
                R.layout.listviewitem_account,
                fields,
                fieldIds
        );
        ListView listViewAccountList = findViewById(R.id.listView_account);
        listViewAccountList.setAdapter(simpleAdapter);

    }
}