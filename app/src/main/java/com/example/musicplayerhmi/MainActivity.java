package com.example.musicplayerhmi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayerservice.IMusicPlayerInterface;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private IMusicPlayerInterface iMusicPlayerInterface;
    private MusicPlayerServiceConnection musicPlayerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button testButton = findViewById(R.id.btn_test);
        testButton.setOnClickListener(v -> {
            try {
                iMusicPlayerInterface.serviceTest();
            } catch (RemoteException e) {
                Log.e(TAG, String.valueOf(e));
            }
        });

        bindService();
    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();
        musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        musicPlayerIntent.setPackage("com.example.musicplayerservice");
        musicPlayerIntent.setAction("com.example.musicplayerservice.MusicPlayerService");
        bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
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