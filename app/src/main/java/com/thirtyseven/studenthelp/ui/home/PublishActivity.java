package com.thirtyseven.studenthelp.ui.home;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class PublishActivity extends AppCompatActivity implements Global {

    private Button buttonCancel;
    private Button buttonPublish;
    private EditText editTextTitle;
    private Spinner spinnerTag;
    private EditText editTextMoney;
    private EditText editTextContent;
    private ImageButton imageButtonAppend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_publish);
        setTitle(R.string.title_publish);

        buttonPublish = findViewById(R.id.button_publish);
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pull();
            }
        });

        buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editTextTitle = findViewById(R.id.editText_title);

        spinnerTag = findViewById(R.id.spinner_tag);
        ArrayAdapter<String> arrayAdapterTag = new ArrayAdapter<>(
                PublishActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                Errand.TagName
        );
        spinnerTag.setAdapter(arrayAdapterTag);

        editTextMoney = findViewById(R.id.editText_money);
        editTextContent = findViewById(R.id.editText_content);

        imageButtonAppend = findViewById(R.id.imageButton_append);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String title;
    private int tag;
    private String money;
    private String content;

    public void pull() {
        title = editTextTitle.getText().toString().trim();
        tag = spinnerTag.getSelectedItemPosition();
        money = editTextMoney.getText().toString().trim();
        content = editTextContent.getText().toString().trim();
        Errand errand = new Errand();
        errand.title = title;
        errand.tag = Errand.Tag.values()[tag];
        errand.money = money;
        errand.content = content;
        errand.publisher = Local.loadAccount();
        Remote.remoteBinder.publish(errand, new Remote.Listener() {
            @Override
            public void execute(ResultCode resultCode, Object object) {
                if (resultCode == ResultCode.Succeeded) {
                    push();
                } else {
                    switch ((PublishError) object) {
                        case MoneyInsufficient:
                            Toast.makeText(
                                    PublishActivity.this,
                                    R.string.toast_publishError_moneyInsufficient,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case UploadFileFalied:
                            Toast.makeText(
                                    PublishActivity.this,
                                    R.string.toast_publishError_uploadError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case CreateFailed:
                            Toast.makeText(
                                    PublishActivity.this,
                                    R.string.toast_publishError_createError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case NetworkError:
                            Toast.makeText(
                                    PublishActivity.this,
                                    R.string.toast_networkError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case PublishError:
                        default:
                            Toast.makeText(
                                    PublishActivity.this,
                                    R.string.toast_publishError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                }
            }
        });
    }

    public void push() {
        finish();
    }

}