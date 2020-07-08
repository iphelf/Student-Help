package com.thirtyseven.studenthelp.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.data.Judge;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;
import com.thirtyseven.studenthelp.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrandActivity extends AppCompatActivity implements Global {

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
    Button buttonComment;
    List<Button> buttonList;

    ListView listViewPulse;
    ListView listViewComment;

    ConstraintLayout constraintLayoutAction;
    ConstraintLayout constraintLayoutPulse;
    ConstraintLayout constraintLayoutComment;

    enum Pulse {Application, Submission, Judge, Result}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand);
        setTitle(R.string.title_errand);

        errand = Local.popErrand();
        if (errand == null)
            finish();

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

        buttonList = new ArrayList<>();

        buttonConversation = findViewById(R.id.button_conversation);
        buttonConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ErrandActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });
        buttonList.add(buttonConversation);

        buttonDelete = findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_delete, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.delete(Local.loadAccount(), errand, new Remote.Listener() {
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
        buttonList.add(buttonDelete);

        buttonDismiss = findViewById(R.id.button_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_dismiss, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.firePeople(errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        } else {
                            switch ((DismissError) object) {
                                case NetworkError:
                                case DismissError:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonList.add(buttonDismiss);

        buttonApply = findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_apply, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.apply(Local.loadAccount(), errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        } else {
                            switch ((ApplyError) object) {
                                case HaveApply:
                                case NetworkError:
                                case ApplyError:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonList.add(buttonApply);

        buttonResign = findViewById(R.id.button_resign);
        buttonResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_resign, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.resignErrand(errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        } else {
                            switch ((ResignError) object) {
                                case NetworkError:
                                case ResignError:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonList.add(buttonResign);

        buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_submit, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.submit(errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        } else {
                            switch ((PushError) object) {
                                case NetworkError:
                                case PushError:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonList.add(buttonSubmit);

        buttonComment = findViewById(R.id.button_comment);
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ErrandActivity.this, R.string.button_comment, Toast.LENGTH_SHORT).show();
                Remote.remoteBinder.comment(Local.loadAccount(), errand, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            refresh();
                        } else {
                            switch ((CommentError) object) {
                                case NetworkError:
                                case CommentError:
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
        buttonList.add(buttonComment);

        listViewPulse = findViewById(R.id.listView_pulse);

        listViewComment = findViewById(R.id.listView_commentList);

        constraintLayoutAction = findViewById(R.id.constraintLayout_action);
        constraintLayoutPulse = findViewById(R.id.constraintLayout_pulse);
        constraintLayoutComment = findViewById(R.id.constraintLayout_comment);

        push();

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

        String[] pulseField = {"Pulser", "Content", "Yes", "No"};
        int[] pulseFiledId = {
                R.id.textView_pulser,
                R.id.textView_content,
                R.id.button_yes,
                R.id.button_no
        };
        final List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map;

        // Show buttons
        for (Button button : buttonList)
            button.setVisibility(View.GONE);
        constraintLayoutComment.setVisibility(View.GONE);
        final List<Pair<Pulse, String>> pulseList = new ArrayList<>();
        // Action buttons include:
        //   buttonConversation (shows in all states except publisher's Waiting)
        //   buttonDelete (shows in all states in publisher's view)
        //   buttonDismiss (shows in publisher's Ongoing state)
        //   buttonApply (shows(changes) in receivers and passerby's Waiting state
        //   buttonResign (shows in receiver's Ongoing state)
        //   buttonSubmit (shows in receiver's Ongoing state)
        if (Local.loadAccount().id.equals(errand.publisher.id)) { // As publisher
            switch (errand.state) {
                case Waiting:
                    buttonDelete.setVisibility(View.VISIBLE);
                    if (errand.applierList != null && !errand.applierList.isEmpty()) {
                        for (Account applier : errand.applierList) {
                            pulseList.add(Pair.create(Pulse.Application, applier.id));
                        }
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
                    if (errand.judge.result != null) {
                        pulseList.add(Pair.create(Pulse.Result, (errand.judge.result == Judge.Result.FaultOnPublisher ? "Publisher" : "Receiver")));
                    }
                    break;
                case CheckFailed:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    pulseList.add(Pair.create(Pulse.Judge, ""));
                    break;
                case Complete:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    buttonComment.setVisibility(View.GONE); // ?
                    break;
                case ToCheck:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    pulseList.add(Pair.create(Pulse.Submission, errand.receiver.getName()));
                    break;
                case NotEvaluate:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonDelete.setVisibility(View.VISIBLE);
                    constraintLayoutComment.setVisibility(View.VISIBLE);
                    break;
                default:
                    buttonDelete.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (errand.receiver != null && Local.loadAccount().id.equals(errand.receiver.id)) { // As receiver
            switch (errand.state) {
                case Ongoing:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonSubmit.setEnabled(true);
                    buttonSubmit.setVisibility(View.VISIBLE);
                    buttonResign.setVisibility(View.VISIBLE);
                    break;
                case Judging:
                    buttonConversation.setVisibility(View.VISIBLE);
                    if (errand.judge.result != null) {
                        pulseList.add(Pair.create(Pulse.Result, (errand.judge.result == Judge.Result.FaultOnPublisher ? "Publisher" : "Receiver")));
                    }
                    break;
                case CheckFailed:
                    buttonConversation.setVisibility(View.VISIBLE);
                    pulseList.add(Pair.create(Pulse.Judge, ""));
                    break;
                case Complete:
                    buttonConversation.setVisibility(View.VISIBLE);
                    break;
                case ToCheck:
                    buttonConversation.setVisibility(View.VISIBLE);
                    buttonSubmit.setVisibility(View.VISIBLE);
                    buttonSubmit.setEnabled(false);
                    break;
                case NotEvaluate:
                    buttonConversation.setVisibility(View.VISIBLE);
                    constraintLayoutComment.setVisibility(View.VISIBLE);
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
                    if (errand.applierList != null && errand.applierList.contains(Local.loadAccount()))
                        buttonApply.setEnabled(false);
                    break;
            }
        }
        boolean empty = true;
        for (Button button : buttonList)
            if (button.getVisibility() == View.VISIBLE) {
                empty = false;
                break;
            }
        if (empty) {
            constraintLayoutAction.setVisibility(View.GONE);
        }

        // Show pulse
        if (pulseList.isEmpty()) {
            constraintLayoutPulse.setVisibility(View.GONE);
        } else {
            constraintLayoutPulse.setVisibility(View.VISIBLE);
            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    this,
                    mapList,
                    R.layout.listviewitem_pulse,
                    pulseField,
                    pulseFiledId
            );
            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return pulseList.size();
                }

                @Override
                public Object getItem(int i) {
                    return pulseList.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    View viewPulse = View.inflate(ErrandActivity.this, R.layout.listviewitem_pulse, null);
                    TextView textViewPulser = viewPulse.findViewById(R.id.textView_pulser);
                    TextView textViewContent = viewPulse.findViewById(R.id.textView_content);
                    Button buttonYes = viewPulse.findViewById(R.id.button_yes);
                    Button buttonNo = viewPulse.findViewById(R.id.button_no);
                    Pulse type = pulseList.get(i).first;
                    final String string = pulseList.get(i).second;
                    switch (type) {
                        case Application:
                            textViewPulser.setText(string);
                            textViewContent.setText(R.string.string_applicationContent);
                            buttonYes.setText(R.string.button_accept);
                            final Account applier = new Account();
                            applier.id = string;
                            buttonYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.acceptApplication(applier, errand, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            buttonNo.setText(R.string.button_reject);
                            buttonNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.rejectApplication(applier, errand, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            break;
                        case Submission:
                            textViewPulser.setText(string);
                            textViewContent.setText(R.string.string_submissionContent);
                            buttonYes.setText(R.string.button_confirm);
                            buttonYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.checkSubmission(errand, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            buttonNo.setText(R.string.button_refuse);
                            buttonNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.rejectSubmission(errand, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            break;
                        case Judge:
                            textViewPulser.setText(R.string.string_judge);
                            textViewContent.setText(R.string.string_judgeContent);
                            buttonYes.setText(R.string.button_yes);
                            buttonYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ErrandActivity.this, ComplainActivity.class);
                                    Local.pushErrand(errand);
                                    startActivity(intent);
                                }
                            });
                            buttonNo.setText(R.string.button_no);
                            buttonNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.supressJudge(Local.loadAccount(), errand, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            break;
                        case Result:
                            textViewPulser.setText(R.string.string_judge);
                            if (string.equals("Publisher"))
                                textViewContent.setText(R.string.string_resultKeepContent);
                            else
                                textViewContent.setText(R.string.string_resultCancelContent);
                            buttonYes.setText(R.string.button_agree);
                            buttonYes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.agreeJudge(Local.loadAccount(), errand.judge, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            buttonNo.setText(R.string.button_disagree);
                            buttonNo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Remote.remoteBinder.disagreeJudge(Local.loadAccount(), errand.judge, new Remote.Listener() {
                                        @Override
                                        public void execute(ResultCode resultCode, Object object) {
                                            refresh();
                                        }
                                    });
                                }
                            });
                            break;
                    }
                    return viewPulse;
                }
            };
            listViewPulse.setAdapter(baseAdapter);
            Utility.setListViewHeightBasedOnChildren(listViewPulse);
        }

        // Show comments
        if (errand.commentList != null && !errand.commentList.isEmpty()) {
            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return errand.commentList.size();
                }

                @Override
                public Object getItem(int i) {
                    return errand.commentList.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    view = View.inflate(ErrandActivity.this, R.layout.listviewitem_comment, null);
                    TextView textViewCommenter = view.findViewById(R.id.textView_commenter);
                    textViewCommenter.setText(errand.commentList.get(i).commenter.getName());
                    TextView textViewContent = view.findViewById(R.id.textView_content);
                    textViewContent.setText(errand.commentList.get(i).content);
                    RatingBar ratingBar = view.findViewById(R.id.ratingBar_score);
                    ratingBar.setRating(errand.commentList.get(i).score);
                    return view;
                }
            };
            listViewComment.setAdapter(baseAdapter);
            Utility.setListViewHeightBasedOnChildren(listViewComment);
        }
    }

    public void refresh() {
        Remote.remoteBinder.queryDetail(errand, new Remote.Listener() {
            @Override
            public void execute(ResultCode resultCode, Object object) {
                errand = (Errand) object;
                push();
            }
        });
    }
}