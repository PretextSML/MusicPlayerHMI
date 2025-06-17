package com.pretext.musicplayerhmi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.fragment.HistoryFragment;
import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerhmi.fragment.ProfileFragment;
import com.pretext.musicplayerservice.IMusicProgressCallback;

import java.util.Locale;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MainActivity]";
    private static Context context;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private Boolean isChanging = false;
    private Boolean mIsPlayed = false;
    private SeekBar musicProgress;
    private ImageButton pauseAndResume;
    private Boolean mIsPause = false;
    private TextView currentMusic;
    private TextView totalDurationText;
    private boolean fromList = false;
    private boolean stopMusic = false;
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
                    pauseAndResume.setImageResource(R.drawable.pause);
                    mIsPlayed = true;
                } else {
                    pauseAndResume.setImageResource(R.drawable.play);
                    mIsPlayed = false;
                }
            }
        }

        @Override
        public void onFinishPlaying() {
            Log.d(TAG, "onFinishPlaying");
            if (fromList) {
                if (!stopMusic) {
                    if (!ProfileFragment.getInstance().playNext()) {
                        resetToDefault();
                    }
                } else {
                    resetToDefault();
                }
            } else {
                resetToDefault();
            }
        }
    };
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.d(TAG, "handleMessage: get message from profile fragment");
                MusicInfo musicInfo = msg.getData().getSerializable("playMusic", MusicInfo.class);
                boolean isFromList = msg.getData().getBoolean("fromList");
                if (musicInfo != null) {
                    playMusic(musicInfo, isFromList);
                }
            }
        }
    };
    private ImageButton nextMusic;
    private ImageButton previousMusic;
    private PopupWindow popupWindow;
    private ImageButton volume;
    private ImageButton stop;
    private AudioManager audioManager;
    private TextView volumeText;
    private int maxVolume;
    private int currentVolume;
    private boolean isChangingVolume = false;
    private TextView currentDurationText;

    public static Context getContext() {
        return context;
    }

    public void resetToDefault() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "stopPlaying");
            mIsPlayed = false;
            mIsPause = false;
            musicProgress.setMax(0);
            musicProgress.setProgress(0);
            pauseAndResume.setImageResource(R.drawable.play);
            totalDurationText.setText(R.string.default_duration);
            currentMusic.setText(String.format(getString(R.string.now_playing), "None"));
            ProfileFragment.getInstance().reset(true);
        });
    }

    public void playMusic(MusicInfo info, boolean isFromList) {
        stopMusic = false;
        Log.d(TAG, "playMusic: " + info.getMusicName());
        String[] split = info.getMusicName().split(" - ");
        String name = split[1].substring(0, split[1].length() - 4);
        String path = info.getMusicPath();
        long duration = info.getMusicDuration();
        fromList = isFromList;

        currentMusic.setText(String.format(getString(R.string.now_playing), name));
        musicProgress.setMax((int) duration);
        totalDurationText.setText(String.format("%s:%s", (duration / 1000 / 60), (duration / 1000 % 60) / 10 > 0 ? (duration / 1000) % 60 : "0" + (duration / 1000) % 60));
        pauseAndResume.setImageResource(R.drawable.pause);

        try {
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().playMusic(path);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
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
                        volume.setImageResource(R.drawable.volume_loud);
                    } else if (progress * 100 / maxVolume > 0) {
                        volume.setImageResource(R.drawable.volume_medium);
                    } else if (progress == 0) {
                        volume.setImageResource(R.drawable.volume_mute);
                    }
                    volumeText.setText("");
                } else {
                    volumeText.setText(String.format(Locale.ENGLISH, "%d", seekBar.getProgress() * 100 / maxVolume));
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChangingVolume = true;
                volume.setImageResource(R.drawable.no_volume);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isChangingVolume = false;
                if (seekBar.getProgress() * 100 / maxVolume >= 50) {
                    volume.setImageResource(R.drawable.volume_loud);
                } else if (seekBar.getProgress() * 100 / maxVolume > 0) {
                    volume.setImageResource(R.drawable.volume_medium);
                } else if (seekBar.getProgress() == 0) {
                    volume.setImageResource(R.drawable.volume_mute);
                }
                volumeText.setText("");
            }
        });
        popupWindow = new PopupWindow(
                view,
                Math.round(getResources().getDisplayMetrics().density * 25),
                Math.round(getResources().getDisplayMetrics().density * 150),
                true
        );
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v1, event) -> false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(v, Gravity.CENTER, Gravity.CENTER, 0);
    }

    public void initButton() {
        pauseAndResume = findViewById(R.id.play_and_pause);
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

        stop = findViewById(R.id.stop_music);
        stop.setOnClickListener(v -> {
            try {
                stopMusic = true;
                MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopMusic();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        nextMusic = findViewById(R.id.skip_next);
        nextMusic.setOnClickListener(v -> ProfileFragment.getInstance().playNextMusic());
        previousMusic = findViewById(R.id.skip_previous);
        previousMusic.setOnClickListener(v -> ProfileFragment.getInstance().playPreviousMusic());
    }

    public void initVolume() {
        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume = findViewById(R.id.volume);
        volume.setOnClickListener(this::initPopupWindow);
        volumeText = findViewById(R.id.volume_text);

        if (currentVolume * 100 / maxVolume >= 50) {
            volume.setImageResource(R.drawable.volume_loud);
        } else if (currentVolume * 100 / maxVolume > 0) {
            volume.setImageResource(R.drawable.volume_medium);
        } else if (currentVolume == 0) {
            volume.setImageResource(R.drawable.volume_mute);
        }
    }

    public void initSeekBar() {
        Log.d(TAG, "initSeekBar: " + currentMusic);

        currentDurationText = findViewById(R.id.current_time);
        totalDurationText = findViewById(R.id.total_time);
        currentMusic = findViewById(R.id.current_music);
        musicProgress = findViewById(R.id.music_progress);
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
    }

    public void initMenu() {
        TabLayout menu = findViewById(R.id.tab_layout);
        menu.addTab(menu.newTab().setText("Profile"));
        menu.addTab(menu.newTab().setText("Music List"));
        menu.addTab(menu.newTab().setText("Local History"));
        menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragmentManager.beginTransaction().
                                show(ProfileFragment.getInstance()).
                                hide(MusicListFragment.getInstance()).
                                hide(HistoryFragment.getInstance()).
                                commit();
                        break;
                    case 1:
                        fragmentManager.beginTransaction().
                                hide(ProfileFragment.getInstance()).
                                show(MusicListFragment.getInstance()).
                                hide(HistoryFragment.getInstance()).
                                commit();
                        break;
                    case 2:
                        fragmentManager.beginTransaction().
                                hide(ProfileFragment.getInstance()).
                                hide(MusicListFragment.getInstance()).
                                show(HistoryFragment.getInstance()).
                                commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void initFragment() {
        ProfileFragment.getInstance().setHandler(handler);
        MusicListFragment.getInstance().setHandler(handler);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, ProfileFragment.getInstance(), "Profile").show(ProfileFragment.getInstance());
        fragmentTransaction.add(R.id.frame_layout, MusicListFragment.getInstance(), "Music List").hide(MusicListFragment.getInstance());
        fragmentTransaction.add(R.id.frame_layout, HistoryFragment.getInstance(), "History").hide(HistoryFragment.getInstance());
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSeekBar();
        initButton();
        initVolume();
        initMenu();
        initFragment();
        resetToDefault();
        MusicPlayerServiceConnection.getInstance().setMusicProgressCallback(callback);
        MusicPlayerServiceConnection.getInstance().bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopMusic();
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopTimer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        getApplicationContext().unbindService(MusicPlayerServiceConnection.getInstance());
        MusicPlayerServiceConnection.getInstance().setMusicPlayerServiceConnection(null);
    }
}