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

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {
    Conversation conversation;
    MessageAdapter messageAdapter;
    ListView list_conversation;
    private EditText editTextMsg;
    private Button buttonSend;
    private String msg;
    CustomTitleBar customTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

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

        customTitleBar = findViewById(R.id.customTitleBar);
        customTitleBar.setTitle(R.string.title_conversation);

        customTitleBar.setLeftIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setListener();

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

    public void pull() {
        msg = editTextMsg.getText().toString().trim();
    }

    public void refresh() {
        Remote.remoteBinder.queryConversation(conversation, new Remote.Listener() {
            @Override
            public void execute(Global.ResultCode resultCode, Object object) {
                conversation = (Conversation) object;
                push();
            }
        });
//        Remote.remoteBinder.queryConversationList(
//                Local.loadAccount(), new Remote.Listener() {
//                    @Override
//                    public void execute(Global.ResultCode resultCode, Object object) {
//                        conversation = Local.loadConversationMap().get(conversation.receiver.id);
//                    }
//                }
//        );
    }

    public void add(Message message) {
        conversation.messageList.add(message);
        if (message.sender.equals(conversation.sender))
            messageAdapter.addDataToAdapter(new MsgInfo(null, message.content));
        else
            messageAdapter.addDataToAdapter(new MsgInfo(message.content, null));
        list_conversation.setAdapter(messageAdapter);
        list_conversation.setSelection(list_conversation.getBottom());
    }

    public void push() {
        editTextMsg.setText("");
        messageAdapter = new MessageAdapter(this);
        for (Message message : conversation.messageList) {
            if (message.sender.equals(conversation.sender))
                messageAdapter.addDataToAdapter(new MsgInfo(null, message.content));
            else
                messageAdapter.addDataToAdapter(new MsgInfo(message.content, null));
        }
        list_conversation.setAdapter(messageAdapter);
        list_conversation.setSelection(list_conversation.getBottom());
    }

    Message messageNormal;
    Message messageSign;

    private void setListener() {
        messageNormal = new Message();
        messageNormal.sender = conversation.sender;
        messageNormal.receiver = conversation.receiver;
        messageNormal.type = Message.Type.Chat;
        messageSign = new Message();
        messageSign.sender = conversation.sender;
        messageSign.receiver = conversation.receiver;
        messageSign.type = Message.Type.Sign;
        messageSign.toSignList = new ArrayList<>();
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pull();
                messageNormal.content = msg;
                Remote.remoteBinder.send(messageNormal);
                add(messageNormal);
                editTextMsg.setText("");
            }
        });
        Remote.remoteBinder.subscribe(Local.loadAccount().id, new Remote.Listener() {
            @Override
            public void execute(Global.ResultCode resultCode, Object object) {
                if (!(object instanceof Message)) return;
                final Message message = (Message) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        add(message);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Remote.remoteBinder.unsubscribe(Local.loadAccount().id);
    }
}