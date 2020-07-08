package com.thirtyseven.studenthelp.ui.common;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.data.Message;
import com.thirtyseven.studenthelp.data.MsgInfo;
import com.thirtyseven.studenthelp.ui.common.adapter.MessageAdapter;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class ConversationActivity extends AppCompatActivity {
    private EditText editTextMsg;
    private Button buttonSend;
    private String msg;
    Conversation conversation;
    MessageAdapter messageAdapter;
    ListView list_conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setTitle(R.string.title_conversation);

        conversation = new Conversation();
        conversation.sender = Local.loadAccount();
        conversation.receiver = Local.popAccount();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextMsg = findViewById(R.id.editText_message);
        buttonSend = findViewById(R.id.button_send);

        list_conversation = findViewById(R.id.listView_conversation);

        final MessageAdapter adapter = new MessageAdapter(ConversationActivity.this);
        list_conversation.setAdapter(adapter);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = editTextMsg.getText().toString().trim();
                adapter.addDataToAdapter(new MsgInfo(null, msg));
                adapter.notifyDataSetChanged();
                list_conversation.smoothScrollToPosition(list_conversation.getCount() - 1);
                editTextMsg.setText("");
            }
        });

        refresh();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void pull() {
    }

    public void refresh() {
        Remote.remoteBinder.queryConversation(conversation, new Remote.Listener() {
            @Override
            public void execute(Global.ResultCode resultCode, Object object) {
                conversation = (Conversation) object;
                push();
            }
        });
    }

    public void push() {
        messageAdapter = new MessageAdapter(this);
        for (Message message : conversation.messageList) {
            if (message.sender.equals(conversation.sender))
                messageAdapter.addDataToAdapter(new MsgInfo(null, message.content));
            else
                messageAdapter.addDataToAdapter(new MsgInfo(message.content, null));
        }
        list_conversation.setAdapter(messageAdapter);
    }

}