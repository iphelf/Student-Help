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
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.data.Tag;

import org.json.JSONException;
import org.json.JSONObject;

public class Remote extends Service implements Global {
    public interface Listener {
        void execute(ResultCode resultCode, Object object);
    }

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

    public class RemoteBinder extends Binder {

        public void login(
                Account account,
                final Listener listener) {
            call(
                    "/user/login", Request.Method.POST,
                    "?username=" + account.username + "&password=" + account.password,
                    null,
                    new Listener() {
                        @Override
                        public void execute(ResultCode resultCode, Object object) {
                            if (resultCode == ResultCode.Failed || !(object instanceof JSONObject)) {
                                listener.execute(resultCode, null);
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

        public void register(
                Account account,
                final Listener listener) {
            // TODO: 完成Remote.register
        }

        public void certificate(
                Account account,
                final Listener listener) {
            // TODO: 完成Remote.certificate
        }

        public void queryErrandList(
                Account account, String keyword, Tag tag, Errand.State state, Errand.Type type,
                final Listener listener) {
            // TODO: 完成Remote.query
            //  返回值object中存放List<Errand>
        }

        public void publish(
                Errand errand,
                final Listener listener) {
            // TODO: 完成Remote.publish
        }

        public void queryConversationList(
                Account account,
                final Listener listener){
            // TODO: 完成Remote.queryConversationList
            //  返回值object中存放List<Message>
        }

        private void call(String route, int method, String param, JSONObject body, final Listener listener) {
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

    }

}
