package com.thirtyseven.studenthelp.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class Errand {
    // Related accounts
    public Account publisher;
    public Account receiver;
    public List<Account> receiverList; // include receiver
    // Information
    public String id;
    public String title;
    public String content;
    public Conversation conversation;
    public Date date;
    //Properties
    public int tag;
    public List<Integer> tagList; // include tag
    public BigDecimal money;
    public State state;
    public enum State {Waiting, Ongoing, NotEvaluate,Judging,CheckFailed,Complete}
}
