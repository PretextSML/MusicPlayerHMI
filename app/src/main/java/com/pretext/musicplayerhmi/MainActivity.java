package com.pretext.musicplayerhmi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.fragment.HistoryFragment;
import com.pretext.musicplayerhmi.fragment.MusicListFragment;
import com.pretext.musicplayerhmi.fragment.ProfileFragment;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public static Context getContext() {
        return context;
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
        initFragment();
        initMenu();

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
        unbindService(MusicPlayerServiceConnection.getInstance());
        MusicPlayerServiceConnection.getInstance().setMusicPlayerServiceConnection(null);
    }
}