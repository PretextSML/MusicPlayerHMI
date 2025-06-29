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
import com.pretext.musicplayerhmi.adapter.HistoryAdapter;
import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.databinding.FragmentHistoryBinding;
import com.pretext.musicplayerhmi.viewmodel.HistoryViewModel;

public class HistoryFragment extends Fragment {
    public static final String TAG = "[HistoryFragment]";

    private HistoryAdapter historyAdapter;
    private HistoryViewModel historyViewModel;
    private FragmentHistoryBinding fragmentHistoryBinding;

    public void initHistoryList() {
        fragmentHistoryBinding.historyTitle.setText(String.format(getResources().getString(R.string.history_title), ((MusicPlayerApplication) requireActivity().getApplication()).getCurrentUser()));
        historyAdapter = new HistoryAdapter(historyViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        fragmentHistoryBinding.historyList.setAdapter(historyAdapter);
        fragmentHistoryBinding.historyList.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        historyViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        fragmentHistoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        fragmentHistoryBinding.setHistoryViewModel(historyViewModel);
        fragmentHistoryBinding.setLifecycleOwner(getViewLifecycleOwner());

        if (!((MusicPlayerApplication) requireActivity().getApplication()).getCurrentUser().equals("GUEST")) {
            Log.d(TAG, "Get user history");
            historyViewModel.getHistoryListFromDB();

            historyViewModel.getHistoryList().observe(getViewLifecycleOwner(), historyList -> {
                Log.d(TAG, "Observce history change");
                historyAdapter.notifyItemInserted(historyList.getHistoryList().size());
            });
            initHistoryList();
        } else {
            fragmentHistoryBinding.getRoot().findViewById(R.id.user).setVisibility(View.GONE);
            fragmentHistoryBinding.getRoot().findViewById(R.id.guest).setVisibility(View.VISIBLE);
        }

        return fragmentHistoryBinding.getRoot();
    }
}
