package com.pretext.musicplayerhmi;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.databinding.ActivityMainBinding;
import com.pretext.musicplayerhmi.databinding.VolumePopupBinding;
import com.pretext.musicplayerhmi.fragment.HistoryFragment;
import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerhmi.fragment.ProfileFragment;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;
import com.pretext.musicplayerhmi.viewmodel.VolumeViewModel;
import com.pretext.musicplayerservice.IMusicProgressCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MainActivity]";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(32);

    private FragmentManager fragmentManager;

    private MusicPlayerViewModel musicPlayerViewModel;
    private CustomListViewModel customListViewModel;
    private VolumeViewModel volumeViewModel;

    private ActivityMainBinding activityMainBinding;

    private final IMusicProgressCallback callback = new IMusicProgressCallback.Stub() {
        @Override
        public void onProgressChanged(long currentDuration) {
            runOnUiThread(() -> {
                activityMainBinding.currentTime.setText(formatTime((int) currentDuration));
                activityMainBinding.musicProgress.setProgress((int) currentDuration);
            });
        }

        @Override
        public void onPlayStatusChanged(boolean currentStatus) {
            runOnUiThread(() -> {
                if (currentStatus)
                    activityMainBinding.playAndPause.setImageResource(R.drawable.pause);
                else
                    activityMainBinding.playAndPause.setImageResource(R.drawable.play);
                musicPlayerViewModel.getIsPlaying().setValue(currentStatus);
                musicPlayerViewModel.getIsPaused().setValue(!currentStatus);
            });
        }

        @Override
        public void onFinishPlaying() {
            runOnUiThread(() -> {
                if (Boolean.FALSE.equals(musicPlayerViewModel.getFromList().getValue())) {
                    activityMainBinding.totalTime.setText(formatTime(0));
                    musicPlayerViewModel.stopMusic();
                } else {
                    customListViewModel.playNext();
                }
            });
        }
    };

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    private String formatTime(int time) {
        int seconds = time / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void resetToDefault() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "stopPlaying");
            activityMainBinding.musicProgress.setMax(0);
            activityMainBinding.musicProgress.setProgress(0);
            activityMainBinding.playAndPause.setImageResource(R.drawable.play);
            activityMainBinding.totalTime.setText(R.string.default_duration);
        });
    }

    public void initPlayerComponent() {
        musicPlayerViewModel.getMaxProgress().observe(this, maxProgress -> {
            activityMainBinding.totalTime.setText(formatTime(maxProgress));
            Log.d(TAG, "Observe max prorgess here.");
        });
        musicPlayerViewModel.getCurrentMusic().observe(this, currentMusic -> {
            Log.d(TAG, "Observe current music here.");
            if (!((MusicPlayerApplication) getApplication()).getCurrentUser().equals("GUEST"))
                HistoryFragment.getInstance().addHistoryList(currentMusic.getMusicName());
        });
    }

    public void initPopupWindow() {
        volumeViewModel = new ViewModelProvider(this).get(VolumeViewModel.class);
        VolumePopupBinding volumePopupBinding = VolumePopupBinding.inflate(LayoutInflater.from(this), null, false);
        volumePopupBinding.setVolumeViewModel(volumeViewModel);
        volumePopupBinding.setLifecycleOwner(this);
        volumePopupBinding.volumeSetting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeViewModel.setVolumeLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        PopupWindow popupWindow = new PopupWindow(
                volumePopupBinding.getRoot(),
                Math.round(getResources().getDisplayMetrics().density * 25),
                Math.round(getResources().getDisplayMetrics().density * 150),
                true
        );
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(activityMainBinding.volume, Gravity.CENTER, Gravity.CENTER, 0);
    }

    public void initButton() {
        activityMainBinding.playAndPause.setOnClickListener(v -> musicPlayerViewModel.switchPlayAndPause());
        activityMainBinding.stopMusic.setOnClickListener(v -> {
            musicPlayerViewModel.stopMusic();
            customListViewModel.reset();
        });
        activityMainBinding.skipNext.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(musicPlayerViewModel.getFromList().getValue()))
                customListViewModel.playNext();
        });
        activityMainBinding.skipPrevious.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(musicPlayerViewModel.getFromList().getValue()))
                customListViewModel.playPrevious();
        });
    }

    public void initVolume() {
        activityMainBinding.volume.setOnClickListener(v -> initPopupWindow());
    }

    public void initSeekBar() {
        activityMainBinding.musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    activityMainBinding.currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayerViewModel.seekTo(seekBar.getProgress());
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().startTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void initMenu() {
        TabLayout menu = findViewById(R.id.tab_layout);
        menu.addTab(menu.newTab().setText("Music list"));
        menu.addTab(menu.newTab().setText("All music"));
        menu.addTab(menu.newTab().setText("Local history"));
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
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, ProfileFragment.getInstance(), "Profile").show(ProfileFragment.getInstance());
        fragmentTransaction.add(R.id.frame_layout, MusicListFragment.getInstance(), "Music List").hide(MusicListFragment.getInstance());
        fragmentTransaction.add(R.id.frame_layout, HistoryFragment.getInstance(), "History").hide(HistoryFragment.getInstance());
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String user = ((MusicPlayerApplication) getApplication()).getCurrentUser();
        Log.d(TAG, "current user: " + user);
        super.onCreate(savedInstanceState);

        musicPlayerViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
        customListViewModel = new ViewModelProvider(this).get(CustomListViewModel.class);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setMusicPlayerViewModel(musicPlayerViewModel);
        activityMainBinding.setLifecycleOwner(this);

        initSeekBar();
        initButton();
        initVolume();
        initMenu();
        initFragment();
        initPlayerComponent();
        resetToDefault();
        MusicPlayerServiceConnection.getInstance().setMusicProgressCallback(callback);
        MusicPlayerServiceConnection.getInstance().activateService();
    }

    @Override
    protected void onDestroy() {
        try {
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopTimer();
            MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().stopMusic();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }
}