package com.thirtyseven.studenthelp.utility;

import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Errand;

import java.util.Stack;

public class Local {
    private static Account account = null;
    private static Errand errand = null;
    private static Stack<Account> accountStack = new Stack<>();
    private static Stack<Errand> errandStack = new Stack<>();

    public static Account loadAccount() {
        return account;
    }

    public static void saveAccount(Account account) {
        Local.account = account;
    }

    public static void pushAccount(Account account) {
        Local.accountStack.push(account);
    }

    public static Account popAccount() {
        return accountStack.pop();
    }

    public static Errand loadErrand() {
        return errand;
    }

    public static void saveErrand(Errand errand) {
        Local.errand = errand;
    }

    public static void pushErrand(Errand errand) {
        Local.errandStack.push(errand);
    }

    public static Errand popErrand() {
        return errandStack.pop();
    }
}
