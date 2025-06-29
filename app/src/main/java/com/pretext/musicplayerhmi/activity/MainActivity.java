package com.pretext.musicplayerhmi.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.pretext.musicplayerhmi.R;
import com.pretext.musicplayerhmi.application.MusicPlayerApplication;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.databinding.ActivityMainBinding;
import com.pretext.musicplayerhmi.databinding.VolumePopupBinding;
import com.pretext.musicplayerhmi.fragment.CustomListFragment;
import com.pretext.musicplayerhmi.fragment.HistoryFragment;
import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerhmi.viewmodel.CustomListViewModel;
import com.pretext.musicplayerhmi.viewmodel.HistoryViewModel;
import com.pretext.musicplayerhmi.viewmodel.MusicPlayerViewModel;
import com.pretext.musicplayerhmi.viewmodel.VolumeViewModel;
import com.pretext.musicplayerservice.IMusicProgressCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[MainActivity]";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(32);

    private FragmentManager mFragmentManager;
    private CustomListFragment mCustomListFragment;
    private MusicListFragment mMusicListFragment;
    private HistoryFragment mHistoryFragment;

    private MusicPlayerViewModel mMusicPlayerViewModel;
    private CustomListViewModel mCustomListViewModel;
    private VolumeViewModel mVolumeViewModel;
    private HistoryViewModel mHistoryViewModel;

    private ActivityMainBinding mActivityMainBinding;

    private final IMusicProgressCallback iCallback = new IMusicProgressCallback.Stub() {
        @Override
        public void onProgressChanged(long currentDuration) {
            runOnUiThread(() -> {
                mActivityMainBinding.currentTime.setText(formatTime((int) currentDuration));
                mActivityMainBinding.musicProgress.setProgress((int) currentDuration);
            });
        }

        @Override
        public void onPlayStatusChanged(boolean currentStatus) {
            runOnUiThread(() -> {
                if (currentStatus)
                    mActivityMainBinding.playAndPause.setImageResource(R.drawable.pause);
                else
                    mActivityMainBinding.playAndPause.setImageResource(R.drawable.play);
                mMusicPlayerViewModel.getIsPlaying().setValue(currentStatus);
                mMusicPlayerViewModel.getIsPaused().setValue(!currentStatus);
            });
        }

        @Override
        public void onFinishPlaying() {
            runOnUiThread(() -> {
                if (Boolean.FALSE.equals(mMusicPlayerViewModel.getFromList().getValue())) {
                    mActivityMainBinding.totalTime.setText(formatTime(0));
                    mMusicPlayerViewModel.stopMusic();
                } else {
                    mCustomListViewModel.playNext();
                }
            });
        }
    };

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int time) {
        int seconds = time / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public void resetToDefault() {
        runOnUiThread(() -> {
            Log.d(TAG, "stopPlaying");
            mActivityMainBinding.musicProgress.setMax(0);
            mActivityMainBinding.musicProgress.setProgress(0);
            mActivityMainBinding.playAndPause.setImageResource(R.drawable.play);
            mActivityMainBinding.totalTime.setText(R.string.default_duration);
        });
    }

    public void initPlayerComponent() {
        mMusicPlayerViewModel.getMaxProgress().observe(this, maxProgress -> {
            mActivityMainBinding.totalTime.setText(formatTime(maxProgress));
            Log.d(TAG, "Observe max progress here.");
        });
        mMusicPlayerViewModel.getCurrentMusic().observe(this, currentMusic -> {
            Log.d(TAG, "Observe current music here.");
            if (!((MusicPlayerApplication) getApplication()).getCurrentUser().equals("GUEST"))
                mHistoryViewModel.addHistoryList(currentMusic.getMusicName());
        });
    }

    public void initPopupWindow() {
        mVolumeViewModel = new ViewModelProvider(this).get(VolumeViewModel.class);
        VolumePopupBinding volumePopupBinding = VolumePopupBinding.inflate(LayoutInflater.from(this), null, false);
        volumePopupBinding.setVolumeViewModel(mVolumeViewModel);
        volumePopupBinding.setLifecycleOwner(this);
        volumePopupBinding.volumeSetting.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVolumeViewModel.setVolumeLevel(progress);
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
        popupWindow.showAsDropDown(mActivityMainBinding.volume, Gravity.CENTER, Gravity.CENTER, 0);
    }

    public void initButton() {
        mActivityMainBinding.playAndPause.setOnClickListener(v -> mMusicPlayerViewModel.switchPlayAndPause());
        mActivityMainBinding.stopMusic.setOnClickListener(v -> {
            mMusicPlayerViewModel.stopMusic();
            mCustomListViewModel.reset();
        });
        mActivityMainBinding.skipNext.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(mMusicPlayerViewModel.getFromList().getValue()))
                mCustomListViewModel.playNext();
        });
        mActivityMainBinding.skipPrevious.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(mMusicPlayerViewModel.getFromList().getValue()))
                mCustomListViewModel.playPrevious();
        });
    }

    public void initVolume() {
        mActivityMainBinding.volume.setOnClickListener(v -> initPopupWindow());
    }

    public void initSeekBar() {
        mActivityMainBinding.musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mActivityMainBinding.currentTime.setText(formatTime(progress));
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
                mMusicPlayerViewModel.seekTo(seekBar.getProgress());
                try {
                    MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().startTimer();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void initMenu() {
        mCustomListFragment = new CustomListFragment();
        mMusicListFragment = new MusicListFragment();
        mHistoryFragment = new HistoryFragment();
        TabLayout menu = findViewById(R.id.tab_layout);
        menu.addTab(menu.newTab().setText("Music list"));
        menu.addTab(menu.newTab().setText("All music"));
        menu.addTab(menu.newTab().setText("Local history"));
        menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mFragmentManager.beginTransaction().
                                show(mCustomListFragment).
                                hide(mMusicListFragment).
                                hide(mHistoryFragment).
                                commit();
                        break;
                    case 1:
                        mFragmentManager.beginTransaction().
                                hide(mCustomListFragment).
                                show(mMusicListFragment).
                                hide(mHistoryFragment).
                                commit();
                        break;
                    case 2:
                        mFragmentManager.beginTransaction().
                                hide(mCustomListFragment).
                                hide(mMusicListFragment).
                                show(mHistoryFragment).
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
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, mCustomListFragment, "Profile").show(mCustomListFragment);
        fragmentTransaction.add(R.id.frame_layout, mMusicListFragment, "Music List").hide(mMusicListFragment);
        fragmentTransaction.add(R.id.frame_layout, mHistoryFragment, "History").hide(mHistoryFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String user = ((MusicPlayerApplication) getApplication()).getCurrentUser();
        Log.d(TAG, "current user: " + user);
        super.onCreate(savedInstanceState);

        mMusicPlayerViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
        mCustomListViewModel = new ViewModelProvider(this).get(CustomListViewModel.class);
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mActivityMainBinding.setMusicPlayerViewModel(mMusicPlayerViewModel);
        mActivityMainBinding.setLifecycleOwner(this);

        initSeekBar();
        initButton();
        initVolume();
        initMenu();
        initFragment();
        initPlayerComponent();

        resetToDefault();

        MusicPlayerServiceConnection.getInstance().setMusicProgressCallback(iCallback);
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