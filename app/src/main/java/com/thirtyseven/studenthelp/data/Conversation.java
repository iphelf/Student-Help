package com.thirtyseven.studenthelp.data;

import java.util.List;

public class Conversation {
    public Account publisher;
    public Account receiverPrimary;
    public String id;
    public List<Message> messageList;
    public Message messageLatest;
}
