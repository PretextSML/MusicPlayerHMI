package com.pretext.musicplayerhmi;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.pretext.musicplayerservice.IMusicPlayerInterface;
import com.pretext.musicplayerservice.IMusicProgressCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static List<MusicInfo> musicInfoList;
    public static IMusicPlayerInterface iMusicPlayerInterface;
    private Boolean mIsConnected = false;
    private Boolean mIsPlayed = false;
    private Boolean mIsPause = false;
    private Boolean isChanging = false;
    private SeekBar musicProgress;

    private ImageButton pauseAndResume;

    private TextView currentDurationText;
    private TextView currentMusic;
    private TextView totalDurationText;
    private final IMusicProgressCallback callback = new IMusicProgressCallback.Stub() {
        @Override
        public void onProgressChanged(long currentDuration) {
            if (!isChanging) {
                musicProgress.setProgress((int) currentDuration);
            }
        }

        @Override
        public void onPlayStatusChanged(boolean currentStatus) {
            if (currentStatus) {
                pauseAndResume.setBackgroundResource(R.drawable.pause);
                mIsPlayed = true;
            } else {
                pauseAndResume.setBackgroundResource(R.drawable.play);
                mIsPlayed = false;
            }
        }

        @Override
        public void onFinishPlaying() {
            Log.d(TAG, "onFinishPlaying");
            resetToDefault();
        }
    };
    private TabLayout menu;
    private MusicPlayerServiceConnection musicPlayerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.tab_layout);
        menu.addTab(menu.newTab().setText("Music List"));
        menu.addTab(menu.newTab().setText("Local History"));

        currentDurationText = findViewById(R.id.current_time);
        currentMusic = findViewById(R.id.current_music);
        totalDurationText = findViewById(R.id.total_time);
        musicProgress = findViewById(R.id.music_progress);
        musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentDurationText.setText(String.format("%s:%s", (progress / 1000 / 60), (progress / 1000 % 60) / 10 > 0 ? (progress / 1000) % 60 : "0" + (progress / 1000) % 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    iMusicPlayerInterface.stopTimer();
                    isChanging = true;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (mIsPlayed || mIsPause) {
                        iMusicPlayerInterface.setDuration(seekBar.getProgress());
                    }
                    isChanging = false;
                    iMusicPlayerInterface.startTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pauseAndResume = findViewById(R.id.play_and_pause);
        pauseAndResume.setOnClickListener(v -> {
            if (mIsPlayed) {
                try {
                    iMusicPlayerInterface.pauseMusic();
                    mIsPause = true;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else if (mIsPause) {
                try {
                    iMusicPlayerInterface.resumeMusic();
                    mIsPause = false;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ImageButton stop = findViewById(R.id.stop_music);
        stop.setOnClickListener(v -> {
            try {
                iMusicPlayerInterface.stopMusic();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        bindService();
    }

    public void resetToDefault() {
        Log.d(TAG, "stopPlaying: ");
        mIsPlayed = false;
        mIsPause = false;
        musicProgress.setMax(0);
        musicProgress.setProgress(0);
        pauseAndResume.setBackgroundResource(R.drawable.play);
        totalDurationText.setText(R.string.default_duration);
        currentMusic.setText("Now playing: None");
    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();
        musicPlayerServiceConnection = new MusicPlayerServiceConnection();

        musicPlayerIntent.setPackage("com.pretext.musicplayerservice");
        musicPlayerIntent.setAction("com.pretext.musicplayerservice.MusicPlayerService");

        mIsConnected = bindService(musicPlayerIntent, musicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            iMusicPlayerInterface.stopMusic();
            iMusicPlayerInterface.stopTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        unbindService(musicPlayerServiceConnection);
        musicPlayerServiceConnection = null;
    }

    public List<MusicInfo> readMusicFile() {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                musicInfoList.add(
                        new MusicInfo(
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        )
                );
            }
            cursor.close();
        } else {
            Log.d(TAG, "cursor is null!");
        }

        return musicInfoList;
    }

    static class MusicListViewHolder extends RecyclerView.ViewHolder {
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

    class MusicPlayerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMusicPlayerInterface = IMusicPlayerInterface.Stub.asInterface(service);
            try {
                musicInfoList = readMusicFile();
                iMusicPlayerInterface.registerCallback(callback);
                iMusicPlayerInterface.startTimer();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            resetToDefault();

            RecyclerView musicListView = findViewById(R.id.music_list);
            MusicListAdapter musicListAdapter = new MusicListAdapter();
            GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);
            musicListView.setAdapter(musicListAdapter);
            musicListView.setLayoutManager(layoutManager);

            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMusicPlayerInterface = null;
            mIsConnected = false;
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    class MusicListAdapter extends RecyclerView.Adapter<MusicListViewHolder> {

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
                Glide.with(getApplicationContext())
                        .load(data)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(holder.musicAlbum);
            } else {
                Glide.with(getApplicationContext())
                        .load(R.drawable.album_default)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                        .into(holder.musicAlbum);
            }
            holder.musicName.setText(name);
            holder.musicAuthor.setText(author);
            holder.rootView.setOnClickListener(v -> {
                if (!mIsConnected) {
                    Toast.makeText(v.getContext(), "Service not connected!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    currentMusic.setText("Now playing: " + split[1]);
                    musicProgress.setMax((int) duration);
                    totalDurationText.setText(String.format("%s:%s", (duration / 1000 / 60), (duration / 1000 % 60) / 10 > 0 ? (duration / 1000) % 60 : "0" + (duration / 1000) % 60));
                    iMusicPlayerInterface.playMusic(path);
                    pauseAndResume.setBackgroundResource(R.drawable.pause);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public int getItemCount() {
            return musicInfoList.size();
        }
    }
}