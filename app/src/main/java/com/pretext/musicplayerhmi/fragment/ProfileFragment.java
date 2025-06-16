package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.MusicInfo;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.ProfileListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = "[Profile]";
    private static ProfileFragment profileFragment;
    private List<MusicInfo> playList = new ArrayList<>();
    private LinearLayout playAllBtn;
    private View rootView;
    private Handler handler;
    private int currentMusicID = 0;
    private RecyclerView musicListView;
    private ProfileListAdapter profileListAdapter;
    private Message message;
    private Bundle bundle;

    public static ProfileFragment getInstance() {
        if (profileFragment == null)
            profileFragment = new ProfileFragment();
        return profileFragment;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void initMusicList() {
        musicListView = rootView.findViewById(R.id.music_list);
        profileListAdapter = new ProfileListAdapter(playList, getContext(), getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + profileListAdapter);
        musicListView.setAdapter(profileListAdapter);
        musicListView.setLayoutManager(layoutManager);
    }

    public boolean isInPlayList(MusicInfo info) {
        return playList.contains(info);
    }

    public void addToPlayList(MusicInfo music) {
        playList.add(music);
        profileListAdapter.notifyItemInserted(playList.size());
    }

    public boolean playNext() {
        Log.d(TAG, "playNext: " + playList.size());
        if (playList.size() > 1) {
            Log.d(TAG, "playNext: " + true);
            new Handler(Looper.getMainLooper()).post(() -> {
                playList.remove(0);
                playMusic(playList.get(0), true);
                profileListAdapter.notifyItemRemoved(0);
            });
            return true;
        } else {
            Log.d(TAG, "playNext: " + false);
            playList.clear();
            new Handler(Looper.getMainLooper()).post(() -> profileListAdapter.notifyItemRemoved(0));
            Log.d(TAG, "No music in list!");
            return false;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initMusicList();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playAllBtn = view.findViewById(R.id.btn_play_all);
        playAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "playList: " + playList.size());
                if (!playList.isEmpty()) {
                    playMusic(playList.get(0), true);
                }
            }
        });
    }

    private void playMusic(MusicInfo info, boolean isFromList) {
        bundle = new Bundle();
        bundle.putSerializable("playMusic", info);
        bundle.putBoolean("fromList", isFromList);

        message = new Message();
        message.what = 1;
        message.setData(bundle);

        handler.sendMessage(message);
    }
}
