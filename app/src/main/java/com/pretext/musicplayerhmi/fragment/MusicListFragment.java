package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
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
import com.pretext.musicplayerhmi.adapter.MusicListAdapter;
import com.pretext.musicplayerhmi.databinding.FragmentMusicListBinding;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;

public class MusicListFragment extends Fragment {

    private static final String TAG = "[MusicListFragment]";

    private MusicListViewModel mMusicListViewModel;
    private MusicPlayerViewModel mMusicPlayerViewModel;
    private CustomListViewModel mCustomListViewModel;
    private FragmentMusicListBinding mMusicListBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Create music list fragment");

        mMusicListViewModel = new ViewModelProvider(requireActivity()).get(MusicListViewModel.class);
        mMusicPlayerViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerViewModel.class);
        mCustomListViewModel = new ViewModelProvider(requireActivity()).get(CustomListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMusicListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_list, container, false);
        mMusicListBinding.setMusicListViewModel(mMusicListViewModel);
        mMusicListBinding.setLifecycleOwner(getViewLifecycleOwner());

        return mMusicListBinding.getRoot();
    }

    public void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        mMusicListBinding.musicList.setLayoutManager(layoutManager);
    }

    public void initObserveViewModel() {
        mMusicListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            if (musicList != null && !musicList.isEmpty()) {
                MusicListAdapter adapter = new MusicListAdapter(musicList, getContext(), mMusicPlayerViewModel, mCustomListViewModel);
                mMusicListBinding.musicList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        initObserveViewModel();

        mMusicListViewModel.loadMusicFiles();
    }
}
