package com.thirtyseven.studenthelp.ui.common;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class ErrandActivity extends AppCompatActivity implements Global {

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

    Errand errand;

    ImageView imageViewAvatar;
    TextView textViewPublisher;
    TextView textViewCredit;
    TextView textViewDate;
    TextView textViewTitle;
    TextView textViewState;
    TextView textViewTag;
    TextView textViewMoney;
    TextView textViewContent;

    Button buttonConversation;
    Button buttonDelete;
    Button buttonDismiss;
    Button buttonApply;
    Button buttonResign;
    Button buttonSubmit;

    ListView listViewQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand);
        setTitle(R.string.title_errand);

        errand = Local.popErrand();
        if (errand == null)
            finish();

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

        imageViewAvatar = findViewById(R.id.imageView_avatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ErrandActivity.this, ZoneActivity.class);
                startActivity(intent);
            }
        });

        textViewPublisher = findViewById(R.id.textView_author);
        textViewCredit = findViewById(R.id.textView_credit);
        textViewDate = findViewById(R.id.textView_date);
        textViewTitle = findViewById(R.id.textView_title);
        textViewState = findViewById(R.id.textView_state);
        textViewTag = findViewById(R.id.textView_tag);
        textViewMoney = findViewById(R.id.textView_money);
        textViewContent = findViewById(R.id.textView_content);

        buttonConversation = findViewById(R.id.button_conversation);
        buttonConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ErrandActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });

        buttonDelete = findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_delete, Toast.LENGTH_SHORT).show();
                remoteBinder.delete(Local.loadAccount(), errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            finish();
                        } else {
                            switch ((DeleteError) object) {
                                case NotCreator:
                                case NetworkError:
                                case DeleteFailed:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonDelete.setVisibility(View.GONE);

        buttonDismiss = findViewById(R.id.button_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_dismiss, Toast.LENGTH_SHORT).show();
//                remoteBinder.dismiss(Local.loadAccount(), errand, new Remote.Listener() {
//                    @Override
//                    public void execute(ResultCode resultCode, Object object) {
//                        if(resultCode==ResultCode.Succeeded){
//                            refresh();
//                        }
//                        else{
//                            switch ((DismissError)object){
//                                case NetworkError:
//                                case DismissError:
//                                default:
//                                    break;
//                            }
//                        }
//                    }
//                });
            }
        });
        buttonDismiss.setVisibility(View.GONE);

        buttonApply = findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_apply, Toast.LENGTH_SHORT).show();
                remoteBinder.apply(Local.loadAccount(), errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        }
                    }
                });
            }
        });
        buttonApply.setVisibility(View.GONE);

        buttonResign = findViewById(R.id.button_resign);
        buttonResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_resign, Toast.LENGTH_SHORT).show();
            }
        });
        buttonResign.setVisibility(View.GONE);

        buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_submit, Toast.LENGTH_SHORT).show();
            }
        });
        buttonSubmit.setVisibility(View.GONE);

        listViewQueue = findViewById(R.id.listView_queue);

        push();

    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    public void push() {
        textViewPublisher.setText(errand.publisher.getName());
        textViewCredit.setText(errand.publisher.getCredit());
        textViewDate.setText(errand.getDate());
        textViewTitle.setText(errand.getTitle());
        textViewState.setText(errand.getStateName());
        textViewTag.setText(errand.getTagName());
        textViewMoney.setText("悬赏: " + errand.getMoney());
        textViewContent.setText(errand.getContent());

        LinearLayout linearLayout = findViewById(R.id.linearLayout_queue);
        View viewApplication;
        for (int i = 0; i < 3; i++) {
            viewApplication = View.inflate(this, R.layout.listviewitem_application, linearLayout);
            ((TextView) viewApplication.findViewById(R.id.textView_applier)).setText("Applier #" + i);
        }
        for (int i = 0; i < 2; i++) {
            viewApplication = View.inflate(this, R.layout.listviewitem_submission, linearLayout);
        }

        if (Local.loadAccount().id.equals(errand.publisher.id)) {
            // As publisher
            switch (errand.state) {
                case Waiting:
                    buttonDelete.setVisibility(View.VISIBLE);
                    for (Account applier : errand.applierList) {
                        viewApplication = View.inflate(this, R.layout.listviewitem_application, linearLayout);
                    }
                    break;
                case Ongoing:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDismiss.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                case Judging:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                case CheckFailed:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                case Complete:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                case ToCheck:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                case NotEvaluate:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
                default:
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (errand.receiver != null && Local.loadAccount().id.equals(errand.receiver.id)) {
            // As receiver
            switch (errand.state) {
                case Waiting:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonApply.setVisibility(View.VISIBLE);
                    break;
                case Ongoing:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonSubmit.setVisibility(View.VISIBLE);
                    buttonResign.setVisibility(View.VISIBLE);
                    break;
                case Judging:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                case CheckFailed:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                case Complete:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                case ToCheck:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                case NotEvaluate:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                default:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            // As passerby
            switch (errand.state) {
                case Waiting:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonApply.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void refresh() {
        remoteBinder.queryDetail(errand, new Remote.Listener() {
            @Override
            public void execute(ResultCode resultCode, Object object) {
                errand = (Errand) object;
                push();
            }
        });
    }
}