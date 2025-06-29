package com.pretext.musicplayerhmi.viewmodel;

import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.MusicInfoUtil;

public class MusicPlayerViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mIsPaused = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> mFromList = new MutableLiveData<>(false);

    private final MutableLiveData<Integer> mProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> mMaxProgress = new MutableLiveData<>(0);

    private final MutableLiveData<MusicInfoUtil> mCurrentMusic = new MutableLiveData<>();
    private final MutableLiveData<String> mCurrentMusicName = new MutableLiveData<>("");

    public MutableLiveData<Boolean> getIsPlaying() {
        return mIsPlaying;
    }

    public MutableLiveData<Boolean> getIsPaused() {
        return mIsPaused;
    }

    public MutableLiveData<Boolean> getFromList() {
        return mFromList;
    }

    public MutableLiveData<Integer> getProgress() {
        return mProgress;
    }

    public MutableLiveData<Integer> getMaxProgress() {
        return mMaxProgress;
    }

    public MutableLiveData<MusicInfoUtil> getCurrentMusic() {
        return mCurrentMusic;
    }

    public MutableLiveData<String> getCurrentMusicName() {
        return mCurrentMusicName;
    }

    public void playMusic(@NonNull MusicInfoUtil music, boolean isFromList) throws RemoteException {
        MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().playMusic(music.getMusicPath());

        mFromList.setValue(isFromList);

        mIsPlaying.setValue(true);
        mIsPaused.setValue(false);

        mCurrentMusic.setValue(music);
        mCurrentMusicName.setValue(music.getMusicName());

        mMaxProgress.setValue((int) music.getMusicDuration());
    }

    public void switchPlayAndPause() {
        try {
            if (Boolean.TRUE.equals(mIsPlaying.getValue())) {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().pauseMusic();

                mIsPlaying.setValue(false);
                mIsPaused.setValue(true);
            } else if (Boolean.TRUE.equals(mIsPaused.getValue())) {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().resumeMusic();

                mIsPlaying.setValue(true);
                mIsPaused.setValue(false);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void seekTo(int position) {
        try {
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().setDuration(position);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopMusic() {
        try {
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopMusic();

            mIsPlaying.setValue(false);
            mIsPaused.setValue(false);
            mFromList.setValue(false);

            mCurrentMusicName.setValue("");
            mProgress.setValue(0);
            mMaxProgress.setValue(0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
