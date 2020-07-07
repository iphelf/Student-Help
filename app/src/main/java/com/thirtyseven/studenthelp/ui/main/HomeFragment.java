package com.thirtyseven.studenthelp.ui.main;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;
import com.thirtyseven.studenthelp.ui.home.PublishActivity;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements Global {
    private HomeViewModel homeViewModel;

    private EditText editTextKeyword;
    private ImageButton imageButtonSearch;
    private ListView listViewErrandList;
    private FloatingActionButton floatingActionButtonPublish;
    private Spinner spinnerTag;
    private Spinner spinnerState;
    private SimpleAdapter simpleAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        requireActivity().setTitle(R.string.title_home);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        editTextKeyword = root.findViewById(R.id.editText_keyword);

        imageButtonSearch = root.findViewById(R.id.imageButton_search);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pull();
            }
        });

        floatingActionButtonPublish = root.findViewById(R.id.floatingActionButton_publish);
        floatingActionButtonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
            }
        });

        String[] tags = new String[Errand.TagName.length + 1];
        tags[0] = "全部";
        System.arraycopy(Errand.TagName, 0, tags, 1, Errand.TagName.length);
        spinnerTag = root.findViewById(R.id.spinner_tag);
        ArrayAdapter<String> arrayAdapterTag = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                tags
        );
        spinnerTag.setAdapter(arrayAdapterTag);
        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pull();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        String[] states = new String[Errand.StateName.length + 1];
        states[0] = "全部";
        System.arraycopy(Errand.StateName, 0, states, 1, Errand.StateName.length);
        spinnerState = root.findViewById(R.id.spinner_state);
        ArrayAdapter<String> arrayAdapterState = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                states
        );
        spinnerState.setAdapter(arrayAdapterState);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pull();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        listViewErrandList = root.findViewById(R.id.listView_errandList);
        listViewErrandList.setAdapter(simpleAdapter);
        listViewErrandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Local.pushErrand(errandList.get(i));
                Intent intent = new Intent(getActivity(), ErrandActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<Errand> errandList;

    public void pull() {
        String keyword = editTextKeyword.getText().toString().trim();
        if (keyword.length() == 0) keyword = "";
        int tag = spinnerTag.getSelectedItemPosition() - 1;
        int state = spinnerState.getSelectedItemPosition() - 1;
        Remote.remoteBinder.queryErrandList(null, keyword, tag, state, new Remote.Listener() {
            @Override
            public void execute(ResultCode resultCode, Object object) {
                if (resultCode == ResultCode.Succeeded && object instanceof List) {
                    errandList = (List<Errand>) object;
                    push();
                } else {
                    switch ((SearchCompositeError) object) {
                        case NetworkError:
                            Toast.makeText(
                                    requireContext(),
                                    R.string.toast_networkError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case SearchFailed:
                        default:
                            Toast.makeText(
                                    requireContext(),
                                    R.string.toast_searchError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                }
            }
        });
    }

    String[] fields = {
            "Thumbnail", "Title", "State", "Author", "Preview", "Money"
    };
    int[] fieldIds = {
            R.id.imageView_thumbnail,
            R.id.textView_title,
            R.id.textView_state,
            R.id.textView_author,
            R.id.textView_preview,
            R.id.textView_money
    };

    public void push() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        int n = errandList.size();
        for (Errand errand : errandList) {
            Map<String, Object> map = new HashMap<>();
            map.put("Thumbnail", R.drawable.ic_logo);
            map.put("Title", errand.getTitle());
            map.put("State", errand.getStateName());
            map.put("Author", errand.publisher.getName());
            map.put("Preview", errand.getContentPreview());
            map.put("Money", "悬赏: " + errand.money);
            mapList.add(map);
        }
        simpleAdapter = new SimpleAdapter(
                getContext(),
                mapList,
                R.layout.listviewitem_errand,
                fields,
                fieldIds
        );
        listViewErrandList.setAdapter(simpleAdapter);
    }

}