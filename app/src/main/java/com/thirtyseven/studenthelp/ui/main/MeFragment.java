package com.thirtyseven.studenthelp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.ui.common.ZoneActivity;
import com.thirtyseven.studenthelp.ui.me.AboutActivity;
import com.thirtyseven.studenthelp.ui.me.AccountActivity;
import com.thirtyseven.studenthelp.ui.me.HistoryActivity;
import com.thirtyseven.studenthelp.ui.me.SettingsActivity;
import com.thirtyseven.studenthelp.ui.me.WalletActivity;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel =
                ViewModelProviders.of(this).get(MeViewModel.class);
        requireActivity().setTitle(R.string.title_me);
        View root = inflater.inflate(R.layout.fragment_me, container, false);
        Button buttonZone = root.findViewById(R.id.button_zone);
        buttonZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ZoneActivity.class);
                startActivity(intent);
            }
        });
        Button buttonAccount = root.findViewById(R.id.button_account);
        buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });
        Button buttonWallet = root.findViewById(R.id.button_wallet);
        buttonWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                startActivity(intent);
            }
        });
        Button buttonHistory = root.findViewById(R.id.button_history);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        Button buttonSettings = root.findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        Button buttonAbout = root.findViewById(R.id.button_about);
        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}