package com.thirtyseven.studenthelp.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.thirtyseven.studenthelp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private List<CharSequence> skinList = null;
    private ArrayAdapter<CharSequence> skinAdapter = null;
    private Spinner skinSpinner= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);

        skinSpinner=findViewById(R.id.spinner_skin);
        skinSpinner.setPrompt("请选择更换的颜色");
        skinList=new ArrayList<CharSequence>();
        skinList.add("蓝色");
        skinList.add("红色");
        skinList.add("黄色");
        skinList.add("紫色");
        skinList.add("黑色");
        skinAdapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,skinList);
        skinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skinSpinner.setAdapter(skinAdapter);
    }
}