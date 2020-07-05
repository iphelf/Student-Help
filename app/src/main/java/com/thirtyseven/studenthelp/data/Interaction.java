package com.thirtyseven.studenthelp.data;

public class Interaction {
    public enum Type{Application,Acceptance,Rejection,Submission,Dismissal}
    public Errand errand;
    public Account sender;
    public Account receiver;
    public Type type;
    public boolean read;
}
