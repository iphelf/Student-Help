package com.thirtyseven.studenthelp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class Conversation {
    public Account sender;
    public Account receiver;
    public String id;
    public Date time;
    public List<Message> messageList;
    public Message messageLatest;

    public Account getSender(){
        return sender;
    }
    public Account getReceiver(){
        return receiver;
    }
    public String getId(){
        if (id != null) return id;
        return "";
    }
    public Date getTime(){
        return time;
    }
    public List<Message> getMessageList(){
        return messageList;
    }
    public Message getMessage(){
        return messageLatest;
    }

    public String packSignature(){
        Message messageSignature=new Message();
        messageSignature.type= Message.Type.Sign;
        messageSignature.sender=sender;
        messageSignature.receiver=receiver;
        messageSignature.toSignList=messageList;
        return messageSignature.pack();
    }
}
