package com.thirtyseven.studenthelp.data;

import java.util.Date;

public class Message {
    public String id;
    public Account sender;
    public Account receiver;
    public String content;
    public Date date;
    public int type;
    public boolean read;

    //public enum Type {Normal, Complaint}
    public interface Type {
        int Connect = 1;
        int Chat = 2;
        int Signed = 3;
        int KeepAlive = 4;

        // Receiver
        int Apply = 5;
        int Resign = 61;
        int Submit = 8;
        int Complain = 9;
        // Publisher
        int AcceptApplication = 6;
        int Dismiss = 71;
        int RefuseSubmission = 99;
    }

    public String pack() {
//        webSocket.send("{\"action\":1,\"chatMsg\":{\"senderId\":\"20176151\",\"receiverId\":\"20171745\",\"msg\":\"Hello, this is iphelf\",\"msgId\":null},\"extend\":null}");
        return "";
    }
}
