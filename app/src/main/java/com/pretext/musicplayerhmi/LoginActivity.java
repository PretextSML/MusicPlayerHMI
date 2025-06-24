package com.pretext.musicplayerhmi;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.databinding.ActivityLoginBinding;
import com.pretext.musicplayerhmi.util.UserUtil;
import com.pretext.musicplayerhmi.viewmodels.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding loginBinding;

    private void initViewModel() {
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new LoginViewModel();

        UserUtil userUtil = new UserUtil("", "");
        loginViewModel.getUser().setValue(userUtil);
        loginViewModel.getUser().observe(this, userUtil1 -> loginBinding.setLoginViewModel(loginViewModel));

        loginBinding.login.setOnClickListener(view -> {
            String account = Objects.requireNonNull(loginViewModel.getUser().getValue()).getAccount();
            String password = Objects.requireNonNull(loginViewModel.getUser().getValue()).getPassword();
            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Account or Password can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    boolean isCorrect = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().authenticatedUser(account, password);
                    if (isCorrect) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        currentUser = account;
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Username or Password error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        loginBinding.register.setOnClickListener(view -> {
            String account = Objects.requireNonNull(loginViewModel.getUser().getValue()).getAccount();
            String password = Objects.requireNonNull(loginViewModel.getUser().getValue()).getPassword();

            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Account or Password can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    boolean isExists = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().addNewUser(account, password);
                    if (!isExists) {
                        currentUser = account;
                        Toast.makeText(getApplicationContext(), "Register successful, automantic login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "User already exists!", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        loginBinding.loginAsTourist.setOnClickListener(view -> {
            currentUser = "GUEST";
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    public static String currentUser = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MusicPlayerServiceConnection.getInstance().bindService(getApplicationContext());

        initViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(MusicPlayerServiceConnection.getInstance());
        MusicPlayerServiceConnection.getInstance().setMusicPlayerServiceConnection(null);
    }
}
