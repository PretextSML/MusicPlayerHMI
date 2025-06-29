package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.ProfileListAdapter;
import com.pretext.musicplayerhmi.databinding.FragmentProfileBinding;
import com.pretext.musicplayerhmi.util.MusicInfoUtil;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String TAG = "[Profile]";
    private static ProfileFragment profileFragment;

    private final List<MusicInfoUtil> playList = new ArrayList<>();
    private LinearLayout playAllBtn;
    private View rootView;
    private RecyclerView musicListView;
    private ProfileListAdapter profileListAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private CustomListViewModel customListViewModel;
    private FragmentProfileBinding fragmentProfileBinding;
    private int currentMusicID;
    private boolean isPlaying = false;

    public static ProfileFragment getInstance() {
        if (profileFragment == null)
            profileFragment = new ProfileFragment();
        return profileFragment;
    }

    public void initMusicList() {
        profileListAdapter = new ProfileListAdapter(getContext(), customListViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + profileListAdapter);
        fragmentProfileBinding.musicList.setAdapter(profileListAdapter);
        fragmentProfileBinding.musicList.setLayoutManager(layoutManager);
    }

    public boolean isInPlayList(MusicInfoUtil info) {
        return playList.contains(info);
    }

    public void addToPlayList(MusicInfoUtil music) {
//        playList.add(music);
//        profileListAdapter.notifyItemInserted(playList.size());
    }

    public boolean playNext() {
        reset(false);
        musicListView.smoothScrollToPosition(currentMusicID + 1 >= playList.size() ? playList.size() - 1 : currentMusicID + 1);
        Log.d(TAG, "playNext: " + playList.size());
        if (currentMusicID < playList.size()) {
            Log.d(TAG, "playNext: " + true);

            try {
                musicPlayerViewModel.playMusic(playList.get(currentMusicID), true);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            return true;
        } else {
            reset(true);
            return false;
        }
    }

    public void playNextMusic() {
        if (currentMusicID + 1 < playList.size() && isPlaying) {
            reset(false);
            musicListView.smoothScrollToPosition(currentMusicID + 1 >= playList.size() ? playList.size() - 1 : currentMusicID + 1);
            try {
                currentMusicID++;
                musicPlayerViewModel.playMusic(playList.get(currentMusicID), true);
                profileListAdapter.getViewHolder(currentMusicID).rootView.setBackgroundResource(R.drawable.bg_playing);

                Log.d(TAG, "playNextMusic: " + currentMusicID);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void playPreviousMusic() {
        if (currentMusicID - 1 >= 0 && isPlaying) {
            reset(false);
            musicListView.smoothScrollToPosition(Math.max(currentMusicID - 1, 0));
            try {
                currentMusicID--;
                musicPlayerViewModel.playMusic(playList.get(currentMusicID), true);
                profileListAdapter.getViewHolder(currentMusicID).rootView.setBackgroundResource(R.drawable.bg_playing);

                Log.d(TAG, "playPreviousMusic: " + currentMusicID);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        musicPlayerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        customListViewModel = new ViewModelProvider(requireActivity()).get(CustomListViewModel.class);
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        fragmentProfileBinding.setLifecycleOwner(getViewLifecycleOwner());

        initMusicList();

        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentProfileBinding.btnPlayAll.setOnClickListener(v -> {
            if (!Objects.requireNonNull(customListViewModel.getMusicList().getValue()).isEmpty()) {
                Log.d(TAG, "play music");
                reset(true);
                isPlaying = true;
                try {
                    musicPlayerViewModel.playMusic(customListViewModel.getMusicList().getValue().get(currentMusicID), true);
                    profileListAdapter.getViewHolder(currentMusicID).rootView.setBackgroundResource(R.drawable.bg_playing);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        customListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            Log.d(TAG, "Observe music list change.");
            profileListAdapter.notifyItemInserted(musicList.size());
        });
    }


    public void reset(boolean isForceStop) {
        Log.d(TAG, "reset");
        MusicListViewHolder viewHolder = profileListAdapter.getViewHolder(Math.max(currentMusicID, 0));
        if (viewHolder != null)
            viewHolder.rootView.setBackgroundResource(R.drawable.button_bg);
        if (isForceStop) {
            fragmentProfileBinding.musicList.smoothScrollToPosition(0);
            isPlaying = false;
            currentMusicID = 0;
        }

    }
}
