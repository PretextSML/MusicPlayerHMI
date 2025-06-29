package com.pretext.musicplayerhmi.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.viewholder.HistoryViewHolder;
import com.pretext.musicplayerhmi.viewmodel.HistoryViewModel;

import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private final HistoryViewModel mHistoryViewModel;

    public HistoryAdapter(HistoryViewModel mHistoryViewModel) {
        this.mHistoryViewModel = mHistoryViewModel;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.detail_history, null);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.mHistory.setText(Objects.requireNonNull(mHistoryViewModel.getHistoryList().getValue()).getHistoryList().get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(mHistoryViewModel.getHistoryList().getValue()).getHistoryList().size();
    }
}
