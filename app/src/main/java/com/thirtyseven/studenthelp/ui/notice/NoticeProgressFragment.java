package com.thirtyseven.studenthelp.ui.notice;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoticeProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticeProgressFragment extends Fragment {

    // TO-DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO-DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoticeProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticeProgressFragment.
     */
    // TO-DO: Rename and change types and number of parameters
    public static NoticeProgressFragment newInstance(String param1, String param2) {
        NoticeProgressFragment fragment = new NoticeProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                "Title", "Preview"
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