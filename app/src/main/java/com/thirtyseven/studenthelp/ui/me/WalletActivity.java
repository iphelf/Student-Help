package com.thirtyseven.studenthelp.ui.me;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.thirtyseven.studenthelp.ui.login.LoginActivity;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.Map;

public class WalletActivity extends AppCompatActivity {
    private Button rechargeBtn;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private String amount;
    private String studentNumber;
    private String apilyAccount;
    private EditText editRecharge;
    private EditText editWithDraw;
    private TextView myCoin;
    private int myCoinNumber;
    private Account account;
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
//               myCoinNumber=Integer.parseInt(amount);
//               myCoin.setText(Integer.toString(myCoinNumber));
//               Remote.remoteBinder.alipaySuccess(amount,studentNumber,new Remote.Listener(){
//                   public void execute(Global.ResultCode resultCode, Object object) {
//                   } 
//               });
           }
           else{
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
        setTitle(R.string.title_wallet);
        myCoinNumber=0;
        Account account= Local.loadAccount();
        studentNumber=account.id;
        editRecharge=(EditText)findViewById(R.id.editText_recharge);
        editWithDraw=(EditText)findViewById(R.id.editText_withdraw);
        myCoin=findViewById(R.id.textView_myCoin);
        amount=editRecharge.getText().toString();
        apilyAccount="nxiwfa8023@sandbox.com";
        rechargeBtn=findViewById(R.id.button_recharge);
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=editRecharge.getText().toString();
                Remote.remoteBinder.recharge(amount,studentNumber,new Remote.Listener(){
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            final String orderInfo =(String)object;
                            Runnable payRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(WalletActivity.this);
                                    Map<String,String> result = alipay.payV2(orderInfo,true);
                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }else{

                        }
                    }
                });
            }
        });
    }

}