package com.pretext.musicplayerhmi.application;

import android.app.Application;

public class MusicPlayerApplication extends Application {
    private String currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
