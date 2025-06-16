package com.pretext.musicplayerhmi.viewholder;

import android.view.View;
import android.widget.ImageButton;
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
    public ImageButton addToMusicList;
    public ConstraintLayout rootView;

    public MusicListViewHolder(@NonNull View itemView) {
        super(itemView);
        musicName = itemView.findViewById(R.id.music_name);
        musicAuthor = itemView.findViewById(R.id.music_artist);
        musicAlbum = itemView.findViewById(R.id.music_album);
        addToMusicList = itemView.findViewById(R.id.add_to_music_list);
        rootView = itemView.findViewById(R.id.root_view);
    }
}
