package com.thirtyseven.studenthelp.ui.common;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.MsgInfo;
import com.thirtyseven.studenthelp.ui.common.adapter.MessageAdapter;

public class ConversationActivity extends AppCompatActivity {
    private EditText msg_edit;
    private Button left_btn;
    private Button right_btn;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setTitle(R.string.title_conversation);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        msg_edit = findViewById(R.id.editText_message);
        left_btn = findViewById(R.id.btn_left);
        right_btn = findViewById(R.id.btn_right);

        final ListView list_conversation = findViewById(R.id.listView_conversation);
        ;
        final MessageAdapter adapter = new MessageAdapter(ConversationActivity.this);
        list_conversation.setAdapter(adapter);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = msg_edit.getText().toString().trim();
                adapter.addDataToAdapter(new MsgInfo(msg, null));
                adapter.notifyDataSetChanged();
                list_conversation.smoothScrollToPosition(list_conversation.getCount() - 1);
                msg_edit.setText("");
            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = msg_edit.getText().toString().trim();
                adapter.addDataToAdapter(new MsgInfo(null, msg));
                adapter.notifyDataSetChanged();
                list_conversation.smoothScrollToPosition(list_conversation.getCount() - 1);
                msg_edit.setText("");
            }
        });

    }
}