package com.thirtyseven.studenthelp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.MainActivity;


public class NoticeFragment extends Fragment {

    private NoticeViewModel noticeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noticeViewModel =
                ViewModelProviders.of(this).get(NoticeViewModel.class);
        requireActivity().setTitle(R.string.title_notice);
        View root = inflater.inflate(R.layout.fragment_notice, container, false);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        final NavController navController = Navigation.findNavController(root.findViewById(R.id.navHostFragment_notice));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.noticeConversationFragment,false)
                        .build();
                switch (tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.noticeConversationFragment, null, navOptions);
                        break;
                    case 1:
                        navController.navigate(R.id.noticeProgressFragment, null, navOptions);
                        break;
                    case 2:
                        navController.navigate(R.id.noticeAnnouncementFragment, null, navOptions);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        return root;
    }
}

