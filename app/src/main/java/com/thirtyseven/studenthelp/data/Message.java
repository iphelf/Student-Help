package com.thirtyseven.studenthelp.data;

import java.util.Date;

public class Message {
    public enum Type{Normal,Complaint}

    public Account sender;
    public Account receiver;

    public String content;
    public Date date;
    public Type type;
}
