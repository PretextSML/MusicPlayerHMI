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
    private final MutableLiveData<HistoryUtil> userHistory = new MutableLiveData<>(new HistoryUtil());

    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<HistoryUtil> getHistoryList() {
        return userHistory;
    }

    public void addHistorList(String musicName) {
        if (userHistory.getValue() != null) {
            HistoryUtil currentHistory = userHistory.getValue();
            currentHistory.addMusic(musicName);
            userHistory.setValue(currentHistory);
            Log.d(TAG, "add to history: " + userHistory.getValue().getHistoryList().size());
            Objects.requireNonNull(userHistory.getValue()).addMusic(musicName);
            try {
                Log.d(TAG, "modify history to database");
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().setHistory(((MusicPlayerApplication) getApplication()).getCurrentUser(), userHistory.getValue().toJson());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getHistoryListFromDB() {
        try {
            String history = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().getHistory(((MusicPlayerApplication) getApplication()).getCurrentUser());
            if (history != null) {
                if (Objects.requireNonNull(userHistory.getValue()).fromGson(history) != null) {
                    userHistory.setValue(userHistory.getValue().fromGson(history));
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
