package com.pretext.musicplayerhmi.viewmodel;

import android.app.Application;
import android.app.Service;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class VolumeViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mVolumeLevel = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> mMaxVolumeLevel = new MutableLiveData<>(0);

    private AudioManager mAudioManager;

    public VolumeViewModel(@NonNull Application application) {
        super(application);

        initVolume();
    }

    public MutableLiveData<Integer> getVolumeLevel() {
        return mVolumeLevel;
    }

    public void setVolumeLevel(int level) {
        mVolumeLevel.setValue(level);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, AudioManager.FLAG_PLAY_SOUND);
    }

    public MutableLiveData<Integer> getMaxVolumeLevel() {
        return mMaxVolumeLevel;
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getApplication().getSystemService(Service.AUDIO_SERVICE);

        mVolumeLevel.setValue(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mMaxVolumeLevel.setValue(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    }

}
