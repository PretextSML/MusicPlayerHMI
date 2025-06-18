package com.pretext.musicplayerhmi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pretext.musicplayerhmi.MainActivity;
import com.pretext.musicplayerhmi.MusicInfo;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;

import java.io.IOException;
import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    private final List<MusicInfo> musicInfoList;
    private final Context context;

    public ProfileListAdapter(List<MusicInfo> musicInfoList, Context context) {
        this.musicInfoList = musicInfoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.music_detail, null);
        return new MusicListViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder holder, int position) {
        MusicInfo info = musicInfoList.get(position);

        setDefaultAlbumCover(holder);

        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String author = split[0];

        holder.musicName.setText(name);
        holder.musicAuthor.setText(author);
        holder.addToMusicList.setVisibility(View.GONE);
        loadAlbumCoverAsync(holder, info);
    }

    private void setDefaultAlbumCover(MusicListViewHolder holder) {
        Glide.with(context)
                .load(R.drawable.album_default)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.musicAlbum);
    }

    private void loadAlbumCoverAsync(MusicListViewHolder holder, MusicInfo info) {
        final String currentPath = info.getMusicPath();

        MainActivity.getExecutorService().execute(() -> {
            byte[] data;
            try {
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(currentPath);
                data = metadataRetriever.getEmbeddedPicture();
                metadataRetriever.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            final int currentPosition = holder.getAdapterPosition();
            final byte[] finalData = data;

            holder.itemView.post(() -> {
                if (currentPosition != RecyclerView.NO_POSITION &&
                        currentPosition < musicInfoList.size() &&
                        currentPath.equals(musicInfoList.get(currentPosition).getMusicPath())) {

                    if (finalData != null) {
                        Glide.with(context)
                                .load(finalData)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                .into(holder.musicAlbum);
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }
}