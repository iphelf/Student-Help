package com.thirtyseven.studenthelp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.thirtyseven.studenthelp.R;

public class CertificateFragment extends Fragment {

    public CertificateFragment() {
        // Required empty public constructor
    }

    public static CertificateFragment newInstance() {
        return new CertificateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_certificate);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_certificate, container, false);
        Button buttonCertificate = root.findViewById(R.id.button_certificate);
        buttonCertificate.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_certificateFragment_to_registerFragment)
        );
        return root;
    }
}