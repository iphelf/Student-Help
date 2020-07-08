package com.thirtyseven.studenthelp.data;

import java.math.BigDecimal;

public class Account {
    public String id; // Primary key
    public String password;
    public String nickname;
    public String realName;
    public String credit;
    public String capital;
    public String userImage;

    public String getName() {
        if (nickname != null) return nickname;
        if (realName != null) return realName;
        return id;
    }

    public String getCredit() {
        if (credit != null) return credit;
        return "";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) return false;
        return ((Account) object).id.equals(id);
    }

}
