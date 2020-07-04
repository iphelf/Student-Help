package com.thirtyseven.studenthelp.ui.login;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thirtyseven.studenthelp.ui.MainActivity;
import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TO-DO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TO-DO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TO-DO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                remoteBinder = (Remote.RemoteBinder) iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        requireActivity().bindService(
                new Intent(requireContext(), Remote.class),
                serviceConnection,
                Service.BIND_AUTO_CREATE
        );
    }

    @Override
    public void onDestroy() {
        requireActivity().unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_login);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText editTextUsername = root.findViewById(R.id.editText_username);
        final EditText editTextPassword = root.findViewById(R.id.editText_password);

        final Button buttonLogin = root.findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TO-DO:
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                Account account=new Account();
                account.username=username;
                account.password=password;
                remoteBinder.login(account, new Remote.Listener() {
                    @Override
                    public void execute(Global.ResultCode resultCode, Object object) {
                        if(resultCode== Global.ResultCode.Succeeded){
                            Local.saveAccount(new Account());
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                        else{
                            Toast.makeText(
                                    requireContext(),
                                    R.string.toast_loginError,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
            }
        });

        Button buttonRegister = root.findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment, null)
        );

        return root;
    }
}