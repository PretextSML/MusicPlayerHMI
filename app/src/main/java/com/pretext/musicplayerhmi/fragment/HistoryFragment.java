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

    private HistoryAdapter mHistoryAdapter;
    private HistoryViewModel mHistoryViewModel;
    private FragmentHistoryBinding mFragmentHistoryBinding;

    public void initHistoryList() {
        mFragmentHistoryBinding.historyTitle.setText(String.format(getResources().getString(R.string.history_title), ((MusicPlayerApplication) requireActivity().getApplication()).getCurrentUser()));
        mHistoryAdapter = new HistoryAdapter(mHistoryViewModel);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        mFragmentHistoryBinding.historyList.setAdapter(mHistoryAdapter);
        mFragmentHistoryBinding.historyList.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHistoryViewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);
        mFragmentHistoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        mFragmentHistoryBinding.setHistoryViewModel(mHistoryViewModel);
        mFragmentHistoryBinding.setLifecycleOwner(getViewLifecycleOwner());

        if (!((MusicPlayerApplication) requireActivity().getApplication()).getCurrentUser().equals("GUEST")) {
            Log.d(TAG, "Get user history");
            mHistoryViewModel.getHistoryListFromDB();

            mHistoryViewModel.getHistoryList().observe(getViewLifecycleOwner(), historyList -> {
                Log.d(TAG, "Observe history change");
                mHistoryAdapter.notifyItemInserted(historyList.getHistoryList().size());
            });
            initHistoryList();
        } else {
            mFragmentHistoryBinding.getRoot().findViewById(R.id.user).setVisibility(View.GONE);
            mFragmentHistoryBinding.getRoot().findViewById(R.id.guest).setVisibility(View.VISIBLE);
        }

        return mFragmentHistoryBinding.getRoot();
    }
}
