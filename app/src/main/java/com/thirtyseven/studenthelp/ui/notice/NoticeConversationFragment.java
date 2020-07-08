package com.thirtyseven.studenthelp.ui.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.ui.common.ConversationActivity;
import com.thirtyseven.studenthelp.ui.common.adapter.ConversationListAdapter;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoticeConversationFragment extends Fragment {

    private ListView listViewConversationList;
    private List<Conversation> conversationList;

    public NoticeConversationFragment() {
        // Required empty public constructor
    }

    public static NoticeConversationFragment newInstance(String param1, String param2) {
        return new NoticeConversationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notice_conversation, container, false);
        listViewConversationList = root.findViewById(R.id.listView_conversationList);
        refresh();
        return root;
    }

    public void refresh() {
        Remote.remoteBinder.queryConversationList(Local.loadAccount(), new Remote.Listener() {
                    @Override
                    public void execute(Global.ResultCode resultCode, Object object) {
                        Map<String, Conversation> conversationMap = (Map<String, Conversation>) object;
                        Local.saveConversationMap(conversationMap);
                        conversationList = new ArrayList<>();
                        for (Conversation conversation : conversationMap.values()) {
                            if (conversation.receiver.id.equals("server")) continue;
                            conversationList.add(conversation);
                        }
                        Local.saveConversationList(conversationList);
                        push();
                    }
                }
        );
    }

    public void push() {
        ConversationListAdapter conversationListAdapter = new ConversationListAdapter(getContext(), conversationList);
        listViewConversationList.setAdapter(conversationListAdapter);
        listViewConversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                Local.pushAccount(conversationList.get(i).receiver);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}