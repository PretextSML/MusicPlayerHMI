package com.pretext.musicplayerhmi.util;

public class LoginResultUtil {
    private final boolean mSuccess;
    private final String mUsername;
    private final String mErrorMessage;


    public LoginResultUtil(boolean mSuccess, String mUsername) {
        this.mSuccess = mSuccess;
        this.mUsername = mUsername;
        this.mErrorMessage = null;
    }

    public LoginResultUtil(boolean mSuccess, String mUsername, String mErrorMessage) {
        this.mSuccess = mSuccess;
        this.mUsername = mUsername;
        this.mErrorMessage = mErrorMessage;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
