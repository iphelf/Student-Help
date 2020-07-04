package com.thirtyseven.studenthelp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.thirtyseven.studenthelp.R;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_register);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        CheckBox checkBoxCertificate = root.findViewById(R.id.checkBox_certificate);
        checkBoxCertificate.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_certificateFragment)
        );
        Button buttonRegister = root.findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
        );
        return root;
    }
}