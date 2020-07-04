package com.thirtyseven.studenthelp.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class Errand {
    // Related accounts
    public Account publisher;
    public Account receiverPrimary;
    public List<Account> receiverList; // include receiverPrimary
    // Information
    public String id;
    public String title;
    public String content;
    public Conversation conversation;
    public Date date;
    // Properties
    public Tag tagPrimary;
    public List<Tag> tag; // include tagPrimary
    public BigDecimal money;
    public State state;
    public enum State {Waiting, Ongoing, Complete, Deleted}
}
