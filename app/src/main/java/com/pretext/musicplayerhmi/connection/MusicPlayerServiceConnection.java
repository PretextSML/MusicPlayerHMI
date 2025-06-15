package com.pretext.musicplayerhmi.connection;

import static com.pretext.musicplayerhmi.MainActivity.iMusicPlayerInterface;
import static com.pretext.musicplayerhmi.MainActivity.mIsConnected;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerservice.IMusicPlayerInterface;

public class MusicPlayerServiceConnection implements ServiceConnection {
    private final static String TAG = "[Connection]";
    private final MusicListFragment musicListFragment;

    public MusicPlayerServiceConnection(MusicListFragment musicListFragment) {
        this.musicListFragment = musicListFragment;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            Log.d(TAG, "onServiceConnected");
            iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(service);
            iMusicPlayerInterface.registerCallback(musicListFragment.getCallback());
            iMusicPlayerInterface.startTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        iMusicPlayerInterface = null;
        mIsConnected = false;
    }
}