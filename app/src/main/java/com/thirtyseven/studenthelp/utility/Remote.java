package com.thirtyseven.studenthelp.utility;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
import com.thirtyseven.studenthelp.data.Tag;

import org.json.JSONException;
import org.json.JSONObject;

public class Remote extends Service implements Global {
    private RequestQueue requestQueue;
    private String urlHost;
    private RemoteBinder remoteBinder = new RemoteBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return remoteBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
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

        // /user/login
        public void login(
                Account account,
                final Listener listener
        ) { // LoginFragment.java
            call(
                    "/user/login", Request.Method.POST,
                    "?username=" + account.username + "&password=" + account.password,
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
                                        case 4005:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4004:
                                            listener.execute(ResultCode.Failed, LoginError.LoginError);
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

        // /user/register
        public void register(
                Account account,
                final Listener listener
        ) { // RegisterFragment.java
            // TODO: 完成Remote.register
            call("/user/register", Request.Method.POST,
                    "?username=" + account.username + "&password=" + account.password,
                    null,
                    new Listener() {
                        public void execute(ResultCode resultCode, Object object) {
                            if(resultCode == ResultCode.Failed || !(object instanceof JSONObject)){
                                listener.execute(ResultCode.Failed, null);
                            }else{
                                JSONObject jsonObject = (JSONObject) object;
                                try{
                                    switch (jsonObject.getInt("code")) {
                                        case 0:
                                            listener.execute(ResultCode.Succeeded, null);
                                            break;
                                        case 4003:
                                            listener.execute(ResultCode.Failed, RegisterError.UserExist);
                                            break;
                                        default:
                                            listener.execute(ResultCode.Failed, RegisterError.NetworkError);
                                            break;
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }


            }
            );

        }

        // /user/check
        public void certificate(
                Account account,
                final Listener listener
        ) { // CertificateFragment.java
            // TODO: 完成Remote.certificate
        }

        // /user/myOffer, /user/myPublish
        public void queryErrandList(
                Account account, String keyword, Tag tag, Errand.State state, Errand.Type type,
                final Listener listener
        ) { // HomeFragment.java
            // TODO: 完成Remote.query
            //  返回值object中存放List<Errand>
        }

        // /errand/publish
        public void publish(
                Errand errand,
                final Listener listener
        ) { // PublishActivity.java
            // TODO: 完成Remote.publish
        }

        // ?
        public void queryConversationList(
                Account account,
                final Listener listener
        ) { // NoticeConversationActivity.java
            // TODO: 完成Remote.queryConversationList
            //  返回值object中存放List<Conversation>
        }

        // ?
        public void queryConversation(
                Conversation conversation,
                final Listener listener
        ) { // ConversationActivity.java
            // TODO: 完成Remote.queryConversation
            //  返回值object中存放Conversation
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
        }

        // ?
        public void delete(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.delete
        }

        // ?
        public void resign(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.resign
        }

        // ?
        public void submit(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.submit
        }

        // ?
        public void dismiss(
                Account account, Errand errand,
                final Listener listener
        ) { // ErrandActivity.java
            // TO-DO: 完成Remote.dismiss
        }

    }

}
