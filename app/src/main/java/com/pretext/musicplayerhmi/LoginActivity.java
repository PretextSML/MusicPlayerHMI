package com.pretext.musicplayerhmi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;

public class LoginActivity extends Activity {
    public static String currentUser = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MusicPlayerServiceConnection.getInstance().bindService(getApplicationContext());

        EditText userInput = findViewById(R.id.user_name);
        EditText passwordInput = findViewById(R.id.user_password);

        findViewById(R.id.login).setOnClickListener(v -> {
            String user = userInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username or Password can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    boolean isCorrect = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().authenticatedUser(user, password);
                    if (isCorrect) {
                        currentUser = user;
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Username or Password error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        findViewById(R.id.register).setOnClickListener(v -> {
            String user = userInput.getText().toString();
            String password = passwordInput.getText().toString();
            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username or Password can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    boolean isExists = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().addNewUser(user, password);
                    if (!isExists) {
                        Toast.makeText(getApplicationContext(), "Register successful, please login", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "User already exists!", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        findViewById(R.id.login_as_tourist).setOnClickListener(v -> {
            currentUser = "GUEST";
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(MusicPlayerServiceConnection.getInstance());
        MusicPlayerServiceConnection.getInstance().setMusicPlayerServiceConnection(null);
    }
}
