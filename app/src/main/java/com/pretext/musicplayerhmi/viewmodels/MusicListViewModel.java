package com.pretext.musicplayerhmi.viewmodels;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pretext.musicplayerhmi.util.MusicInfoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MusicListViewModel extends AndroidViewModel {
    private final MutableLiveData<List<MusicInfoUtil>> musicList = new MutableLiveData<>();

    public MusicListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<MusicInfoUtil>> getMusicList() {
        return musicList;
    }

    public void loadMusicFiles() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MusicInfoUtil> result = new ArrayList<>();
            Context context = getApplication().getApplicationContext();

            try (Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            )) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        result.add(new MusicInfoUtil(
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        ));
                    }

                    musicList.postValue(result);
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
