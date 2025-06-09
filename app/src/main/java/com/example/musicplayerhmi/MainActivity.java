package com.example.musicplayerhmi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerservice.IMusicPlayerInterface;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String username;
    private String password;

    private IMusicPlayerInterface iMusicPlayerInterface;
    private MusicPlayerServiceConnection musicPlayerServiceConnection;

    private boolean mIsConnected = false;

    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mLoginButton = findViewById(R.id.btn_test);

        mEditTextUsername = findViewById(R.id.text_username);
        mEditTextPassword = findViewById(R.id.text_password);


        bindService();

        if (mIsConnected) {
            mLoginButton.setOnClickListener(v -> {
                username = String.valueOf(mEditTextUsername.getText());
                password = String.valueOf(mEditTextPassword.getText());

                try {
                    boolean isAuthentication = iMusicPlayerInterface.checkcheckAuthentication(username, password);
                    if (isAuthentication) {
                        Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else
                        Toast.makeText(this, "Username or password error!", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            Toast.makeText(this, "Service is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();
        musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        musicPlayerIntent.setPackage("com.example.musicplayerservice");
        musicPlayerIntent.setAction("com.example.musicplayerservice.MusicPlayerService");
        mIsConnected = bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicPlayerServiceConnection);
        musicPlayerServiceConnection = null;
    }

    class MusicPlayerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(service);
            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMusicPlayerInterface = null;
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    }
}