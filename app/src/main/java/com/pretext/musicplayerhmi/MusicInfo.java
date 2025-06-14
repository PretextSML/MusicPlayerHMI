package com.pretext.musicplayerhmi;

public class MusicInfo {
    private final String musicName;
    private final String musicPath;
    private final long musicDuration;

    public MusicInfo(String musicName, long musicDuration, String musicPath) {
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