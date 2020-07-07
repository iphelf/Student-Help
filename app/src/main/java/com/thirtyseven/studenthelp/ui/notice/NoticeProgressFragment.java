package com.thirtyseven.studenthelp.ui.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Conversation;
import com.thirtyseven.studenthelp.data.Message;
import com.thirtyseven.studenthelp.data.Progress;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;
import com.thirtyseven.studenthelp.ui.common.adapter.ConversationListAdapter;
import com.thirtyseven.studenthelp.ui.common.adapter.ProgressListAdapter;
import com.thirtyseven.studenthelp.utility.Local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeProgressFragment extends Fragment {
    private ListView listViewProgressList;
    private List<Conversation> conversationList;
    private List<Conversation> progressList;
    private Map<String, Conversation> map;
    private List<Message> msgList;

    public NoticeProgressFragment() {
        // Required empty public constructor
    }

    public static NoticeProgressFragment newInstance(String param1, String param2) {
        return new NoticeProgressFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notice_progress, container, false);
        ListView listViewProgressList = root.findViewById(R.id.listView_progressList);
        conversationList = Local.loadConversationList();
        map = Local.loadConversationMap();
        Conversation conversation = map.get("server");
        msgList = conversation.messageList;
        List<Message> messageList = new ArrayList<>();
        for (Message msg : msgList) {
            if (msg.type != 1 && msg.type != 2 && msg.type != 3 && msg.type != 4) {
                messageList.add(msg);
            }
        }
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.date.compareTo(m2.date);
            }
        });
        Collections.reverse(messageList); //倒序 按时间从近到远

        listViewProgressList = root.findViewById(R.id.listView_progressList);
        ProgressListAdapter conversationListAdapter = new ProgressListAdapter(getContext(), messageList);
        listViewProgressList.setAdapter(conversationListAdapter);

        listViewProgressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ErrandActivity.class);
                startActivity(intent);
            }
        });

//        String[] fields = {
//                "活动状态更新", "更新内容"
//        };
//        int[] fieldIds = {
//                R.id.textView_title,
//                R.id.textView_preview
//        };
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        int n = 50;
//        for (int i = 0; i < n; i++) {
//            Map<String, Object> map = new HashMap<>();
//            for (String field : fields) map.put(field, field);
//            mapList.add(map);
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(
//                getContext(),
//                mapList,
//                R.layout.listviewitem_notice,
//                fields,
//                fieldIds
//        );
//        listViewProgressList.setAdapter(simpleAdapter);

        return root;
    }
}