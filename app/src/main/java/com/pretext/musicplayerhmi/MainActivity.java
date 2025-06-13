package com.pretext.musicplayerhmi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pretext.musicplayerservice.IMusicPlayerInterface;
import com.pretext.musicplayerservice.MusicInfo;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static IMusicPlayerInterface iMusicPlayerInterface;

    private List<MusicInfo> musicInfoList;
    private MusicPlayerServiceConnection musicPlayerServiceConnection;
    private boolean mIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();

        Button getMusicList = findViewById(R.id.btn_get_music_list);
        Button playMusic = findViewById(R.id.btn_play_music);
        TextView musicListView = findViewById(R.id.music_list);
        EditText musicId = findViewById(R.id.input_music_id);

        getMusicList.setOnClickListener(v -> {
            if (!mIsConnected) {
                Toast.makeText(MainActivity.this, "Service not connected!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder builder = new StringBuilder();
            try {
                musicInfoList = iMusicPlayerInterface.readMusicFile();
                for (int i = 0; i < musicInfoList.size(); i++) {
                    String info = String.format("%s: %s\n", i + 1, musicInfoList.get(i).getMusicName());
                    builder.append(info);
                }

                musicListView.setText(builder.toString());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        playMusic.setOnClickListener(v -> {
            if (!mIsConnected) {
                Toast.makeText(MainActivity.this, "Service not connected!", Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                int id = Integer.parseInt(String.valueOf(musicId.getText()));
                if (id > 0 && id <= musicInfoList.size())
                    iMusicPlayerInterface.playMusic(musicInfoList.get(id - 1).getMusicPath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();
        musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        musicPlayerIntent.setPackage("com.pretext.musicplayerservice");
        musicPlayerIntent.setAction("com.pretext.musicplayerservice.MusicPlayerService");
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