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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeAnnouncementFragment extends Fragment {

    public NoticeAnnouncementFragment() {
        // Required empty public constructor
    }

    public static NoticeAnnouncementFragment newInstance() {
        return new NoticeAnnouncementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notice_announcement, container, false);
        ListView listViewAnnouncementList = root.findViewById(R.id.listView_announcementList);
        listViewAnnouncementList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AnnouncementActivity.class);
                startActivity(intent);
            }
        });

        String[] fields = {
                "公告", "公告内容"
        };
        int[] fieldIds = {
                R.id.textView_title,
                R.id.textView_preview
        };
        List<Map<String, Object>> mapList = new ArrayList<>();
        int n = 50;
        for (int i = 0; i < n; i++) {
            Map<String, Object> map = new HashMap<>();
            for (String field : fields) map.put(field, field);
            mapList.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                mapList,
                R.layout.listviewitem_notice,
                fields,
                fieldIds
        );
        listViewAnnouncementList.setAdapter(simpleAdapter);

        return root;
    }
}