package com.thirtyseven.studenthelp.data;

import java.math.BigDecimal;

public class Account {
    public String id; // Primary key
    public String password;
    public String nickname;
    public String realName;
    public int credit;
    public BigDecimal capital;

    public String getName() {
        if (nickname != null) return nickname;
        if (realName != null) return realName;
        return id;
    }
}
