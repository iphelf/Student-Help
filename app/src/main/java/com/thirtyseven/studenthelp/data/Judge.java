package com.thirtyseven.studenthelp.data;

import java.util.Date;

public class Judge {
    public enum Status {Waiting, Ongoing, Handled}

    public enum Result {FaultOnPublisher, FaultOnReceiver}

    //    "judgeId": 2,
    public String id;
    //    "judgeErrandId": 3,
    public Errand errand;
    //    "complainantId": "20163933",
    public Account plaintiff; // 原告
    //    "respondentId": "20179026",
    public Account defendant; // 被告
    //    "judgeTitle": "快递未送达",
    public String title;
    //    "judgeReason": "三天还未送快递过来",
    public String reason;
    //    "judgeImage": "http://129.211.5.147:8080/20171722/images/InitAvatar.jpg",
    public String image;
    //    "judgeStatus": 1,
    public Status status;
    //    "judgeResult": 1,
    public Result result;
    //    "resultReason": "因为接单者4天仍然未送达快递，判定为接单者责任。",
    public String resultReason;
    //    "createTime": "2020-07-04T03:20:14.000+00:00",
    public Date createTime;
    //    "updateTime": "2020-07-07T00:04:18.000+00:00"
    public Date updateTime;
}
