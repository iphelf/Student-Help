package com.thirtyseven.studenthelp.ui.common;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class ErrandActivity extends AppCompatActivity {

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

    Errand errand;

    ImageView imageViewAvatar;
    TextView textViewPublisher;
    TextView textViewCredit;
    TextView textViewDate;
    TextView textViewTitle;
    TextView textViewState;
    TextView textViewTag;
    TextView textViewMoney;
    TextView textViewContent;

    Button buttonConversation;
    Button buttonDelete;
    Button buttonDismiss;
    Button buttonApply;
    Button buttonResign;
    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand);
        setTitle(R.string.title_errand);

        errand = Local.popErrand();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                remoteBinder = (Remote.RemoteBinder) iBinder;
                remoteBinder.startConversation();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(
                new Intent(this, Remote.class),
                serviceConnection,
                Service.BIND_AUTO_CREATE
        );

        imageViewAvatar = findViewById(R.id.imageView_avatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ErrandActivity.this, ZoneActivity.class);
                startActivity(intent);
            }
        });

        textViewPublisher = findViewById(R.id.textView_author);
        textViewCredit = findViewById(R.id.textView_credit);
        textViewDate = findViewById(R.id.textView_date);
        textViewTitle = findViewById(R.id.textView_title);
        textViewState = findViewById(R.id.textView_state);
        textViewTag = findViewById(R.id.textView_tag);
        textViewMoney = findViewById(R.id.textView_money);
        textViewContent = findViewById(R.id.textView_content);

        buttonConversation = findViewById(R.id.button_conversation);
        buttonConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ErrandActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });

        buttonDelete = findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_delete, Toast.LENGTH_SHORT).show();
            }
        });

        buttonDismiss = findViewById(R.id.button_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_dismiss, Toast.LENGTH_SHORT).show();
            }
        });

        buttonApply = findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_apply, Toast.LENGTH_SHORT).show();
            }
        });

        buttonResign = findViewById(R.id.button_resign);
        buttonResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_resign, Toast.LENGTH_SHORT).show();
            }
        });

        buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_submit, Toast.LENGTH_SHORT).show();
            }
        });

        push();

    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public void push() {
        textViewPublisher.setText(errand.publisher.getName());
        textViewCredit.setText(errand.publisher.getCredit());
        textViewDate.setText(errand.getDate());
        textViewTitle.setText(errand.getTitle());
        textViewState.setText(errand.getStateName());
        textViewTag.setText(errand.getTagName());
        textViewMoney.setText(errand.getMoney());
        textViewContent.setText(errand.getContent());
    }
}