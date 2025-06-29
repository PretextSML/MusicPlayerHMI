package com.pretext.musicplayerhmi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.databinding.ActivityLoginBinding;
import com.pretext.musicplayerhmi.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding loginBinding;

    public void setCurrentUser(String username) {
        ((MusicPlayerApplication) getApplication()).setCurrentUser(username);
    }

    public void initObservers() {
        loginViewModel.getLoginResult().observe(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    setCurrentUser(result.getUsername());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    String error = result.getErrorMessage();
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginViewModel.getRegisterResult().observe(this, result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Toast.makeText(this, "Register successful!", Toast.LENGTH_SHORT).show();
                    setCurrentUser(result.getUsername());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    String error = result.getErrorMessage();
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginBinding.loginAsTourist.setOnClickListener(view -> {
            setCurrentUser("GUEST");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginBinding.setLoginViewModel(loginViewModel);
        loginBinding.setLifecycleOwner(this);

        MusicPlayerServiceConnection.getInstance().bindService(getApplicationContext());

        initObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(MusicPlayerServiceConnection.getInstance());
        MusicPlayerServiceConnection.getInstance().setMusicPlayerServiceConnection(null);
    }
}
