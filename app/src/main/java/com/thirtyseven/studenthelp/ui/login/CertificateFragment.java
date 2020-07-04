package com.thirtyseven.studenthelp.ui.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thirtyseven.studenthelp.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CertificateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CertificateFragment extends Fragment {

    // TO-DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO-DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CertificateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CertificateFragment.
     */
    // TO-DO: Rename and change types and number of parameters
    public static CertificateFragment newInstance(String param1, String param2) {
        CertificateFragment fragment = new CertificateFragment();
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
        requireActivity().setTitle(R.string.title_certificate);
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_certificate, container, false);
        Button buttonCertificate=root.findViewById(R.id.button_certificate);
        buttonCertificate.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_certificateFragment_to_registerFragment)
        );
        return root;
    }
}