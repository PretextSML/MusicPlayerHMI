// IMusicPlayerInterface.aidl
package com.pretext.musicplayerservice;

// Declare any non-default types here with import statements
import com.pretext.musicplayerservice.MusicInfo;

interface IMusicPlayerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void playMusic(String musicName);

     List<MusicInfo> readMusicFile();
}