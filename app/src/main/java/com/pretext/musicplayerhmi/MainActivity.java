package com.pretext.musicplayerhmi;

import android.content.Context;
import android.content.Intent;
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
import com.pretext.musicplayerservice.IMusicPlayerInterface;


public class MainActivity extends AppCompatActivity {

    public static IMusicPlayerInterface iMusicPlayerInterface;
    public static Boolean mIsConnected = false;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProfileFragment profileFragment;
    HistoryFragment historyFragment;
    private MusicPlayerServiceConnection musicPlayerServiceConnection;
    private MusicListFragment musicListFragment;

    private void initMenu() {
        TabLayout menu = findViewById(R.id.tab_layout);
        menu.addTab(menu.newTab().setText("Profile"));
        menu.addTab(menu.newTab().setText("Music List"));
        menu.addTab(menu.newTab().setText("Local History"));

        menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragmentManager.beginTransaction().show(profileFragment).hide(musicListFragment).hide(historyFragment).commit();
                        break;
                    case 1:
                        fragmentManager.beginTransaction().hide(profileFragment).show(musicListFragment).hide(historyFragment).commit();
                        break;
                    case 2:
                        fragmentManager.beginTransaction().hide(profileFragment).hide(musicListFragment).show(historyFragment).commit();
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

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        profileFragment = new ProfileFragment();
        musicListFragment = new MusicListFragment();
        historyFragment = new HistoryFragment();
        fragmentTransaction.add(R.id.frame_layout, profileFragment, "Profile").show(profileFragment);
        fragmentTransaction.add(R.id.frame_layout, musicListFragment, "Music List").hide(musicListFragment);
        fragmentTransaction.add(R.id.frame_layout, historyFragment, "History").hide(historyFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();
        initMenu();
        bindService();
    }

    public void bindService() {
        Intent musicPlayerIntent = new Intent();
        musicPlayerServiceConnection = new MusicPlayerServiceConnection(musicListFragment);

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
}