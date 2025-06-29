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
    public TextView mMusicName;
    public TextView mMusicAuthor;
    public ImageView mMusicAlbum;
    public ImageButton mAddToMusicList;
    public ConstraintLayout mRootView;

    public MusicListViewHolder(@NonNull View itemView) {
        super(itemView);
        mMusicName = itemView.findViewById(R.id.music_name);
        mMusicAuthor = itemView.findViewById(R.id.music_artist);
        mMusicAlbum = itemView.findViewById(R.id.music_album);
        mAddToMusicList = itemView.findViewById(R.id.add_to_music_list);
        mRootView = itemView.findViewById(R.id.root_view);
    }
}
