package com.pretext.musicplayerhmi.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.LoginActivity;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.HistoryAdapter;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.HistoryUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    public static final String TAG = "[HistoryFragment]";
    private static HistoryFragment historyFragment;
    HistoryAdapter historyAdapter;
    private HistoryUtil userHistory = new HistoryUtil();
    private List<String> historyList = new ArrayList<>();
    private View rootView;

    public static HistoryFragment getInstance() {
        if (historyFragment == null)
            historyFragment = new HistoryFragment();
        return historyFragment;
    }

    public void addHistoryList(String name) {
        String addedName = userHistory.addMusic(name);
        if (addedName != null) {
            historyList.add(addedName);
            historyAdapter.notifyItemInserted(historyList.size());
            try {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().setHistory(LoginActivity.currentUser, userHistory.toJson());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initHistoryList() {
        TextView title = rootView.findViewById(R.id.history_title);
        title.setText(String.format(getResources().getString(R.string.history_title), LoginActivity.currentUser));
        try {
            String history = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().getHistory(LoginActivity.currentUser);
            if (history != null) {
                if (userHistory.fromGson(history) != null) {
                    userHistory = userHistory.fromGson(history);
                    Log.d(TAG, "initHistoryList: " + history);
                    historyList = userHistory.getHistoryList();
                    for (String name : historyList) {
                        Log.d(TAG, "History: " + name);
                    }
                }
            } else {
                historyList = new ArrayList<>();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        RecyclerView historyListView = rootView.findViewById(R.id.history_list);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        historyAdapter = new HistoryAdapter(historyList);
        historyListView.setAdapter(historyAdapter);
        historyListView.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (!LoginActivity.currentUser.equals("GUEST"))
            initHistoryList();
        else {
            rootView.findViewById(R.id.user).setVisibility(View.GONE);
            rootView.findViewById(R.id.guest).setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    public HistoryUtil getUserHistory() {
        return userHistory;
    }

    public void setUserHistory(HistoryUtil userHistory) {
        this.userHistory = userHistory;
    }
}
