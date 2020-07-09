package com.thirtyseven.studenthelp.ui.me;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.ui.common.CustomTitleBar;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.Map;

public class WalletActivity extends AppCompatActivity {
    private Button rechargeBtn;
    private Button withdrawBtn;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private String amount;
    private String studentNumber;
    private String apilyAccount;
    private EditText editRecharge;
    private EditText editWithdraw;
    private TextView myCoin;
    private CustomTitleBar customTitleBar;
    private int myCoinNumber = 0;
    private Account account;
    private String ordNo;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SDK_PAY_FLAG) {
                Toast.makeText(
                        WalletActivity.this,
                        R.string.toast_recharge_success,
                        Toast.LENGTH_SHORT
                ).show();
                Remote.remoteBinder.alipaySuccess(amount, studentNumber, ordNo, new Remote.Listener() {
                    public void execute(Global.ResultCode resultCode, Object object) {
                        myCoinNumber = Integer.parseInt(editRecharge.getText().toString());
                        myCoinNumber = myCoinNumber + Integer.parseInt(account.capital);
                        account.capital = Integer.toString(myCoinNumber);
                        myCoin.setText(account.capital);
                    }
                });
            } else {
                Toast.makeText(
                        WalletActivity.this,
                        R.string.toast_recharge_failed,
                        Toast.LENGTH_SHORT
                ).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        customTitleBar = findViewById(R.id.customTitleBar);
        customTitleBar.setLeftIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        customTitleBar.setTitle(R.string.title_wallet);
        account = Local.loadAccount();
        studentNumber = account.id;
        editRecharge = (EditText) findViewById(R.id.editText_recharge);
        editWithdraw = (EditText) findViewById(R.id.editText_withdraw);
        myCoin = findViewById(R.id.textView_coinNum);
        amount = editRecharge.getText().toString();
        myCoin.setText(account.capital);
        apilyAccount = "nxiwfa8023@sandbox.com";
        rechargeBtn = findViewById(R.id.button_recharge);
        withdrawBtn = findViewById(R.id.button_withdrawal);
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = editRecharge.getText().toString();
                Remote.remoteBinder.recharge(amount, studentNumber, new Remote.Listener() {
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            final String orderInfo = (String) object;
                            Runnable payRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(WalletActivity.this);
                                    Map<String, String> result = alipay.payV2(orderInfo, true);
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        } else {
                            Toast.makeText(
                                    WalletActivity.this,
                                    R.string.toast_networkError,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
            }
        });
        withdrawBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                amount = editWithdraw.getText().toString();
                Remote.remoteBinder.withdraw(amount, studentNumber, apilyAccount, new Remote.Listener() {
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            if (resultCode == Global.ResultCode.Succeeded) {

                                if (object == null) {
                                    Toast.makeText(
                                            WalletActivity.this,
                                            R.string.toast_withdraw_success,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            WalletActivity.this,
                                            R.string.toast_withdraw_money,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            } else {
                                Toast.makeText(
                                        WalletActivity.this,
                                        R.string.toast_withdraw_failed,
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    WalletActivity.this,
                                    R.string.toast_withdraw_failed,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
            }
        });
    }

}