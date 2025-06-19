package com.pretext.musicplayerhmi.connection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pretext.musicplayerservice.IMusicPlayerInterface;
import com.pretext.musicplayerservice.IMusicProgressCallback;

public class MusicPlayerServiceConnection implements ServiceConnection {
    private final static String TAG = "[Connection]";
    private static MusicPlayerServiceConnection musicPlayerServiceConnection;
    private IMusicPlayerInterface iMusicPlayerInterface;
    private IMusicProgressCallback musicProgressCallback;
    private Boolean isConnected;
    private IBinder iBinder;

    public static MusicPlayerServiceConnection getInstance() {
        if (musicPlayerServiceConnection == null)
            musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        return musicPlayerServiceConnection;
    }

    public void setMusicPlayerServiceConnection(MusicPlayerServiceConnection musicPlayerServiceConnection) {
        MusicPlayerServiceConnection.musicPlayerServiceConnection = musicPlayerServiceConnection;
    }

    public void setMusicProgressCallback(IMusicProgressCallback musicProgressCallback) {
        this.musicProgressCallback = musicProgressCallback;
    }

    public IMusicPlayerInterface getMusicPlayerInterface() {
        return iMusicPlayerInterface;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        iBinder = service;
        iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(iBinder);
        try {
            iMusicPlayerInterface.setDBHelper();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void activateService() {
        try {
            iMusicPlayerInterface.registerCallback(musicProgressCallback);
            iMusicPlayerInterface.startTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void bindService(Context context) {
        Intent musicPlayerIntent = new Intent();

        musicPlayerIntent.setPackage("com.pretext.musicplayerservice");
        musicPlayerIntent.setAction("com.pretext.musicplayerservice.MusicPlayerService");

        isConnected = context.bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService: " + isConnected);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");

        iMusicPlayerInterface = null;
        isConnected = false;
    }
}