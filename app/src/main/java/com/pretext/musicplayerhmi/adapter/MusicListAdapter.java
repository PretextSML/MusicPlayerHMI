package com.pretext.musicplayerhmi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pretext.musicplayerhmi.MusicInfo;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;

import java.io.IOException;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    private final TextView currentMusic;
    private final TextView totalDurationText;
    private final ImageButton pauseAndResume;
    private final SeekBar musicProgress;
    private final List<MusicInfo> musicInfoBeanList;
    private final Context context;

    public MusicListAdapter(List<MusicInfo> musicInfoList, Context context, View rootView) {
        this.musicInfoBeanList = musicInfoList;
        this.context = context;

        totalDurationText = rootView.findViewById(R.id.total_time);
        currentMusic = rootView.findViewById(R.id.current_music);
        musicProgress = rootView.findViewById(R.id.music_progress);
        pauseAndResume = rootView.findViewById(R.id.play_and_pause);
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
        MusicInfo info = musicInfoBeanList.get(position);
        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String author = split[0];
        String path = info.getMusicPath();

        long duration = info.getMusicDuration();
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

        holder.musicName.setText(name);
        holder.musicAuthor.setText(author);
        holder.rootView.setOnClickListener(v -> {
            if (!MusicPlayerServiceConnection.getInstance().getIsConnected()) {
                Toast.makeText(v.getContext(), "Service not connected!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                currentMusic.setText("Now playing: " + split[1]);
                musicProgress.setMax((int) duration);
                totalDurationText.setText(String.format("%s:%s", (duration / 1000 / 60), (duration / 1000 % 60) / 10 > 0 ? (duration / 1000) % 60 : "0" + (duration / 1000) % 60));
                pauseAndResume.setBackgroundResource(R.drawable.pause);

                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().playMusic(path);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicInfoBeanList.size();
    }
}