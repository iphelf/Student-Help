package com.thirtyseven.studenthelp.ui.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thirtyseven.studenthelp.R;

public class AnnouncementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        setTitle(R.string.title_announcement);
    }
}