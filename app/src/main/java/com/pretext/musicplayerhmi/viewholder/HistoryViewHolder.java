package com.pretext.musicplayerhmi.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView history;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        history = itemView.findViewById(R.id.history_detail);
    }
}
