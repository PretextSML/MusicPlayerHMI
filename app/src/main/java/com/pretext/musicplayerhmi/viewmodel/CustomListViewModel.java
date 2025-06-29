package com.pretext.musicplayerhmi.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pretext.musicplayerhmi.util.MusicInfoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomListViewModel extends ViewModel {

    private static final String TAG = "[CustomListViewModel]";
    private final MutableLiveData<List<MusicInfoUtil>> mMusicList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> mCurrentMusic = new MutableLiveData<>(-1);

    public MutableLiveData<List<MusicInfoUtil>> getMusicList() {
        return mMusicList;
    }

    public MutableLiveData<Integer> getCurrentMusic() {
        return mCurrentMusic;
    }

    public void playNext() {
        if (!Objects.requireNonNull(mMusicList.getValue()).isEmpty()) {
            int current = mCurrentMusic.getValue() != null ? mCurrentMusic.getValue() : -1;
            if (current + 1 < mMusicList.getValue().size()) {
                Log.d(TAG, "playNext");
                mCurrentMusic.setValue(current + 1);
            } else {
                reset();
            }
        }
    }

    public void playPrevious() {
        if (!Objects.requireNonNull(mMusicList.getValue()).isEmpty()) {
            int current = mCurrentMusic.getValue() != null ? mCurrentMusic.getValue() : -1;
            if (current - 1 >= 0) {
                Log.d(TAG, "playPrevious");
                mCurrentMusic.setValue(current - 1);
            } else {
                reset();
            }
        }
    }

    public void reset() {
        mCurrentMusic.setValue(-1);
    }

    public void addToMusicList(MusicInfoUtil musicInfo) {
        List<MusicInfoUtil> currentList = mMusicList.getValue();
        if (currentList != null) {
            List<MusicInfoUtil> newList = new ArrayList<>(currentList);
            newList.add(musicInfo);
            mMusicList.setValue(newList);
        }
        Log.d(TAG, "add to music list: " + musicInfo.getMusicName());
        Log.d(TAG, "current music list size: " + mMusicList.getValue().size());
    }
}
