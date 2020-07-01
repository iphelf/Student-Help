package com.thirtyseven.studenthelp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Errand;
import com.thirtyseven.studenthelp.ui.common.ConversationActivity;
import com.thirtyseven.studenthelp.ui.common.ErrandActivity;
import com.thirtyseven.studenthelp.ui.notice.AnnouncementActivity;

public class NoticeFragment extends Fragment {

    private NoticeViewModel noticeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noticeViewModel =
                ViewModelProviders.of(this).get(NoticeViewModel.class);
        requireActivity().setTitle(R.string.title_notice);
        View root = inflater.inflate(R.layout.fragment_notice, container, false);
        Button buttonDetail=root.findViewById(R.id.button_detail);
        buttonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ErrandActivity.class);
                startActivity(intent);
            }
        });
        Button buttonConversation=root.findViewById(R.id.button_conversation);
        buttonConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ConversationActivity.class);
                startActivity(intent);
            }
        });
        Button buttonAnnouncement=root.findViewById(R.id.button_announcement);
        buttonAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AnnouncementActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}