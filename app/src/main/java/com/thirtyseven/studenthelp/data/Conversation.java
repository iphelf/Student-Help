package com.thirtyseven.studenthelp.data;

import java.util.Date;
import java.util.List;

public class Conversation {
    public Account sender;
    public Account receiverPrimary;
    public String id;
    public Date time;
    public List<Message> messageList;
    public Message messageLatest;

    public Account getSender(){
        return sender;
    }
    public Account getReceiverPrimary(){
        return receiverPrimary;
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
}
