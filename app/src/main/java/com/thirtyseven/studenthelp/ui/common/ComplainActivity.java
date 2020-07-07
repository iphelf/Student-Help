package com.thirtyseven.studenthelp.ui.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.utility.Local;

public class ComplainActivity extends AppCompatActivity {

    Errand errand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        setTitle(getString(R.string.title_complain));

        errand= Local.popErrand();
    }
}