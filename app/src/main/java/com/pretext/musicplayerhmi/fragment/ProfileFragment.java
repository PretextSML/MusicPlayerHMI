package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.os.Handler;
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

import com.pretext.musicplayerhmi.MusicInfoUtil;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.ProfileListAdapter;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = "[Profile]";
    private static ProfileFragment profileFragment;

    private final List<MusicInfoUtil> playList = new ArrayList<>();
    private LinearLayout playAllBtn;
    private View rootView;
    private Handler handler;
    private RecyclerView musicListView;
    private ProfileListAdapter profileListAdapter;
    private Message message;
    private Bundle bundle;
    private int currentMusicID;
    private boolean isPlaying = false;

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
        profileListAdapter = new ProfileListAdapter(playList, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + profileListAdapter);
        musicListView.setAdapter(profileListAdapter);
        musicListView.setLayoutManager(layoutManager);
    }

    public boolean isInPlayList(MusicInfoUtil info) {
        return playList.contains(info);
    }

    public void addToPlayList(MusicInfoUtil music) {
        playList.add(music);
        profileListAdapter.notifyItemInserted(playList.size());
    }

    public boolean playNext() {
        reset(false);
        musicListView.smoothScrollToPosition(currentMusicID + 1 >= playList.size() ? playList.size() - 1 : currentMusicID + 1);
        Log.d(TAG, "playNext: " + playList.size());
        if (currentMusicID < playList.size()) {
            Log.d(TAG, "playNext: " + true);
            playMusic(playList.get(currentMusicID));
            return true;
        } else {
            reset(true);
            return false;
        }
    }

    public void playNextMusic() {
        if (currentMusicID < playList.size() && isPlaying) {
            reset(false);
            musicListView.smoothScrollToPosition(currentMusicID + 1 >= playList.size() ? playList.size() - 1 : currentMusicID + 1);
            playMusic(playList.get(currentMusicID));
        }
    }

    public void playPreviousMusic() {
        if (currentMusicID - 2 >= 0 && isPlaying) {
            reset(false);
            currentMusicID -= 2;
            musicListView.smoothScrollToPosition(Math.max(currentMusicID - 1, 0));
            playMusic(playList.get(currentMusicID));
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
        playAllBtn.setOnClickListener(v -> {
            Log.d(TAG, "playList: " + playList.size());
            if (!playList.isEmpty()) {
                reset(true);
                isPlaying = true;
                playMusic(playList.get(currentMusicID));
            }
        });
    }


    public void reset(boolean isForceStop) {
        Log.d(TAG, "reset");
        MusicListViewHolder viewHolder = profileListAdapter.getViewHolder(Math.max(currentMusicID - 1, 0));
        if (viewHolder != null)
            viewHolder.rootView.setBackgroundResource(R.drawable.button_bg);
        if (isForceStop) {
            musicListView.smoothScrollToPosition(0);
            isPlaying = false;
            currentMusicID = 0;
        }

    }

    private void playMusic(MusicInfoUtil info) {
        profileListAdapter.getViewHolder(currentMusicID).rootView.setBackgroundResource(R.drawable.bg_playing);
        currentMusicID++;

        bundle = new Bundle();
        bundle.putSerializable("playMusic", info);
        bundle.putBoolean("fromList", true);

        message = new Message();
        message.what = 1;
        message.setData(bundle);

        handler.sendMessage(message);
    }
}
