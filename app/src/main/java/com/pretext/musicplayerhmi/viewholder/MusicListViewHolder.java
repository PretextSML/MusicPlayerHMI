package com.pretext.musicplayerhmi.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.R;

public class MusicListViewHolder extends RecyclerView.ViewHolder {
    public TextView musicName;
    public TextView musicAuthor;
    public ImageView musicAlbum;
    public ConstraintLayout rootView;

    public MusicListViewHolder(@NonNull View itemView) {
        super(itemView);
        musicName = itemView.findViewById(R.id.music_name);
        musicAuthor = itemView.findViewById(R.id.music_artist);
        musicAlbum = itemView.findViewById(R.id.music_album);
        rootView = itemView.findViewById(R.id.root_view);
    }
}
