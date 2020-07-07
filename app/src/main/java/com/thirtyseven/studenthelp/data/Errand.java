package com.thirtyseven.studenthelp.data;

import java.util.Date;
import java.util.List;


public class Errand {
    public static final String[] TagName = {
            /* 0: */ "二手交易",
            /* 1: */ "快递代领",
            /* 2: */ "竞赛组队",
            /* 3: */ "习题求解",
            /* 4: */ "寻物启示"
    };
    public static final String[] StateName = {
            /* 0: */ "等待中",
            /* 1: */ "进行中",
            /* 2: */ "待评价",
            /* 3: */ "裁决中",
            /* 4: */ "验收失败",
            /* 5: */ "已完成",
            /* 6: */ "待验收"
    };

    public enum State {Waiting, Ongoing, NotEvaluate, Judging, CheckFailed, Complete, ToCheck}

    public enum Tag {SecondHand, Express, Group, Study, LostAndFound}

    // Related accounts
    public Account publisher;
    public Account receiver;
    public List<Account> receiverList; // include receiver
    public List<Account> applierList;
    // Related judge
    public Judge judge;
    // Information
    public String id;
    public String title;
    public String content;
    public Conversation conversation;
    public Date date;
    //Properties
    public Tag tag;
    public List<Integer> tagList; // include tag
    public String money;
    public State state;

    public String getTitle() {
        if (title == null) return "";
        return title;
    }

    public String getContent() {
        if (content != null) return content;
        return "";
    }

    public String getDate() {
        if (date != null) return date.toString();
        return "";
    }

    public String getMoney() {
        if (money != null) return money;
        return "";
    }

    public String getContentPreview() {
        String preview = getContent();
        if (preview.length() <= 200) return preview;
        return preview.substring(0, 200);
    }

    public String getStateName() {
        return StateName[state.ordinal()];
    }

    public String getTagName() {
        return TagName[tag.ordinal()];
    }
}
