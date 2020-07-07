package com.thirtyseven.studenthelp.utility;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.data.Judge;
import com.thirtyseven.studenthelp.data.Message;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Remote extends Service implements Global {
    static private RequestQueue requestQueue;
    static private OkHttpClient okHttpClient;
    static private String urlHost;
    static public RemoteBinder remoteBinder = new RemoteBinder();
    private boolean success;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return remoteBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
        okHttpClient = new OkHttpClient();
        urlHost = getString(R.string.urlHost);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface Listener {
        void execute(ResultCode resultCode, Object object);
    }

    public static class RemoteBinder extends Binder {

        String encode(String string) {
            try {
                string = URLEncoder.encode(string, "utf-8");
            } catch (UnsupportedEncodingException ignored) {
            }
            return string;
        }

        private void call(
                String route, int method, String param, JSONObject body,
                final Listener listener
        ) {
            String url = urlHost + route + (param == null ? "" : param);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    method, url, body,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Debug", "successfully return: " + response.toString());
                            listener.execute(ResultCode.Succeeded, response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Debug", "unsuccessfully return: " + error.toString());
                            listener.execute(ResultCode.Failed, null);
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
            Log.d("Debug", "call: " + url);
        }

        public void startConversation() {
            okhttp3.Request request = new okhttp3.Request.Builder().url("ws://129.211.5.147:8088/ws").build();
            RemoteWebSocketListener listener = new RemoteWebSocketListener();
            WebSocket webSocket = okHttpClient.newWebSocket(request, listener);
            webSocket.send("{\"action\":1,\"chatMsg\":{\"senderId\":\"20176151\",\"receiverId\":\"20171722\",\"msg\":\"Hello, this is iphelf\",\"msgId\":null},\"extend\":null}");
            okHttpClient.dispatcher().executorService().shutdown();
        }

        public void responseErrand(Errand errand, JSONObject item) {
            try {
                errand.id = item.getString("errandId");
                errand.title = item.getString("errandTitle");
                errand.tag = Errand.Tag.values()[item.getInt("errandItem")];
                errand.state = Errand.State.values()[item.getInt("errandStatus")];
                errand.content = item.getString("errandDescription");
                errand.publisher = new Account();
                errand.publisher.id = item.getString("publisherId");
                if (item.has("applierId")) {
                    errand.applierList = new ArrayList<>();
                    for (String applierId : item.getString("applierId").split("\\s*,\\s*")) {
                        Account applier = new Account();
                        applier.id = applierId;
                        errand.applierList.add(applier);
                    }
                }
                if (item.has("offerId")) {
                    errand.receiver = new Account();
                    errand.receiver.id = item.getString("offerId");
                }
                errand.money = item.getString("errandMoney");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //        {"msg":"server","signFlag":0,"senderId":"server",
//                "createTime":1593854476000,"msgId":"1593854476193-server-20171722-3429","action":5}
        public void responseMessage(Message message, JSONObject item) {
            try {
                message.id = item.getString("msgId");
                message.sender = new Account();
                message.sender.id = item.getString("senderId");
                message.date = new Date(item.getLong("createTime"));
                message.read = item.getInt("signFlag") == 1;
                message.content = item.getString("msg");
                message.type = item.getInt("action");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // /user/login
        public void login(
                Account account,
                final Listener listener
        ) { // LoginFragment.java
            call(
                    "/user/newLogin", Request.Method.POST,
                    "?studentNumber=" + account.id + "&password=" + account.password,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, LoginError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4001:
                                            listener.execute(ResultCode.Failed, LoginError.NotExist);
                                            break;
                                        case 4002:
                                            listener.execute(ResultCode.Failed, LoginError.WrongPassword);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, LoginError.LoginError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            );
        }


        // /user/myOffer, /user/myPublish, /errand/searchComposite
        public void queryErrandList(
                Account account, String keyword, int tag, int state,
                final Listener listener
        ) { // HomeFragment.java
            // TODO: 完成Remote.queryErrandList
            //  返回值object中存放List<Errand>
            if (account != null) {
                final List<Errand> errandList = new ArrayList<>();
                String queryUrl = ""; //根据选择的类型调用不同的url
                String param = "?studentNumber=" + account.id;
                call(queryUrl, Request.Method.GET,
                        param,
                        null,
                        new Listener() {
                            @Override
                            public void execute(ResultCode resultCode, Object object) {
                                if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                    listener.execute(ResultCode.Failed, SearchCompositeError.NetworkError);
                                } else {
                                    JSONObject jsonObject = (JSONObject) object;
                                    try {
                                        if (jsonObject.getInt("code") == 0) {
                                            JSONArray data = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject item = data.getJSONObject(i);
                                                final Errand errand = new Errand();
                                                responseErrand(errand, item);
                                                errandList.add(errand);
                                            }
                                            listener.execute(ResultCode.Succeeded, errandList);
                                        } else {
                                            listener.execute(ResultCode.Failed, SearchCompositeError.SearchFailed);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            } else {
                final List<Errand> errandList = new ArrayList<>();
                String param = "?errandItem=" + tag + "&errandStatus=" + state +
                        "&keyword=" + encode(keyword);
                call("/errand/searchComposite", Request.Method.GET,
                        param,
                        null,
                        new Listener() {
                            @Override
                            public void execute(ResultCode resultCode, Object object) {
                                if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                    listener.execute(ResultCode.Failed, SearchCompositeError.NetworkError);
                                } else {
                                    JSONObject jsonObject = (JSONObject) object;
                                    try {
                                        if (jsonObject.getInt("code") == 0) {
                                            JSONArray data = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject item = data.getJSONObject(i);
                                                final Errand errand = new Errand();
                                                responseErrand(errand, item);
                                                errandList.add(errand);
                                            }
                                            listener.execute(ResultCode.Succeeded, errandList);
                                        } else {
                                            listener.execute(ResultCode.Failed, SearchCompositeError.SearchFailed);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
            }
        }

        // /errand/item
        public void queryItem(
                final Errand errand,
                final Listener listener
        ) {
            call("/errand/item", Request.Method.GET,
                    "?errandItem=" + errand.tag,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, QueryItemError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code") == 0) {
                                        JSONObject item = jsonObject.getJSONObject("data");
                                        responseErrand(errand, item);
                                        listener.execute(ResultCode.Succeeded, errand);
                                    } else {
                                        listener.execute(ResultCode.Failed, QueryItemError.QueryItemError);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/search
        public void queryKeyword(
                final Errand errand,
                String keyword,
                final Listener listener
        ) {
            call("/errand/search", Request.Method.GET,
                    "?keyword=" + encode(keyword),
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, QueryKeywordError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code") == 0) {
                                        JSONObject item = jsonObject.getJSONObject("data");
                                        responseErrand(errand, item);
                                        listener.execute(ResultCode.Succeeded, errand);
                                    } else {
                                        listener.execute(ResultCode.Failed, QueryKeywordError.QueryKeywordError);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/detail
        public void queryDetail(
                final Errand errand,
                final Listener listener
        ) {
            call("/errand/detail", Request.Method.GET,
                    "?errandId=" + errand.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, DetailError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            JSONObject item = jsonObject.getJSONObject("data");
                                            responseErrand(errand, item);
                                            listener.execute(ResultCode.Succeeded, errand);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, DetailError.DetailError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/publish
        public void publish(
                Errand errand,
                final Listener listener
        ) { // PublishActivity.java
            // TODO: 完成Remote.publish
            call("/errand/publish", Request.Method.POST,
                    "?errandDescription=" + encode(errand.content) + "&errandItem=" + errand.tag.ordinal() +
                            "&errandMoney=" + errand.money + "&errandTitle=" + encode(errand.title) + "&publisherId=" + errand.publisher.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, PublishError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4006:
                                            listener.execute(ResultCode.Failed, PublishError.CreateFailed);
                                            break;
                                        case 4007:
                                            listener.execute(ResultCode.Failed, PublishError.UploadFileFalied);
                                            break;
                                        case 4011:
                                            listener.execute(ResultCode.Failed, PublishError.MoneyInsufficient);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, PublishError.PublishError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

        }

        // /errand/fire
        public void firePeople(
                Errand errand,
                final Listener listener
        ) {
            call("/errand/fire", Request.Method.GET,
                    "?errandId=" + errand.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, FireError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, FireError.FireError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/resign
        public void resignErrand(
                Errand errand,
                final Listener listener) {
            call("/errand/resign", Request.Method.GET,
                    "?errandId=" + errand.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, null);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, ResignError.NetworkError);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, ResignError.ResignError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/newComment
        public void comment(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TODO: 完成Remote.comment
        }

        // ?
        public void newJudge(
                Account account, Judge judge,
                final Listener listener
        ) { // ErrandActivity.java
            // TODO
        }

        // ?
        public void supressJudge(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TODO
        }

        // /judge/agree
        public void disagreeJudge(
                Account account, Judge judge,
                final Listener listener
        ) { // ErrandActivity.java
            // TODO
        }

        // /judge/agree
        public void agreeJudge(
                Account account, Judge judge,
                final Listener listener
        ) { // ErrandActivity.java
            // TODO
        }

        // /chat/queryNewestMsg
        public void queryConversationList(
                Account account,
                final Listener listener
        ) { // NoticeConversationActivity.java
            // TODO: 完成Remote.queryConversationList
            //  返回值object中存放List<Conversation>

            call("/chat/queryNewestMsg", Request.Method.GET,
                    "?studentNumber=" + account.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ConversationListError.NetworkError);
                            } else {
                                List<Conversation> conversationList = new ArrayList<>();
                                Map<String, Integer> map = new HashMap<>();
                                Map<String, Conversation> responseMap = new HashMap<>();
                                int flag = 0;
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    JSONArray data = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject item = data.getJSONObject(i);
                                        Message msg = new Message();
                                        responseMessage(msg, item);
                                        if (map.size() == 0) { //c初始化
                                            map.put(msg.sender.id, map.size());
                                            Conversation conversation = new Conversation();
                                            conversation.sender = new Account();
                                            conversation.sender.id = msg.sender.id;
                                            if (conversation.messageList == null)
                                                conversation.messageList = new ArrayList<>();
                                            conversation.messageList.add(msg);
                                            conversationList.add(conversation);
                                        } else {
                                            int idIndex = 0;
                                            if (map.containsKey(msg.sender.id)) {
                                                flag = 1;
                                                idIndex = map.get(msg.sender.id);
                                            } else
                                                flag = 0;
                                            if (flag == 1) { //若存在接收者
                                                conversationList.get(idIndex).messageList.add(msg);
                                            } else {  //不存在该接收者
                                                map.put(msg.sender.id, map.size());
                                                Conversation conversation = new Conversation();
                                                conversation.sender = new Account();
                                                conversation.sender.id = msg.sender.id;
                                                if (conversation.messageList == null)
                                                    conversation.messageList = new ArrayList<>();
                                                conversation.messageList.add(msg);
                                                conversationList.add(conversation);
                                            }

                                        }

                                    }
                                    for (int i = 0; i < conversationList.size(); i++) {
                                        Collections.sort(conversationList.get(i).messageList, new Comparator<Message>() {
                                            @Override
                                            public int compare(Message m1, Message m2) {
                                                return m1.date.compareTo(m2.date);
                                            }
                                        });
                                        Collections.reverse(conversationList.get(i).messageList); //倒序
                                        conversationList.get(i).messageLatest = conversationList.get(i).messageList.get(0);
                                        conversationList.get(i).time = conversationList.get(i).messageList.get(0).date;
                                    }
                                    for (int i = 0; i < conversationList.size(); i++) {
                                        responseMap.put(conversationList.get(i).sender.id, conversationList.get(i));
                                    }

                                    listener.execute(ResultCode.Succeeded, responseMap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

        }

        // /chat/queryHistoricRecords
        public void queryConversation(
                final Conversation conversation,
                final Listener listener
        ) { // ConversationActivity.java
            // TODO: 完成Remote.queryConversation
            //  返回值object中存放Conversation
            String param = "?myId=" + conversation.sender.id + "&otherId=" + conversation.receiverPrimary.id;
            call("/chat/queryHistoricRecords", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, QueryRecordsError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            JSONArray jsonMessage = new JSONArray();
                                            ((JSONObject) object).toJSONArray(jsonMessage);
                                            for (int i = 0; i < jsonMessage.length(); i++) {
                                                final Message msg = new Message();
                                                JSONObject Msg = jsonMessage.getJSONObject(i);
                                                msg.sender.id = Msg.getString("senderId");
                                                msg.receiver.id = Msg.getString("receiverId");
                                                msg.content = Msg.getString("msg");
                                                conversation.messageList.add(msg);
                                            }
                                            listener.execute(ResultCode.Succeeded, conversation);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, QueryRecordsError.QueryFailed);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // ws://129.211.5.147:8088/ws
        public void sendMessage(
                Message message,
                final Listener listener
        ) { // ConversationActivity.java
            // TODO: 完成Remote.sendMessage
        }

        // /chat/queryErrandHistoricRecords
        public void queryAnnouncementList(
                Account account,
                final Listener listener
        ) { // NoticeAnnouncementActivity.java
            // TODO: 完成Remote.queryAnnouncementList
            //  返回值object中存放List<Announcement>
        }

        // ?
        public void queryProgressList(
                Account account,
                final Listener listener
        ) { // NoticeProgressActivity.java
            // TODO: 完成Remote.queryProgressList
            //  返回值object中存放List<Progress>
        }

        // /errand/apply
        public void apply(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.apply
            String param = "?applierId=" + account.id + "&errandId=" + errand.id;
            call("/errand/apply", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ApplyError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4012:
                                            listener.execute(ResultCode.Failed, ApplyError.HaveApply);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, ApplyError.ApplyError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/choose
        public void acceptApplication(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.acceptApplication
            String param = "?errandId=" + errand.id + "&offerId=" + account.id;
            call("/errand/choose", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ChooseError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, ChooseError.ChooseError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/refuse
        public void rejectApplication(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.rejectApplication
            String param = "?errandId=" + errand.id + "&applierId=" + account.id;
            call("/errand/refuse", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, RefuseError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, RefuseError.RefuseError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/deleteErrand
        public void delete(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.delete
            String param = "?userId=" + account.id + "&errandId=" + errand.id;
            call("/errand/deleteErrand", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, DeleteError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4015:
                                            listener.execute(ResultCode.Failed, DeleteError.NotCreator);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, DeleteError.DeleteFailed);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }


        // /errand/push
        public void submit(
                Account account, final Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.submit
            String param = "?errandId=" + errand.id + "&errandStatus=" + errand.state;
            call("/errand/push", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, PushError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, PushError.PushError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/check 返回errand
        public void checkSubmission(
                Account account, final Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.acceptApplication
            String param = "?errandId=" + errand.id + "&errandStatus=" + errand.state;
            call("/errand/check", Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, CheckError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;

                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            JSONObject Msg = jsonObject.getJSONObject("data");
                                            String msg = Msg.getString("message");
                                            if (msg.equals("验收失败")) {
                                                listener.execute(ResultCode.Succeeded, CheckState.CheckRefused);
                                            } else if (msg.equals("待评价")) {
                                                listener.execute(ResultCode.Succeeded, CheckState.CheckSucceed);
                                            } else {
                                                listener.execute(ResultCode.Succeeded, CheckState.InputRightState);
                                            }
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, CheckError.CheckError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/check 集成在验收
        public void rejectSubmission(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.rejectApplication
        }

    }

    public static final class RemoteWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
            Log.d("Debug", "onOpen()");
            super.onOpen(webSocket, response);
            webSocket.send("{\"action\":1,\"chatMsg\":{\"senderId\":\"20176151\",\"receiverId\":\"20171745\",\"msg\":\"Hello, this is iphelf\",\"msgId\":null},\"extend\":null}");
            webSocket.send("{\"action\":1,\"chatMsg\":{\"senderId\":\"20176151\",\"receiverId\":\"20171722\",\"msg\":\"Hello, this is iphelf\",\"msgId\":null},\"extend\":null}");
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.d("Debug", "onClosed()");
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.d("Debug", "onClosing()");
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable
                t, @org.jetbrains.annotations.Nullable okhttp3.Response response) {
            Log.d("Debug", "onFailure()");
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            Log.d("Debug", "onMessage()");
            super.onMessage(webSocket, text);
            System.out.println("onMessage: " + text);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            Log.d("Debug", "onMessage()");
            super.onMessage(webSocket, bytes);
        }

    }
}
