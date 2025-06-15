package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pretext.musicplayerhmi.R;

public class HistoryFragment extends Fragment {
    private static HistoryFragment historyFragment;

    public static HistoryFragment getInstance() {
        if (historyFragment == null)
            historyFragment = new HistoryFragment();
        return historyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
}
