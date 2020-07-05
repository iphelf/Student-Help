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
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeProgressFragment extends Fragment {

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
        listViewProgressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ErrandActivity.class);
                startActivity(intent);
            }
        });

        String[] fields = {
                "活动状态更新", "更新内容"
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
        listViewProgressList.setAdapter(simpleAdapter);

        return root;
    }
}