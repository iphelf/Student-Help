package com.thirtyseven.studenthelp.ui.common;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.data.Judge;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class ComplainActivity extends AppCompatActivity implements Global {

    Errand errand;

    Button buttonComplain;
    Button buttonCancel;
    EditText editTextTitle;
    EditText editTextContent;
    ImageButton imageButtonAppend;
    CustomTitleBar customTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        customTitleBar = findViewById(R.id.customTitleBar);
        customTitleBar.setTitle(getString(R.string.title_complain));
        buttonComplain.setOnClickListener(new View.OnClickListener() {
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
        editTextContent = findViewById(R.id.editText_content);

        errand = Local.popErrand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String title;
    private String content;
    private Judge judge;

    public void pull() {
        title = editTextTitle.getText().toString().trim();
        content = editTextContent.getText().toString().trim();
        judge = new Judge();
        judge.title = title;
        judge.reason = content;
        judge.image = "";
        Remote.remoteBinder.newJudge(Local.loadAccount(), judge, errand, new Remote.Listener() {
            @Override
            public void execute(ResultCode resultCode, Object object) {
                if (resultCode == ResultCode.Succeeded) {
                    push();
                } else {
                    switch ((NewJudgeError) object) {
                        case NetworkError:
                            Toast.makeText(
                                    ComplainActivity.this,
                                    R.string.toast_networkError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case NewJudgeError:
                        default:
                            Toast.makeText(
                                    ComplainActivity.this,
                                    R.string.toast_complainError,
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