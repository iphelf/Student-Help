package com.thirtyseven.studenthelp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;
import com.thirtyseven.studenthelp.ui.home.PublishActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        requireActivity().setTitle(R.string.title_home);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton floatingActionButtonPublish = root.findViewById(R.id.floatingActionButton_publish);
        floatingActionButtonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
            }
        });

        String[] tags = {
                "全部", "代领快递", "寻物启事", "二手交易", "组队征集", "学习辅导", "问卷调查", "其他"
        };
        Spinner spinnerTag = root.findViewById(R.id.spinner_tags);
        ArrayAdapter<String> arrayAdapterTag = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                tags
        );
        spinnerTag.setAdapter(arrayAdapterTag);


        String[] types = {
                getString(R.string.type_all),
                getString(R.string.type_deal),
                getString(R.string.type_group)
        };
        Spinner spinnerType = root.findViewById(R.id.spinner_type);
        ArrayAdapter<String> arrayAdapterType = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                types
        );
        spinnerType.setAdapter(arrayAdapterType);

        String[] states = {
                getString(R.string.state_all),
                getString(R.string.state_waiting),
                getString(R.string.state_ongoing),
                getString(R.string.state_complete),
                getString(R.string.state_deleted)
        };
        Spinner spinnerState = root.findViewById(R.id.spinner_state);
        ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                states
        );
        spinnerState.setAdapter(arrayAdapterState);

        String[] fields = {
                "Thumbnail", "Title", "State", "Author", "Preview", "Type"
        };
        int[] fieldIds = {
                R.id.imageView_thumbnail,
                R.id.textView_title,
                R.id.textView_state,
                R.id.textView_author,
                R.id.textView_preview,
                R.id.textView_type
        };
        List<Map<String, Object>> mapList = new ArrayList<>();
        int n = 50;
        for (int i = 0; i < n; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(fields[0], R.drawable.ic_logo);
            for (int j = 1; j < fields.length; j++) map.put(fields[j], fields[j]);
            mapList.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getContext(),
                mapList,
                R.layout.listviewitem_errand,
                fields,
                fieldIds
        );
        ListView listViewErrandList = root.findViewById(R.id.listView_errandList);
        listViewErrandList.setAdapter(simpleAdapter);
        listViewErrandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ErrandActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}