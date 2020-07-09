package com.thirtyseven.studenthelp.ui.me;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.ui.common.CustomTitleBar;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private List<Errand> errandList;
    private ListView listViewErrandList;
    private SimpleAdapter simpleAdapter;
    private CustomTitleBar customTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        customTitleBar = findViewById(R.id.customTitleBar);
        customTitleBar.setLeftIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        customTitleBar.setTitle(R.string.title_history);

        Account account = Local.loadAccount();
        Remote.remoteBinder.queryHistoryList(account, new Remote.Listener() {
            @Override
            public void execute(Global.ResultCode resultCode, Object object) {
                if (resultCode == Global.ResultCode.Succeeded && object instanceof List) {
                    errandList = (List<Errand>) object;
                    push();
                } else {
                    switch ((Global.SearchCompositeError) object) {
                        case NetworkError:
                            Toast.makeText(
                                    HistoryActivity.this,
                                    R.string.toast_networkError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case SearchFailed:
                        default:
                            Toast.makeText(
                                    HistoryActivity.this,
                                    R.string.toast_searchError,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                }
            }
        });


    }

    final String[] fields = {
            "Thumbnail", "Title", "State", "Author", "Preview", "Money"
    };
    final int[] fieldIds = {
            R.id.imageView_thumbnail,
            R.id.textView_title,
            R.id.textView_state,
            R.id.textView_author,
            R.id.textView_preview,
            R.id.textView_money
    };

    public void push() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        listViewErrandList = findViewById(R.id.listView_errandList);
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
                HistoryActivity.this,
                mapList,
                R.layout.listviewitem_errand,
                fields,
                fieldIds
        );
        listViewErrandList.setAdapter(simpleAdapter);
//    listViewErrandList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent intent = new Intent(HistoryActivity.this, ErrandActivity.class);
//            startActivity(intent);
//        }
//    });
    }

}