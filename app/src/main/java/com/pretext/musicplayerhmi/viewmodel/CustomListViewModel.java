package com.pretext.musicplayerhmi.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pretext.musicplayerhmi.util.MusicInfoUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewModel extends AndroidViewModel {

    private static final String TAG = "[CustomListViewModel]";
    private final MutableLiveData<List<MusicInfoUtil>> musicList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> currentMusic = new MutableLiveData<>(-1);

    public CustomListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<MusicInfoUtil>> getMusicList() {
        return musicList;
    }

    public MutableLiveData<Integer> getCurrentMusic() {
        return currentMusic;
    }

    public void playNext() {
        if (!musicList.getValue().isEmpty()) {
            int current = currentMusic.getValue();
            if (current + 1 < musicList.getValue().size()) {
                Log.d(TAG, "playNext");
                currentMusic.setValue(current + 1);
            }
        }
    }

    public void playPrevious() {
        if (!musicList.getValue().isEmpty()) {
            int current = currentMusic.getValue();
            if (current - 1 >= 0) {
                Log.d(TAG, "playPrevious");
                currentMusic.setValue(current - 1);
            }
        }
    }

    public void reset() {
        currentMusic.setValue(-1);
    }

    public void addToMusicList(MusicInfoUtil musicInfo) {
        List<MusicInfoUtil> currentList = musicList.getValue();
        if (currentList != null) {
            List<MusicInfoUtil> newList = new ArrayList<>(currentList);
            newList.add(musicInfo);
            musicList.setValue(newList);
        }
        Log.d(TAG, "add to music list: " + musicInfo.getMusicName());
        Log.d(TAG, "current music list size: " + musicList.getValue().size());
    }
}
