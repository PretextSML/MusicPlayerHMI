package com.pretext.musicplayerhmi.viewmodel;

import android.app.Application;
import android.app.Service;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class VolumeViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> volumeLevel = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> maxVolumeLevel = new MutableLiveData<>(0);

    private AudioManager audioManager;

    public VolumeViewModel(@NonNull Application application) {
        super(application);

        initVolume();
    }

    public MutableLiveData<Integer> getVolumeLevel() {
        return volumeLevel;
    }

    public void setVolumeLevel(int level) {
        volumeLevel.setValue(level);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, AudioManager.FLAG_PLAY_SOUND);
    }

    public MutableLiveData<Integer> getMaxVolumeLevel() {
        return maxVolumeLevel;
    }

    private void initVolume() {
        audioManager = (AudioManager) getApplication().getSystemService(Service.AUDIO_SERVICE);

        volumeLevel.setValue(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        maxVolumeLevel.setValue(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    }

}
