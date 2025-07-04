package com.pretext.musicplayerhmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.databinding.ActivityLoginBinding;
import com.pretext.musicplayerhmi.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;
    private ActivityLoginBinding mLoginBinding;

    public void setCurrentUser(String username) {
        ((MusicPlayerApplication) getApplication()).setCurrentUser(username);
    }

    public void initObservers() {
        mLoginViewModel.getLoginResult().observe(this, result -> {
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

        mLoginViewModel.getRegisterResult().observe(this, result -> {
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

        mLoginBinding.loginAsTourist.setOnClickListener(view -> {
            setCurrentUser("GUEST");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mLoginBinding.setLoginViewModel(mLoginViewModel);
        mLoginBinding.setLifecycleOwner(this);

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
