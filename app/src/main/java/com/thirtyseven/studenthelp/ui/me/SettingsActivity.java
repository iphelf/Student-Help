package com.thirtyseven.studenthelp.ui.me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thirtyseven.studenthelp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_settings);
    }
}