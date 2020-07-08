package com.thirtyseven.studenthelp.data;

import java.util.Date;

public class Comment {
    public enum Type {Self, Opponent}

    //    "commentId": 2,
    public String id;
    //    "errandId": 3,
    public Errand errand;
    //    "userId": "20179026",
    public Account commenter;
    //    "commentScore": 40,
    public Type type;
    //    "type为0自己发表的 为1别人发表的"
    public float score;
    //    "commentContent": "明明是我多次问了他快递送达哪里，他一直不回复。。。",
    public String content;
    //    "createTime": "2020-07-05T01:49:28.000+00:00"
    public Date createTime;
}
