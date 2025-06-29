package com.pretext.musicplayerhmi.viewmodel;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.HistoryUtil;

import java.util.Objects;

public class HistoryViewModel extends AndroidViewModel {
    private static final String TAG = "[HistoryViewModel]";
    private final MutableLiveData<HistoryUtil> mUserHistory = new MutableLiveData<>(new HistoryUtil());

    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<HistoryUtil> getHistoryList() {
        return mUserHistory;
    }

    public void addHistoryList(String musicName) {
        if (mUserHistory.getValue() != null) {
            HistoryUtil currentHistory = mUserHistory.getValue();
            currentHistory.addMusic(musicName);
            mUserHistory.setValue(currentHistory);
            Log.d(TAG, "add to history: " + mUserHistory.getValue().getHistoryList().size());
            Objects.requireNonNull(mUserHistory.getValue()).addMusic(musicName);
            try {
                Log.d(TAG, "modify history to database");
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().setHistory(((MusicPlayerApplication) getApplication()).getCurrentUser(), mUserHistory.getValue().toJson());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getHistoryListFromDB() {
        try {
            String history = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().getHistory(((MusicPlayerApplication) getApplication()).getCurrentUser());
            if (history != null) {
                if (Objects.requireNonNull(mUserHistory.getValue()).fromGson(history) != null) {
                    mUserHistory.setValue(mUserHistory.getValue().fromGson(history));
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
