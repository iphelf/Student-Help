package com.thirtyseven.studenthelp.ui.login;

import android.app.ActionBar;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.ui.MainActivity;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_login);

        final EditText editTextStudentId = findViewById(R.id.editText_studentId);
        final EditText editTextPassword = findViewById(R.id.editText_password);

        final Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextStudentId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                final Account account = new Account();
                account.id = id;
                account.password = password;
                Remote.remoteBinder.login(account, new Remote.Listener() {
                    @Override
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            Local.saveAccount(account);
                            Remote.remoteBinder.connect(account);
                            Remote.remoteBinder.queryConversationList(account, new Remote.Listener() {
                                @Override
                                public void execute(Global.ResultCode resultCode, Object object) {
                                    if (resultCode == Global.ResultCode.Succeeded) {
                                        List<Conversation> list=new ArrayList<>();
                                        Map<String, Conversation> map=new HashMap<>();
                                        map=(Map<String, Conversation>)object;
                                        Local.saveConversationMap(map);
                                        for(String str:map.keySet()){
                                            list.add(map.get(str));
                                        }
                                        Collections.sort(list, new Comparator<Conversation>() {
                                            @Override
                                            public int compare(Conversation m1, Conversation m2) {
                                                return m1.messageLatest.date.compareTo(m2.messageLatest.date);
                                            }
                                        });
                                        Collections.reverse(list); //倒序 按时间从近到远
                                        Local.saveConversationList(list);
                                    }else{
                                        switch ((Global.ConversationListError)object){
                                            case NetworkError:
                                                Toast.makeText(
                                                        LoginActivity.this,
                                                        R.string.toast_networkError,
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                                break;
                                            case ConversationListError:
                                            default:
                                                Toast.makeText(
                                                        LoginActivity.this,
                                                        R.string.toast_conversationListError,
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                                break;
                                        }

                                    }
                                }
                            });
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            switch ((Global.LoginError) object) {
                                case NotExist:
                                    Toast.makeText(
                                            LoginActivity.this,
                                            R.string.toast_loginError_notExist,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case WrongPassword:
                                    Toast.makeText(
                                            LoginActivity.this,
                                            R.string.toast_loginError_wrongPassword,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case NetworkError:
                                    Toast.makeText(
                                            LoginActivity.this,
                                            R.string.toast_networkError,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case LoginError:
                                default:
                                    Toast.makeText(
                                            LoginActivity.this,
                                            R.string.toast_loginError,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });

    }

}