package com.thirtyseven.studenthelp.ui.notice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import java.util.List;
import java.util.Map;

public class NoticeConversationFragment extends Fragment {

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

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
        requireActivity().bindService(
                new Intent(requireContext(), Remote.class),
                serviceConnection,
                Service.BIND_AUTO_CREATE
        );
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
                        Local.saveConversationMap((Map<String, Conversation>) object);
                        push();
                    }
                }
        );
    }

    public void push() {
        conversationList = Local.loadConversationList();
        ConversationListAdapter conversationListAdapter = new ConversationListAdapter(getContext(), conversationList);
        listViewConversationList.setAdapter(conversationListAdapter);
        listViewConversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (serviceConnection != null)
            requireActivity().unbindService(serviceConnection);
        super.onDestroy();
    }
}