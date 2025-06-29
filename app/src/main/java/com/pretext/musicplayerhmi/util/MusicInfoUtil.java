package com.pretext.musicplayerhmi.util;

import java.io.Serializable;

public class MusicInfoUtil implements Serializable {
    private final String mMusicName;
    private final String mMusicPath;
    private final long mMusicDuration;

    public MusicInfoUtil(String mMusicName, long mMusicDuration, String mMusicPath) {
        this.mMusicName = mMusicName;
        this.mMusicDuration = mMusicDuration;
        this.mMusicPath = mMusicPath;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public long getMusicDuration() {
        return mMusicDuration;
    }

    public String getMusicPath() {
        return mMusicPath;
    }
}