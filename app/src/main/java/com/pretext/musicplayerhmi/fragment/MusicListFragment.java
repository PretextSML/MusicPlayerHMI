package com.pretext.musicplayerhmi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.pretext.musicplayerhmi.viewmodels.MusicListViewModel;

public class MusicListFragment extends Fragment {

    MusicListViewModel musicListViewModel;
    FragmentMusicListBinding musicListBinding;

    private static final String TAG = "[MusicListFragment]";
    private static MusicListFragment musicListFragment;
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
        musicListViewModel = new ViewModelProvider(this).get(MusicListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        musicListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_list, container, false);
        musicListBinding.setLifecycleOwner(getViewLifecycleOwner());

        return musicListBinding.getRoot();
    }

    public void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        musicListBinding.musicList.setLayoutManager(layoutManager);
    }

    public void initObserveViewModel() {
        musicListViewModel.getMusicList().observe(getViewLifecycleOwner(), musicList -> {
            if (musicList != null && !musicList.isEmpty()) {
                MusicListAdapter adapter = new MusicListAdapter(musicList, context, handler);
                musicListBinding.musicList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView();
        initObserveViewModel();

        musicListViewModel.loadMusicFiles();
    }
}
