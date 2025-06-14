// IMusicPlayerInterface.aidl
package com.pretext.musicplayerservice;

// Declare any non-default types here with import statements
import com.pretext.musicplayerservice.IMusicProgressCallback;

interface IMusicPlayerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void playMusic(String musicName);
    void setDuration(int duration);
    void pauseMusic();
    void resumeMusic();
    void stopMusic();

    void startTimer();
    void stopTimer();

    void registerCallback(IMusicProgressCallback callback);
    void unregisterCallback(IMusicProgressCallback callback);

}