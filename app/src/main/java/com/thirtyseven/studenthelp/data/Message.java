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
}
