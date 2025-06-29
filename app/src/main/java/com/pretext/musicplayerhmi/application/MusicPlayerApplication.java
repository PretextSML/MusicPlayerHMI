package com.pretext.musicplayerhmi.application;

import android.app.Application;

public class MusicPlayerApplication extends Application {
    private String mCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(String mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }
}
