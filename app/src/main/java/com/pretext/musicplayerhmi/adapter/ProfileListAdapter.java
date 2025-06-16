package com.pretext.musicplayerhmi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pretext.musicplayerhmi.MusicInfo;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;

import java.io.IOException;
import java.util.List;

public class ProfileListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    private final TextView currentMusic;
    private final TextView totalDurationText;
    private final ImageButton pauseAndResume;
    private final SeekBar musicProgress;
    private final List<MusicInfo> musicInfoList;
    private final Context context;

    public ProfileListAdapter(List<MusicInfo> musicInfoList, Context context, Activity activity) {
        this.musicInfoList = musicInfoList;
        this.context = context;

        totalDurationText = activity.findViewById(R.id.total_time);
        currentMusic = activity.findViewById(R.id.current_music);
        musicProgress = activity.findViewById(R.id.music_progress);
        pauseAndResume = activity.findViewById(R.id.play_and_pause);
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

        byte[] data;
        try {
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(info.getMusicPath());
            data = metadataRetriever.getEmbeddedPicture();
            metadataRetriever.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (data != null) {
            Glide.with(context)
                    .load(data)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                    .into(holder.musicAlbum);
        } else {
            Glide.with(context)
                    .load(R.drawable.album_default)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                    .into(holder.musicAlbum);
        }

        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String author = split[0];

        holder.musicName.setText(name);
        holder.musicAuthor.setText(author);
        holder.addToMusicList.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }
}