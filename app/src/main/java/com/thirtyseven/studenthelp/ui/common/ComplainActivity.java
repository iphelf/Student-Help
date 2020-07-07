package com.thirtyseven.studenthelp.ui.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thirtyseven.studenthelp.R;

public class ComplainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        setTitle(getString(R.string.title_complain));
    }
}