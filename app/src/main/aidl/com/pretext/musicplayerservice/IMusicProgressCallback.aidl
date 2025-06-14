// IMusicProgressCallback.aidl
package com.pretext.musicplayerservice;

// Declare any non-default types here with import statements

interface IMusicProgressCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onProgressChanged(long currentDuration);
}