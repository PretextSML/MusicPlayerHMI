package com.pretext.musicplayerhmi.util;

import java.io.Serializable;

public class MusicInfoUtil implements Serializable {
    private final String musicName;
    private final String musicPath;
    private final long musicDuration;

    public MusicInfoUtil(String musicName, long musicDuration, String musicPath) {
        this.musicName = musicName;
        this.musicDuration = musicDuration;
        this.musicPath = musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    public String getMusicPath() {
        return musicPath;
    }
}