package com.thirtyseven.studenthelp.utility;

import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.data.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Local {
    private static Account account = null;
    private static Errand errand = null;
    private static Stack<Account> accountStack = new Stack<>();
    private static Stack<Errand> errandStack = new Stack<>();
    private static Map<String, Conversation> conversationMap = null;
    private static List<Conversation> conversationList=null;
    public static Account loadAccount() {
        return account;
    }

    public static void saveAccount(Account account) {
        Local.account = account;
    }

    public static Map<String, Conversation> loadConversationMap() {
        return conversationMap;
    }

    public static void saveConversationMap(Map<String, Conversation> map) {
        Local.conversationMap = map;
    }
    public static void saveConversationList(List<Conversation> list){
        Local.conversationList=list;
    }
    public static List<Conversation> loadConversationList(){
//        conversationList=new ArrayList<>();
//        Conversation conversation=new Conversation();
//        conversation.sender=new Account();
//        conversation.sender.id="20176151";
//        conversation.messageLatest=new Message();
//        conversation.messageLatest.content="Content";
//        for(int i=0;i<50;i++)
//            conversationList.add(conversation);
        return  conversationList;
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
        if (!errandStack.isEmpty())
            return errandStack.pop();
        return null;
    }

    public static Errand topErrand() {
        return errandStack.peek();
    }
}
