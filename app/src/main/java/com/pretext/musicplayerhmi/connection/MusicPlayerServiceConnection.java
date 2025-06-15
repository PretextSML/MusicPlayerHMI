package com.pretext.musicplayerhmi.connection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pretext.musicplayerhmi.MainActivity;
import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerservice.IMusicPlayerInterface;

public class MusicPlayerServiceConnection implements ServiceConnection {
    private final static String TAG = "[Connection]";
    private static MusicPlayerServiceConnection musicPlayerServiceConnection;
    private IMusicPlayerInterface iMusicPlayerInterface;
    private Boolean isConnected;

    public static MusicPlayerServiceConnection getInstance() {
        if (musicPlayerServiceConnection == null)
            musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        return musicPlayerServiceConnection;
    }

    public void setMusicPlayerServiceConnection(MusicPlayerServiceConnection musicPlayerServiceConnection) {
        MusicPlayerServiceConnection.musicPlayerServiceConnection = musicPlayerServiceConnection;
    }

    public IMusicPlayerInterface getMusicPlayerInterface() {
        return iMusicPlayerInterface;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            Log.d(TAG, "onServiceConnected");
            iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(service);
            iMusicPlayerInterface.registerCallback(MusicListFragment.getInstance().getCallback());
            iMusicPlayerInterface.startTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();

        musicPlayerIntent.setPackage("com.pretext.musicplayerservice");
        musicPlayerIntent.setAction("com.pretext.musicplayerservice.MusicPlayerService");

        isConnected = MainActivity.getContext().bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService: " + isConnected);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");

        iMusicPlayerInterface = null;
        isConnected = false;
    }
}