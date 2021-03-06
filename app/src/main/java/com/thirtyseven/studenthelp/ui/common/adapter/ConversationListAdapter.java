package com.thirtyseven.studenthelp.ui.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Conversation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ConversationListAdapter extends BaseAdapter {
    private Context context;
    private List<Conversation>list;
    public ConversationListAdapter(Context context, List<Conversation> list){
        this.context=context;
        this.list=list;

    }
    @Override
    public int getCount() {
        if(list==null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(context).inflate(R.layout.listviewitem_notice,null);
        TextView textView_title=view.findViewById(R.id.textView_title);
        TextView textView_preview=view.findViewById(R.id.textView_preview);
        TextView textView_time=view.findViewById(R.id.textView_time);
        textView_title.setText(list.get(i).receiver.id);
        textView_preview.setText(list.get(i).messageLatest.content);
        SimpleDateFormat formatter = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");
        textView_time.setText(formatter.format(list.get(i).messageLatest.date));

        //添加signal的判断 判断消息是否查看
        return view;
    }
}
