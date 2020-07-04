package com.thirtyseven.studenthelp.ui.notice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;

public class AnnouncementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        setTitle(R.string.title_announcement);
    }
}