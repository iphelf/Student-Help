package com.thirtyseven.studenthelp.ui.me;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private List<CharSequence> skinList = null;
    private ArrayAdapter<CharSequence> skinAdapter = null;
    private Spinner skinSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        skinSpinner = findViewById(R.id.spinner_skin);
        skinSpinner.setPrompt("请选择更换的颜色");
        skinList = new ArrayList<CharSequence>();
        skinList.add("蓝色");
        skinList.add("红色");
        skinList.add("黄色");
        skinList.add("紫色");
        skinList.add("黑色");
        skinAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, skinList);
        skinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skinSpinner.setAdapter(skinAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}