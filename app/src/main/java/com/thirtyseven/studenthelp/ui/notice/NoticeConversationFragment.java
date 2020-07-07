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
import com.thirtyseven.studenthelp.ui.common.ConversationActivity;
import com.thirtyseven.studenthelp.ui.common.adapter.ConversationListAdapter;
import com.thirtyseven.studenthelp.utility.Local;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
        conversationList= Local.loadConversationList();
        listViewConversationList = root.findViewById(R.id.listView_conversationList);
        ConversationListAdapter conversationListAdapter = new ConversationListAdapter(getContext(), conversationList);
        listViewConversationList.setAdapter(conversationListAdapter);
        listViewConversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                startActivity(intent);
            }
        });




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
       // listViewConversationList.setAdapter(simpleAdapter);
       // push();
        return root;
    }
//    String[] fields = {
//            "联系人", "最后一条消息","时间"
//    };
//    int[] fieldIds = {
//            R.id.textView_title,
//            R.id.textView_preview,
//            R.id.textView_time
//    };
//
//    public void push(){
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        int n=conversationList.size();
//        for(Conversation conversation:conversationList){
//            Map<String,Object> map=new HashMap<>();
//            map.put("联系人",conversation.getSender().id);
//            map.put("最后一条消息",conversation.messageLatest.content);
//            SimpleDateFormat formatter = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");
//            map.put("时间",formatter.format(conversation.getMessage().date));
//            mapList.add(map);
//        }
//        SimpleAdapter simpleAdapter = new SimpleAdapter(
//                getContext(),
//                mapList,
//                R.layout.listviewitem_notice,
//                fields,
//                fieldIds
//        );
//        listViewConversationList.setAdapter(simpleAdapter);
//    }
}