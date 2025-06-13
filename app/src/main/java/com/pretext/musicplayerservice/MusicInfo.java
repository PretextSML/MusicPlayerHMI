package com.pretext.musicplayerservice;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Parcelable {

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            return new MusicInfo(in);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
    private String musicName;
    private String musicPath;
    private long musicDuration;

    protected MusicInfo(Parcel in) {
        musicName = in.readString();
        musicDuration = in.readLong();
        musicPath = in.readString();
    }

    public MusicInfo(String musicName, long musicDuration, String musicPath) {
        this.musicName = musicName;
        this.musicDuration = musicDuration;
        this.musicPath = musicPath;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicName);
        dest.writeLong(musicDuration);
        dest.writeString(musicPath);
    }

    @Override
    public int describeContents() {
        return 0;
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
