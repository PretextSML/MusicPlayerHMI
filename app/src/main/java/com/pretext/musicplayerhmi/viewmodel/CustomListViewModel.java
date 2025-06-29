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

    private final MutableLiveData<List<MusicInfoUtil>> musicList = new MutableLiveData<>(new ArrayList<>());

    public CustomListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<MusicInfoUtil>> getMusicList() {
        return musicList;
    }

    public void addToMusicList(MusicInfoUtil musicInfo) {
        List<MusicInfoUtil> currentList = musicList.getValue();
        if (currentList != null) {
            List<MusicInfoUtil> newList = new ArrayList<>(currentList);
            newList.add(musicInfo);
            musicList.setValue(newList);
        }
        Log.d("[CustomListViewModel]", "add to music list: " + musicInfo.getMusicName());
        Log.d("[CustomListViewModel]", "current music list size: " + musicList.getValue().size());
    }
}
