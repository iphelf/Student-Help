package com.thirtyseven.studenthelp.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.ui.login.LoginActivity;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//使状态栏透明

        boolean console = false;
        if (console) {
//            Message message=new Message();
//            message.sender=new Account();
//            message.sender.id="Sender";
//            message.receiver=new Account();
//            message.receiver.id="Receiver";
//            message.content="This is the content.";
//            message.type=Message.Type.Connect;
//            Log.d("Log",message.pack());
            return;
        }
        Intent intent = new Intent(MainActivity.this, Remote.class);
        startService(intent);
        Account account = Local.loadAccount();
        if (account == null) {
            intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_me, R.id.navigation_notice)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}