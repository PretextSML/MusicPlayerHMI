package com.pretext.musicplayerhmi;

public class MusicInfo {
    private String musicName;
    private String musicPath;
    private long musicDuration;

    public MusicInfo(String musicName, long musicDuration, String musicPath) {
        this.musicName = musicName;
        this.musicDuration = musicDuration;
        this.musicPath = musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(long musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }
}