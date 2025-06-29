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

    private ProfileListAdapter mProfileListAdapter;
    private MusicPlayerViewModel mMusicPlayerViewModel;
    private CustomListViewModel mCustomListViewModel;
    private FragmentProfileBinding mFragmentProfileBinding;

    public void initMusicList() {
        mProfileListAdapter = new ProfileListAdapter(getContext(), mCustomListViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + mProfileListAdapter);
        mFragmentProfileBinding.musicList.setAdapter(mProfileListAdapter);
        mFragmentProfileBinding.musicList.setLayoutManager(layoutManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMusicPlayerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        mCustomListViewModel = new ViewModelProvider(requireActivity()).get(CustomListViewModel.class);
        mFragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mFragmentProfileBinding.setCustomListViewModel(mCustomListViewModel);
        mFragmentProfileBinding.setLifecycleOwner(getViewLifecycleOwner());

        initMusicList();

        return mFragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentProfileBinding.btnPlayAll.setOnClickListener(v -> {
            if (!Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).isEmpty()) {
                mCustomListViewModel.playNext();
            }
        });

        mCustomListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            Log.d(TAG, "Observe music list change.");
            mProfileListAdapter.notifyItemInserted(musicList.size());
        });

        mCustomListViewModel.getCurrentMusic().observe(getViewLifecycleOwner(), currentMusic -> {
            if (currentMusic != -1) {
                try {
                    mMusicPlayerViewModel.playMusic(Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).get(currentMusic), true);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                mMusicPlayerViewModel.stopMusic();
            }
        });
    }
}
