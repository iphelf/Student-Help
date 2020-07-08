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
import com.thirtyseven.studenthelp.data.Comment;
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
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Remote extends Service implements Global {
    private static RequestQueue requestQueue;
    private static OkHttpClient okHttpClient;
    private static String urlHost;
    private static String urlWs;
    static public RemoteBinder remoteBinder = new RemoteBinder();
    static public WebSocket webSocket;
    static public Map<String, Listener> listenerMap;

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
        urlHost = getString(R.string.url_host);
        urlWs = getString(R.string.url_ws);
        listenerMap = new HashMap<>();
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

        public void connect(Account account) {
            okhttp3.Request request = new okhttp3.Request.Builder().url(urlWs).build();
            RemoteWebSocketListener listener = new RemoteWebSocketListener();
            webSocket = okHttpClient.newWebSocket(request, listener);
            okHttpClient.dispatcher().executorService().shutdown();
        }

        public void send(Message message) {
            String text = message.pack();
            webSocket.send(text);
            Log.e("WebSocket", "Sent: " + text);
        }

        public void subscribe(String id, Listener listener) {
            listenerMap.put(id, listener);
        }

        public void unsubscribe(String id) {
            listenerMap.remove(id);
        }

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
                        if (applierId.trim().length() == 0) continue;
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

        public void recharge(
                String money, String studentNumber,
                final Listener listener
        ) {
            call(
                    "/alipay/recharge", Request.Method.POST,
                    "?studentNumber=" + studentNumber + "&amount=" + money,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ApilyError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code")==0) {
                                        String info;
                                        String outTradeNo;
                                        List<String> list=new ArrayList<>();
                                        JSONObject data=jsonObject.getJSONObject("data");
                                        info=data.getString("form:");
                                        outTradeNo=data.getString("outTradeNo:");
                                        list.add(info);
                                        list.add(outTradeNo);
                                        listener.execute(ResultCode.Succeeded, list);
                                    } else {
                                        listener.execute(ResultCode.Failed, ApilyError.ApilyError);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
            );
        }

        public void alipaySuccess(
                String money, String studentNumber, String ordNo,
                final Listener listener
        ) {
            call(
                    "/alipay/success", Request.Method.GET,
                    "?studentNumber=" + studentNumber + "&amount=" + money
                    +"&outTradeNo="+ordNo,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ApilyError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code") == 0) {
                                        listener.execute(ResultCode.Succeeded, null);
                                    } else {
                                        listener.execute(ResultCode.Failed, ApilyError.ApilyError);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
            );
        }

        public void withdraw(
                String money, String studentNumber, String account,
                final Listener listener
        ) {
            call(
                    "/alipay/withdraw", Request.Method.POST,
                    "?studentNumber=" + studentNumber + "&amount=" + money + "&account=" + account,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, ApilyError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    if (jsonObject.getInt("code") == 0) {
                                        listener.execute(ResultCode.Succeeded, null);
                                    }else if(jsonObject.getInt("code")==4011){
                                        listener.execute(ResultCode.Succeeded, ApilyError.MoneyNotInEnough);
                                    } else {
                                        listener.execute(ResultCode.Failed, ApilyError.ApilyError);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
            );
        }
        public void information(
                Account account,
                final Listener listener
        ){
            call(
                    "/user/findUser", Request.Method.GET,
                    "?studentNumber=" + account.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, LoginError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    Account account=new Account();
                                    JSONObject data=jsonObject.getJSONObject("data");
                                    if(jsonObject.getInt("code")==0){
                                        account.id=data.getString("userStudentNumber");
                                        account.realName=data.getString("userRealName");
                                        account.nickname=data.getString("userNickname");
                                        account.credit=data.getString("userCredit");
                                        account.capital=data.getString("userMoney");
                                        account.userImage=data.getString("userAvatar");
                                        listener.execute(ResultCode.Succeeded,account);
                                    }else{
                                        listener.execute(ResultCode.Failed, LoginError.LoginError);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
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

        public void queryHistoryList(
                Account account,
                final Listener listener
        ) {
            final List<Errand> errandList = new ArrayList<>();
            String queryUrl = "/user/myHistoryErrand"; //根据选择的类型调用不同的url
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
        }

        // /user/myOffer, /user/myPublish, /errand/searchComposite
        public void queryErrandList(
                Account account, String keyword, int tag, int state,
                final Listener listener
        ) { // HomeFragment.java
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
        public void newComment(
                Account account, Errand errand, Comment comment,
                final Listener listener
        ) { // ErrandActivity.java
            call("/errand/newComment", Request.Method.POST,
                    "?commentContent=" + encode(comment.content) + "&commentScore=" + comment.score
                            + "&errandId=" + errand.id + "&userId=" + account.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, NewCommentError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, NewCommentError.CommentError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        public void showComment(
                Account account, Errand errand, Comment comment,
                final Listener listener
        ) {
            call("/errand/comment", Request.Method.GET,
                    "?viewType=" + comment.type.ordinal()
                            + "&errandId=" + errand.id + "&userId=" + account.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, NewCommentError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            jsonObject = jsonObject.getJSONObject("data");
                                            Comment comment = new Comment();
                                            comment.id = jsonObject.getString("commentId");
                                            comment.commenter = new Account();
                                            comment.commenter.id = jsonObject.getString("userId");
                                            comment.errand = new Errand();
                                            comment.errand.id = jsonObject.getString("errandId");
                                            comment.score = (float) jsonObject.getDouble("commentScore");
                                            comment.content = jsonObject.getString("commentContent");
                                            listener.execute(ResultCode.Succeeded, comment);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, NewCommentError.CommentError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /judge/newJudge
        public void newJudge(
                Account account, Judge judge, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            call("/judge/newJudge", Request.Method.POST,
                    "?complainantId=" + account.id
                            + "&judgeErrandId=" + errand.id + "&judgeImage=" + encode(judge.image)
                            + "&judgeReason=" + encode(judge.reason) + "&judgeTitle=" + encode(judge.title) + "&respondentId=" + errand.receiver.id,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, NewJudgeError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, NewJudgeError.NewJudgeError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /errand/noJudge
        public void suppressJudge(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            call("/errand/noJudge", Request.Method.GET,
                    "?errandId=" + errand.id + "&errandStatus=" + Errand.State.NotEvaluate.ordinal(),
                    null, new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, SuppressError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, SuppressError.SuppressError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /judge/agree
        public void agreeJudge(
                Account account, Judge judge,
                final Listener listener
        ) { // ErrandActivity.java
            call("/judge/agree", Request.Method.POST,
                    "?isAgree=1&judgeId=" + judge.id + "&studentNumber=" + account.id,
                    null, new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, AgreeError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, AgreeError.AgreeError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /judge/agree
        public void disagreeJudge(
                Account account, Judge judge,
                final Listener listener
        ) { // ErrandActivity.java
            call("/judge/agree", Request.Method.POST,
                    "?isAgree=0&judgeId=" + judge.id + "&studentNumber=" + account.id,
                    null, new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, DisagreeError.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, DisagreeError.DisagreeError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /judge/detailByErrandId
        public void queryJudge(
                Errand errand,
                final Listener listener
        ) { // ErrantActivity.java
            call("/judge/detailByErrandId", Request.Method.GET,
                    "?ErrandId=" + errand.id,
                    null, new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, QueryJudge.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                            Judge judge = null;
                                            if (jsonArray.length() > 0) {
                                                jsonObject = jsonArray.getJSONObject(0);
                                                judge = new Judge();
                                                //       "judgeId": 26,
                                                judge.id = jsonObject.getString("judgeId");
                                                //      "judgeErrandId": 29,
                                                judge.errand = new Errand();
                                                judge.errand.id = jsonObject.getString("judgeErrandId");
                                                //      "complainantId": "20171745",
                                                judge.plaintiff = new Account();
                                                judge.plaintiff.id = jsonObject.getString("complainantId");
                                                //      "respondentId": "20176151",
                                                judge.defendant = new Account();
                                                judge.defendant.id = jsonObject.getString("respondentId");
                                                //      "judgeTitle": "Complaint",
                                                judge.title = jsonObject.getString("judgeTitle");
                                                //      "judgeReason": "",
                                                judge.reason = jsonObject.getString("judgeReason");
                                                //      "JudgeResult"
                                                if (jsonObject.has("judgeResult"))
                                                    judge.result = Judge.Result.values()[jsonObject.getInt("judgeResult")];
                                                else
                                                    judge.result = null;
                                                //      "judgeStatus": 0,
                                                judge.status = Judge.Status.values()[jsonObject.getInt("judgeStatus")];
                                                //      "createTime": "2020-07-08T14:53:32.000+00:00",
//                                            judge.createTime
                                                //      "updateTime": "2020-07-08T14:53:32.000+00:00"
//                                            judge.updateTime
                                            }
                                            listener.execute(ResultCode.Succeeded, judge);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, DisagreeError.DisagreeError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // /judge/handleStatus
        public void queryJudgeProgress(
                Judge judge, Account account,
                final Listener listener
        ) { // ErrantActivity.java
            call("/judge/handleStatus", Request.Method.GET,
                    "?judgeId=" + judge.id + "&userId=" + account.id,
                    null, new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(ResultCode.Failed, QueryJudge.NetworkError);
                            } else {
                                JSONObject jsonObject = (JSONObject) object;
                                try {
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            int type = jsonObject.getJSONObject("data").getInt("handleStatus");
                                            if (type > 2) type = 0;
                                            listener.execute(ResultCode.Succeeded,
                                                    Judge.Progress.values()[type]);
                                        default:
                                            listener.execute(ResultCode.Failed, DisagreeError.DisagreeError);
                                            break;
                                    }
                                } catch (JSONException e) {
                                }
                            }
                        }
                    });
        }

        // /chat/queryNewestMsg
        public void queryConversationList(
                Account account,
                final Listener listener
        ) { // NoticeConversationActivity.java

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
                                            conversation.receiver = new Account();
                                            conversation.receiver.id = msg.sender.id;
                                            if (conversation.messageList == null)
                                                conversation.messageList = new Vector<>();
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
                                                conversation.receiver = new Account();
                                                conversation.receiver.id = msg.sender.id;
                                                if (conversation.messageList == null)
                                                    conversation.messageList = new Vector<>();
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
                                        responseMap.put(conversationList.get(i).receiver.id, conversationList.get(i));
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
            String param = "?myId=" + conversation.sender.id + "&otherId=" + conversation.receiver.id;
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
                                            JSONArray jsonMessage = jsonObject.getJSONArray("data");
                                            conversation.messageList = new Vector<>();
                                            for (int i = 0; i < jsonMessage.length(); i++) {
                                                final Message msg = new Message();
                                                JSONObject Msg = jsonMessage.getJSONObject(i);
                                                msg.sender = new Account();
                                                msg.sender.id = Msg.getString("senderId");
                                                msg.receiver = new Account();
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
                final Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.submit
            String param = "?errandId=" + errand.id + "&errandStatus=" + Errand.State.ToCheck.ordinal();
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
                final Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.acceptApplication
            String param = "?errandId=" + errand.id + "&errandStatus=" + Errand.State.NotEvaluate.ordinal();
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
                Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.rejectApplication
            String param = "?errandId=" + errand.id + "&errandStatus=" + Errand.State.CheckFailed.ordinal();
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
    }

    public static final class RemoteWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        Message messageConnect;
        Message messageSignature;

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
            Log.d("Debug", "onOpen()");
            super.onOpen(webSocket, response);
            messageConnect = new Message();
            messageConnect.type = Message.Type.Connect;
            messageConnect.sender = Local.loadAccount();
            messageSignature = new Message();
            messageSignature.sender = Local.loadAccount();
            messageSignature.type = Message.Type.Sign;
            messageSignature.toSignList = new ArrayList<>();
            remoteBinder.send(messageConnect);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.d("Debug", "onClosed()");
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            Log.d("Debug", "onClosing(): " + reason);
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable
                t, @org.jetbrains.annotations.Nullable okhttp3.Response response) {
            Log.d("Debug", "onFailure(): ");
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            Log.e("WebSocket", "Received: " + text);
            super.onMessage(webSocket, text);
            if (text.charAt(0) == '{') {
                final Message message = Message.unpack(text);
                for (Listener listener : listenerMap.values()) {
                    listener.execute(ResultCode.Succeeded, message);
                }
                messageSignature.toSignList.clear();
                messageSignature.toSignList.add(message);
                remoteBinder.send(messageSignature);
            } else {
                for (Listener listener : listenerMap.values()) {
                    listener.execute(ResultCode.Succeeded, text);
                }
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            Log.d("Debug", "onMessage()");
            super.onMessage(webSocket, bytes);
        }
    }
}
