package com.thirtyseven.studenthelp.data;

public class Progress {
    public enum Type{StateUpdate,InteractionUpdate}
    public String id;
    public String title;
    public Type type;
}
