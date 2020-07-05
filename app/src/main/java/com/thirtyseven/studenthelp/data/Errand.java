package com.thirtyseven.studenthelp.data;

import java.math.BigDecimal;
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
    public String tag;
    public List<Integer> tagList; // include tag
    public BigDecimal money;
    public String state;

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
        if (money != null) return money.toString();
        return "";
    }

    public String getContentPreview() {
        String preview = getContent();
        if (preview.length() <= 200) return preview;
        return preview.substring(0, 200);
    }

    public String getStateName() {
        if (state == null || state.length() == 0 || state.equals("%"))
            return "";
        else return StateName[Integer.parseInt(state)];
    }

    public String getTagName() {
        if (tag == null || tag.length() == 0 || tag.equals("%"))
            return "";
        else return TagName[Integer.parseInt(tag)];
    }

    static public String tagValueOf(int i) {
        return i == -1 ? "%" : String.valueOf(i);
    }

    static public String stateValueOf(int i) {
        return i == -1 ? "%" : String.valueOf(i);
    }
}
