package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.ProfileListAdapter;
import com.pretext.musicplayerhmi.databinding.FragmentProfileBinding;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String TAG = "[Profile]";

    private ProfileListAdapter profileListAdapter;
    private MusicPlayerViewModel musicPlayerViewModel;
    private CustomListViewModel customListViewModel;
    private FragmentProfileBinding fragmentProfileBinding;

    public void initMusicList() {
        profileListAdapter = new ProfileListAdapter(getContext(), customListViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + profileListAdapter);
        fragmentProfileBinding.musicList.setAdapter(profileListAdapter);
        fragmentProfileBinding.musicList.setLayoutManager(layoutManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        musicPlayerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        customListViewModel = new ViewModelProvider(requireActivity()).get(CustomListViewModel.class);
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        fragmentProfileBinding.setCustomListViewModel(customListViewModel);
        fragmentProfileBinding.setLifecycleOwner(getViewLifecycleOwner());

        initMusicList();

        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentProfileBinding.btnPlayAll.setOnClickListener(v -> {
            if (!Objects.requireNonNull(customListViewModel.getMusicList().getValue()).isEmpty()) {
                customListViewModel.playNext();
            }
        });

        customListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            Log.d(TAG, "Observe music list change.");
            profileListAdapter.notifyItemInserted(musicList.size());
        });

        customListViewModel.getCurrentMusic().observe(getViewLifecycleOwner(), currentMusic -> {
            if (currentMusic != -1) {
                try {
                    musicPlayerViewModel.playMusic(Objects.requireNonNull(customListViewModel.getMusicList().getValue()).get(currentMusic), true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                musicPlayerViewModel.stopMusic();
            }
        });
    }
}
