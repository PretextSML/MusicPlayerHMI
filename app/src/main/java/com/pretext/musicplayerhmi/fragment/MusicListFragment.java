package com.pretext.musicplayerhmi.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.MusicInfoUtil;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.MusicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private static final String TAG = "[MusicListFragment]";
    private static MusicListFragment musicListFragment;
    private List<MusicInfoUtil> musicInfoUtilList;
    private View rootView;
    private Handler handler;
    private Context context;

    public static MusicListFragment getInstance() {
        if (musicListFragment == null)
            musicListFragment = new MusicListFragment();
        return musicListFragment;
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        musicInfoUtilList = readMusicFile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music_list, container, false);
        initMusicList();
        return rootView;
    }

    public void initMusicList() {
        RecyclerView musicListView = rootView.findViewById(R.id.music_list);
        MusicListAdapter musicListAdapter = new MusicListAdapter(musicInfoUtilList, context, handler);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        Log.d(TAG, "initMusicList: " + musicListAdapter);
        musicListView.setAdapter(musicListAdapter);
        musicListView.setLayoutManager(layoutManager);
    }

    public List<MusicInfoUtil> readMusicFile() {
        List<MusicInfoUtil> musicInfoUtilList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                musicInfoUtilList.add(
                        new MusicInfoUtil(
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        )
                );
            }
            cursor.close();
        } else {
            Log.d(TAG, "cursor is null!");
        }

        return musicInfoUtilList;
    }
}
