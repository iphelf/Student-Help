package com.thirtyseven.studenthelp.ui.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Remote;

public class WalletActivity extends AppCompatActivity {
    private Button rechargeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle(R.string.title_wallet);
        rechargeBtn=findViewById(R.id.button_recharge);
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount="10";
                String studentNumber="20171745";
                Remote.remoteBinder.recharge(amount,studentNumber,new Remote.Listener(){
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if (resultCode == Global.ResultCode.Succeeded) {
                            String orderInfo = (String) object;

                        }else{

                        }
                    }
                });
            }
        });
    }

}