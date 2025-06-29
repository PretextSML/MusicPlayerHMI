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
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.activity.MainActivity;
import com.pretext.musicplayerhmi.util.MusicInfoUtil;
import com.pretext.musicplayerhmi.viewholder.MusicListViewHolder;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;

import java.io.IOException;
import java.util.Objects;

public class ProfileListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

    private final CustomListViewModel mCustomListViewModel;
    private final Context mContext;

    public ProfileListAdapter(Context mContext, CustomListViewModel mCustomListViewModel) {
        this.mContext = mContext;
        this.mCustomListViewModel = mCustomListViewModel;
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
        MusicInfoUtil info = Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).get(position);

        setDefaultAlbumCover(holder);

        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String author = split[0];

        holder.mMusicName.setText(name);
        holder.mMusicAuthor.setText(author);
        holder.mAddToMusicList.setVisibility(View.GONE);

        loadAlbumCoverAsync(holder, info);
    }

    private void setDefaultAlbumCover(MusicListViewHolder holder) {
        Glide.with(mContext)
                .load(R.drawable.album_default)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.mMusicAlbum);
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
                        currentPosition < Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).size() &&
                        currentPath.equals(mCustomListViewModel.getMusicList().getValue().get(currentPosition).getMusicPath())) {

                    if (finalData != null) {
                        Glide.with(mContext)
                                .load(finalData)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                                .into(holder.mMusicAlbum);
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
        return Objects.requireNonNull(mCustomListViewModel.getMusicList().getValue()).size();
    }
}