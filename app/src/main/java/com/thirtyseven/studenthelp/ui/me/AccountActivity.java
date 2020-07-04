package com.thirtyseven.studenthelp.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;
import com.thirtyseven.studenthelp.ui.common.ZoneActivity;

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
        int[] fieldIds = {
                R.id.textView_tag
        };
        List<Map<String, Object>> mapList = new ArrayList<>();
        for(int i=0;i<values.length;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("title", values[i]);
            mapList.add(map);
        }


        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                mapList,
                R.layout.listviewitem_account,
                new String[]{"title"},
                fieldIds
        );
        ListView listViewAccountList = findViewById(R.id.listView_account);
        listViewAccountList.setAdapter(simpleAdapter);

    }
}