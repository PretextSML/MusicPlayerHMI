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
    private IMusicProgressCallback iMusicProgressCallback;
    private Boolean mIsConnected;

    public static MusicPlayerServiceConnection getInstance() {
        if (musicPlayerServiceConnection == null)
            musicPlayerServiceConnection = new MusicPlayerServiceConnection();
        return musicPlayerServiceConnection;
    }

    public void setMusicPlayerServiceConnection(MusicPlayerServiceConnection musicPlayerServiceConnection) {
        MusicPlayerServiceConnection.musicPlayerServiceConnection = musicPlayerServiceConnection;
    }

    public void setMusicProgressCallback(IMusicProgressCallback iMusicProgressCallback) {
        this.iMusicProgressCallback = iMusicProgressCallback;
    }

    public IMusicPlayerInterface getMusicPlayerInterface() {
        return iMusicPlayerInterface;
    }

    public Boolean getIsConnected() {
        return mIsConnected;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(service);
        try {
            iMusicPlayerInterface.setDBHelper();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void activateService() {
        try {
            iMusicPlayerInterface.registerCallback(iMusicProgressCallback);
            iMusicPlayerInterface.startTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void bindService(Context context) {
        Intent musicPlayerIntent = new Intent();

        musicPlayerIntent.setPackage("com.pretext.musicplayerservice");
        musicPlayerIntent.setAction("com.pretext.musicplayerservice.MusicPlayerService");

        mIsConnected = context.bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService: " + mIsConnected);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");

        iMusicPlayerInterface = null;
        mIsConnected = false;
    }
}