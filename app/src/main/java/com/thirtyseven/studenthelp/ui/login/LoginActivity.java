package com.thirtyseven.studenthelp.ui.login;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.ui.MainActivity;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class LoginActivity extends AppCompatActivity {

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.title_login);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                remoteBinder = (Remote.RemoteBinder) iBinder;
                remoteBinder.startConversation();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(
                new Intent(this, Remote.class),
                serviceConnection,
                Service.BIND_AUTO_CREATE
        );

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
                remoteBinder.login(account, new Remote.Listener() {
                    @Override
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            Local.saveAccount(account);
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

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

}