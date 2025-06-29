package com.pretext.musicplayerhmi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.activity.MainActivity;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.MusicInfoUtil;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    private final MusicPlayerViewModel musicPlayerViewModel;
    private final CustomListViewModel customListViewModel;
    private final List<MusicInfoUtil> musicInfoUtilList;
    private final Context context;

    public MusicListAdapter(List<MusicInfoUtil> musicInfoUtilList, Context context, MusicPlayerViewModel musicPlayerViewModel, CustomListViewModel customListViewModel) {
        this.musicInfoUtilList = musicInfoUtilList;
        this.context = context;
        this.musicPlayerViewModel = musicPlayerViewModel;
        this.customListViewModel = customListViewModel;
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
        MusicInfoUtil info = musicInfoUtilList.get(position);

        setDefaultAlbumCover(holder);

        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String author = split[0];
        holder.musicName.setText(name);
        holder.musicAuthor.setText(author);

        loadAlbumCoverAsync(holder, info);

        holder.addToMusicList.setOnClickListener(v -> {
            if (!Objects.requireNonNull(customListViewModel.getMusicList().getValue()).contains(info)) {
                customListViewModel.addToMusicList(info);
                holder.addToMusicList.setImageResource(R.drawable.playlist_add_check);
            } else {
                Toast.makeText(v.getContext(), "Music already in music list", Toast.LENGTH_SHORT).show();
            }
        });

        holder.rootView.setOnClickListener(v -> {
            if (!MusicPlayerServiceConnection.getInstance().getIsConnected()) {
                Toast.makeText(v.getContext(), "Service not connected!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                musicPlayerViewModel.playMusic(info, false);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setDefaultAlbumCover(MusicListViewHolder holder) {
        Glide.with(context)
                .load(R.drawable.album_default)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.musicAlbum);
    }

    private void loadAlbumCoverAsync(MusicListViewHolder holder, MusicInfoUtil info) {
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
                        currentPosition < musicInfoUtilList.size() &&
                        currentPath.equals(musicInfoUtilList.get(currentPosition).getMusicPath())) {

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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return musicInfoUtilList.size();
    }
}