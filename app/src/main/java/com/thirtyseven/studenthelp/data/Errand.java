package com.thirtyseven.studenthelp.data;

import java.util.Date;
import java.util.List;


public class Errand {
    // Related accounts
    public Account publisher;
    public Account receiverPrimary;
    public List<Account> receiverList; // include receiverPrimary
    // Information
    public String title;
    public String content;
    public Date date;
    // Properties
    public Tag tagPrimary;
    public List<Tag> tag; // include tagPrimary
    public Type type;
    public int amount; // associated with type
    public State state;
    public enum State {Waiting, Ongoing, Complete, Deleted}
    public enum Type {Deal, Group}
}
