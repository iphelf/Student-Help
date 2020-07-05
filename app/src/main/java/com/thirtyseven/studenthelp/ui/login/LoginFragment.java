package com.thirtyseven.studenthelp.ui.login;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.thirtyseven.studenthelp.R;
import com.thirtyseven.studenthelp.data.Account;
import com.thirtyseven.studenthelp.ui.MainActivity;
import com.thirtyseven.studenthelp.utility.Global;
import com.thirtyseven.studenthelp.utility.Local;
import com.thirtyseven.studenthelp.utility.Remote;

public class LoginFragment extends Fragment implements Global {

    private Remote.RemoteBinder remoteBinder;
    private ServiceConnection serviceConnection;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                final Account account = new Account();
                account.username = username;
                account.password = password;
                remoteBinder.login(account, new Remote.Listener() {
                    @Override
                    public void execute(ResultCode resultCode, Object object) {
                        if (resultCode == ResultCode.Succeeded) {
                            Local.saveAccount(account);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            switch ((LoginError) object){
                                case NotExist:
                                    Toast.makeText(
                                            requireContext(),
                                            R.string.toast_loginError_notExist,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case WrongPassword:
                                    Toast.makeText(
                                            requireContext(),
                                            R.string.toast_loginError_wrongPassword,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case NetworkError:
                                    Toast.makeText(
                                            requireContext(),
                                            R.string.toast_networkError,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case LoginError:
                                default:
                                    Toast.makeText(
                                            requireContext(),
                                            R.string.toast_loginError,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                            }
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