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
import com.pretext.musicplayerhmi.adapter.CustomListAdapter;
import com.pretext.musicplayerhmi.databinding.FragmentCustomListBinding;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;

import java.util.Objects;

public class CustomListFragment extends Fragment {
    private static final String TAG = "[Profile]";

    private CustomListAdapter mCustomListAdapter;
    private MusicPlayerViewModel mMusicPlayerViewModel;
    private CustomListViewModel mCustomListViewModel;
    private FragmentCustomListBinding mFragmentCustomListBinding;

    public void initMusicList() {
        mCustomListAdapter = new CustomListAdapter(getContext(), mCustomListViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        Log.d(TAG, "initMusicList: " + mCustomListAdapter);
        mFragmentCustomListBinding.musicList.setAdapter(mCustomListAdapter);
        mFragmentCustomListBinding.musicList.setLayoutManager(layoutManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMusicPlayerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        mCustomListViewModel = new ViewModelProvider(requireActivity()).get(CustomListViewModel.class);
        mFragmentCustomListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_list, container, false);
        mFragmentCustomListBinding.setCustomListViewModel(mCustomListViewModel);
        mFragmentCustomListBinding.setLifecycleOwner(getViewLifecycleOwner());

        initMusicList();

        return mFragmentCustomListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFragmentCustomListBinding.btnPlayAll.setOnClickListener(v -> {
            if (!Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).isEmpty()) {
                mCustomListViewModel.playNext();
            }
        });

        mCustomListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            Log.d(TAG, "Observe music list change.");
            mCustomListAdapter.notifyItemInserted(musicList.size());
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
