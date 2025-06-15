package com.pretext.musicplayerhmi.fragment;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pretext.musicplayerhmi.MusicInfo;
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.adapter.MusicListAdapter;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerservice.IMusicProgressCallback;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private static final String TAG = "[MusicListFragment]";
    private static MusicListFragment musicListFragment;
    private List<MusicInfo> musicInfoList;
    private TextView currentDurationText;
    private TextView currentMusic;
    private TextView totalDurationText;
    private SeekBar musicProgress;
    private View rootView;
    private Boolean isChanging = false;
    private Boolean mIsPlayed = false;
    private Boolean mIsPause = false;
    private ImageButton pauseAndResume;
    private final IMusicProgressCallback callback = new IMusicProgressCallback.Stub() {
        @Override
        public void onProgressChanged(long currentDuration) {
            if (!isChanging && musicProgress != null) {
                musicProgress.setProgress((int) currentDuration);
            }
        }

        @Override
        public void onPlayStatusChanged(boolean currentStatus) {
            if (pauseAndResume != null) {
                if (currentStatus) {
                    pauseAndResume.setBackgroundResource(R.drawable.pause);
                    mIsPlayed = true;
                } else {
                    pauseAndResume.setBackgroundResource(R.drawable.play);
                    mIsPlayed = false;
                }
            }
        }

        @Override
        public void onFinishPlaying() {
            Log.d(TAG, "onFinishPlaying");
            resetToDefault();
        }
    };
    private PopupWindow popupWindow;
    private ImageButton volume;
    private ImageButton stop;
    private Context context;
    private AudioManager audioManager;
    private TextView volumeText;
    private int maxVolume;
    private int currentVolume;
    private boolean isChangingVolume = false;


    public static MusicListFragment getInstance() {
        if (musicListFragment == null)
            musicListFragment = new MusicListFragment();
        return musicListFragment;
    }

    public IMusicProgressCallback getCallback() {
        return callback;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        musicInfoList = readMusicFile();
    }

    public void initPopupWindow(View v) {
        View view = LayoutInflater.from(context).inflate(R.layout.volume_popup, null, false);
        SeekBar volumeSetting = view.findViewById(R.id.volume_setting);
        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "initPopupWindow: max = " + maxVolume + ", now = " + currentVolume);
        volumeSetting.setMax(maxVolume);
        volumeSetting.setProgress(currentVolume);
        volumeSetting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isChangingVolume) {
                    if (progress * 100 / maxVolume >= 50) {
                        volume.setBackgroundResource(R.drawable.volume_loud);
                    } else if (progress * 100 / maxVolume > 0) {
                        volume.setBackgroundResource(R.drawable.volume_medium);
                    } else if (progress == 0) {
                        volume.setBackgroundResource(R.drawable.volume_mute);
                    }
                    volumeText.setText("");
                } else {
                    volumeText.setText(Integer.toString(seekBar.getProgress() * 100 / maxVolume));
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChangingVolume = true;
                volume.setBackgroundResource(R.drawable.no_volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isChangingVolume = false;
                if (seekBar.getProgress() * 100 / maxVolume >= 50) {
                    volume.setBackgroundResource(R.drawable.volume_loud);
                } else if (seekBar.getProgress() * 100 / maxVolume > 0) {
                    volume.setBackgroundResource(R.drawable.volume_medium);
                } else if (seekBar.getProgress() == 0) {
                    volume.setBackgroundResource(R.drawable.volume_mute);
                }
                volumeText.setText("");
            }
        });
        popupWindow = new PopupWindow(
                view,
                Math.round(getResources().getDisplayMetrics().density * 25),
                Math.round(getResources().getDisplayMetrics().density * 180),
                true
        );
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v1, event) -> false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(v, Gravity.CENTER, Gravity.CENTER, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_music_list, container, false);

        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume = rootView.findViewById(R.id.volume);
        volumeText = rootView.findViewById(R.id.volume_text);
        volume.setOnClickListener(v -> initPopupWindow(v));
        if (currentVolume * 100 / maxVolume >= 50) {
            volume.setBackgroundResource(R.drawable.volume_loud);
        } else if (currentVolume * 100 / maxVolume > 0) {
            volume.setBackgroundResource(R.drawable.volume_medium);
        } else if (currentVolume == 0) {
            volume.setBackgroundResource(R.drawable.volume_mute);
        }
        currentDurationText = rootView.findViewById(R.id.current_time);
        totalDurationText = rootView.findViewById(R.id.total_time);

        currentMusic = rootView.findViewById(R.id.current_music);

        musicProgress = rootView.findViewById(R.id.music_progress);
        musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentDurationText.setText(String.format("%s:%s", (progress / 1000 / 60), (progress / 1000 % 60) / 10 > 0 ? (progress / 1000) % 60 : "0" + (progress / 1000) % 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopTimer();
                    isChanging = true;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (mIsPlayed || mIsPause) {
                        MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().setDuration(seekBar.getProgress());
                    }
                    isChanging = false;
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().startTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pauseAndResume = rootView.findViewById(R.id.play_and_pause);
        pauseAndResume.setOnClickListener(v -> {
            if (mIsPlayed) {
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().pauseMusic();
                    mIsPause = true;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else if (mIsPause) {
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().resumeMusic();
                    mIsPause = false;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        stop = rootView.findViewById(R.id.stop_music);
        stop.setOnClickListener(v -> {
            try {
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopMusic();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        initMusicList();
        resetToDefault();

        return rootView;
    }

    public void initMusicList() {
        RecyclerView musicListView = rootView.findViewById(R.id.music_list);
        MusicListAdapter musicListAdapter = new MusicListAdapter(musicInfoList, context, rootView);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1);
        Log.d(TAG, "initMusicList: " + musicListAdapter);
        musicListView.setAdapter(musicListAdapter);
        musicListView.setLayoutManager(layoutManager);
    }

    public List<MusicInfo> readMusicFile() {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
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

    public void resetToDefault() {
        Log.d(TAG, "stopPlaying");
        mIsPlayed = false;
        mIsPause = false;
        musicProgress.setMax(0);
        musicProgress.setProgress(0);
        pauseAndResume.setBackgroundResource(R.drawable.play);
        totalDurationText.setText(R.string.default_duration);
        currentMusic.setText("Now playing: None");
    }
}
