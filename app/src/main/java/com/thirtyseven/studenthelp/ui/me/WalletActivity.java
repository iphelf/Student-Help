package com.thirtyseven.studenthelp.ui.me;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle(R.string.title_wallet);
    }
}