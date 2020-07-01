package com.thirtyseven.studenthelp.utility;

import com.thirtyseven.studenthelp.data.Account;

public class Local {
    private static Account account = null;

    public static Account loadAccount() {
        return account;
    }

    public static void saveAccount(Account account) {
        Local.account = account;
    }
}
