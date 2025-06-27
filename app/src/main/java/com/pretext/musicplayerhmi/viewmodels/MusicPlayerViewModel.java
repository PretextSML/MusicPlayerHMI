package com.pretext.musicplayerhmi.viewmodels;

import android.app.Application;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.MusicInfoUtil;

public class MusicPlayerViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isPaused = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> fromList = new MutableLiveData<>(false);

    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> maxProgress = new MutableLiveData<>(0);

    private final MutableLiveData<MusicInfoUtil> currentMusic = new MutableLiveData<>();
    private final MutableLiveData<String> currentMusicName = new MutableLiveData<>("");

    public MusicPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public MutableLiveData<Boolean> getIsPaused() {
        return isPaused;
    }

    public MutableLiveData<Boolean> getFromList() {
        return fromList;
    }

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }

    public MutableLiveData<Integer> getMaxProgress() {
        return maxProgress;
    }

    public MutableLiveData<MusicInfoUtil> getCurrentMusic() {
        return currentMusic;
    }

    public MutableLiveData<String> getCurrentMusicName() {
        return currentMusicName;
    }

    public void playMusic(MusicInfoUtil music, boolean isFromList) throws RemoteException {
        MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().playMusic(music.getMusicPath());

        fromList.setValue(isFromList);

        isPlaying.setValue(true);
        isPaused.setValue(false);

        currentMusic.setValue(music);
        currentMusicName.setValue(music.getMusicName());

        maxProgress.setValue((int) music.getMusicDuration());
    }

    public void switchPlayAndPause() {
        try {
            if (Boolean.TRUE.equals(isPlaying.getValue())) {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().pauseMusic();

                isPlaying.setValue(false);
                isPaused.setValue(true);
            } else if (Boolean.TRUE.equals(isPaused.getValue())) {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().resumeMusic();

                isPlaying.setValue(true);
                isPaused.setValue(false);
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

            isPlaying.setValue(false);
            isPaused.setValue(false);

            currentMusicName.setValue("");
            progress.setValue(0);
            maxProgress.setValue(0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
