package com.thirtyseven.studenthelp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class Message {
    public String id;
    public Account sender;
    public Account receiver;
    public String content;
    public Date date;
    public int type;
    public boolean read;
    public List<Message> toSignList;

    public interface Type {
        int Connect = 1;
        int Chat = 2;
        int Sign = 3;
        int KeepAlive = 4;
        // Receiver
        int Apply = 5;
        int Resign = 61;
        int Submit = 8;
        int Complain = 9;
        // Publisher
        int AcceptApplication = 6;
        int Dismiss = 71;
        int RefuseSubmission = 99;
    }

    public String pack() {
        JSONObject jsonObjectWebSocket = new JSONObject();
        JSONObject jsonObjectMessage = new JSONObject();
        try {
            jsonObjectWebSocket.put("action", type);
            jsonObjectMessage.put("senderId", sender.id);
            jsonObjectMessage.put("receiverId", receiver.id);
            if (type == Type.Sign) {
                jsonObjectMessage.put("msg", null);
                JSONArray jsonArrayToSign = new JSONArray();
                for (Message message : toSignList)
                    jsonArrayToSign.put(message.id);
                jsonObjectWebSocket.put("extend", jsonArrayToSign);
            } else {
                jsonObjectMessage.put("msg", content);
                jsonObjectWebSocket.put("extend", null);
            }
            jsonObjectMessage.put("msgId", null);
            jsonObjectWebSocket.put("chatMsg", jsonObjectMessage);
        } catch (JSONException e) {
        }
        return jsonObjectWebSocket.toString();
    }

    public static Message unpack(String string) {
        // {"msg":"Content","senderId":"20171745","receiverId":"20171745","msgId":"1594176531109-20171745-20171745-1209"}
        Message message = new Message();
        try {
            JSONObject jsonObjectWebSocket = new JSONObject(string);
            message.id = jsonObjectWebSocket.getString("msgId");
            message.content = jsonObjectWebSocket.getString("msg");
            message.sender = new Account();
            message.sender.id = jsonObjectWebSocket.getString("senderId");
            message.receiver = new Account();
            message.receiver.id = jsonObjectWebSocket.getString("receiverId");
        } catch (JSONException e) {
        }
        return message;
    }
}
