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
import com.thirtyseven.studenthelp.data.Message;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Remote extends Service implements Global {
    private RequestQueue requestQueue;
    private OkHttpClient okHttpClient;
    private String urlHost;
    private RemoteBinder remoteBinder = new RemoteBinder();
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

    public class RemoteBinder extends Binder {

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
                            listener.execute(ResultCode.Succeeded, response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.execute(ResultCode.Failed, null);
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
        }

        public void startConversation() {
            okhttp3.Request request = new okhttp3.Request.Builder().url("ws://129.211.5.147:8088/ws").build();
            RemoteWebSocketListener listener = new RemoteWebSocketListener();
            WebSocket webSocket = okHttpClient.newWebSocket(request, listener);
            webSocket.send("{\"action\":1,\"chatMsg\":{\"senderId\":\"20176151\",\"receiverId\":\"20171722\",\"msg\":\"Hello, this is iphelf\",\"msgId\":null},\"extend\":null}");
            okHttpClient.dispatcher().executorService().shutdown();
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


        private void queryItem(String queryUrl, String param, final List<Errand> queryList, final Listener queryListener) {
            call(queryUrl, Request.Method.GET,
                    param,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                queryListener.execute(ResultCode.Failed, SearchComposite.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code") == 0) {
                                        try {
                                            JSONArray data = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < data.length(); i++) {
                                                JSONObject item = data.getJSONObject(i);
                                                final Errand errand = new Errand();
                                                errand.title = item.getString("errandTitle");
                                                errand.tag = item.getInt("errandItem");
                                                errand.state = Errand.State.values()[item.getInt("errandStatus")];
                                                errand.content = item.getString("errandDescription");
                                                errand.publisher.id = item.getString("publisherId");
                                                errand.receiver.id = item.getString("offerId");
                                                errand.money = new BigDecimal(item.getString("errandMoney"));
                                                queryList.add(errand);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        success = true;
                                    } else {
                                        success = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /user/myOffer, /user/myPublish, /errand/searchComposite
        public void queryErrandList(
                Account account, String keyword, int tag, Errand.State state,
                final Listener listener
        ) { // HomeFragment.java
            // TODO: 完成Remote.queryErrandList
            //  返回值object中存放List<Errand>
            if (account != null) {
                List<Errand> errandList = new ArrayList<>();
                queryItem("/user/myOffer", "?studentNumber=" + account.id, errandList, listener);
                queryItem("/user/myPublish", "?studentNumber=" + account.id, errandList, listener);
                if (success) {
                    listener.execute(ResultCode.Succeeded, errandList);
                } else {
                    if (errandList.size() != 0) {
                        listener.execute(ResultCode.Succeeded, errandList); //如果success被标记为失败，可能是有一次查询失败，若是列表不为空，则说明查询到了数据
                    } else {
                        listener.execute(ResultCode.Failed, SearchComposite.SearchFailed);
                    }
                }
            } else {
                List<Errand> errandList = new ArrayList<>();
                String param = "?errandItem=" + tag + "&errandStatus=" + state +
                        "&keyword=" + keyword;
                queryItem("/errand/searchComposite", param, errandList, listener);
                if (success) {
                    listener.execute(ResultCode.Succeeded, errandList);
                } else {
                    listener.execute(ResultCode.Failed, SearchComposite.SearchFailed);
                }
                //以下功能被集中到querItem中
//                call("/errand/searchComposite", Request.Method.GET,
//                        param,
//                        null,
//                        new Listener() {
//                            @Override
//                            public void execute(ResultCode resultCode, Object object) {
//                                if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
//                                    listener.execute(ResultCode.Failed, null);
//                                } else {
//                                    JSONObject jsonObject = (JSONObject) object;
//                                    try {
//                                        if (jsonObject.getInt("code")==0) {
//                                                try {
//                                                    JSONArray data = jsonObject.getJSONArray("data");
//                                                    for (int i = 0; i < data.length(); i++) {
//                                                        JSONObject item = data.getJSONObject(i);
//                                                        final Errand errand=new Errand();
//                                                        errand.title=item.getString("errandTitle");
//                                                        errand.tag=item.getInt("errandItem");
//                                                        switch (item.getInt("errandStatus")){
//                                                            case 0:
//                                                                errand.state=errand.state.Waiting;
//                                                                break;
//                                                            case 1:
//                                                                errand.state=errand.state.Ongoing;
//                                                                break;
//                                                            case 2:
//                                                                errand.state=errand.state.NotEvaluate;
//                                                                break;
//                                                            case 3:
//                                                                errand.state=errand.state.Judging;
//                                                                break;
//                                                            case 4:
//                                                                errand.state=errand.state.CheckFailed;
//                                                                break;
//                                                            case 5:
//                                                                errand.state=errand.state.Complete;
//                                                                break;
//                                                            default:
//                                                                break;
//                                                        }
//                                                        errand.content=item.getString("errandDescription");
//                                                        errand.publisher.id=item.getString("publisherId");
//                                                        errand.receiver.id=item.getString("offerId");
//                                                        errand.money=new BigDecimal(item.getString("errandMoney"));
//                                                        errandList.add(errand);
//                                                    }
//                                                }catch (JSONException e){
//                                                    e.printStackTrace();
//                                                }
//                                                listener.execute(ResultCode.Succeeded, errandList);
//                                        }else{
//                                                listener.execute(ResultCode.Failed, SearchComposite.SearchFailed);
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
            }
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
                                            JSONObject item=jsonObject.getJSONObject("data");
                                            errand.title = item.getString("errandTitle");
                                            errand.tag = item.getInt("errandItem");
                                            errand.state = Errand.State.values()[item.getInt("errandStatus")];
                                            errand.content = item.getString("errandDescription");
                                            errand.publisher.id = item.getString("publisherId");
                                            errand.receiver.id = item.getString("offerId");
                                            errand.money = new BigDecimal(item.getString("errandMoney"));
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
                    "?errandDescription=" + errand.content + "&errandItem =" + errand.tag +
                            "&errandMoney=" + errand.money + "&errandTitle=" + errand.title + "&publisherId=" + errand.publisher.id,
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

        //?
        public void queryConversationList(
                Account account,
                final Listener listener
        ) { // NoticeConversationActivity.java
            // TODO: 完成Remote.queryConversationList
            //  返回值object中存放List<Conversation>

            // 这里暂时存放模板
            call("/user/register", Request.Method.POST,
                    "?studentNumber=" + account.id + "&password=" + account.password,
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
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, RegisterError.NetworkError);
                                            break;
                                    }
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
            String param = "?myId=" + conversation.publisher.id + "&otherId=" + conversation.receiverPrimary.id;
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

        // ?
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

        // ?
        public void resign(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.resign
        }

        // ?
        public void dismiss(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.dismiss
        }

        // /errand/push
        public void submit(
                Account account, final Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.submit
            String param = "?errandId=" + errand.id + "&errandStatus=" + errand.state.ordinal();
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
            String param = "?errandId=" + errand.id + "&errandStatus=" + errand.state.ordinal();
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

    public final class RemoteWebSocketListener extends WebSocketListener {
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
