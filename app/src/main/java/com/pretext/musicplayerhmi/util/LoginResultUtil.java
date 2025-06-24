package com.pretext.musicplayerhmi.util;

public class LoginResultUtil {
    private final boolean success;
    private final String username;
    private final String errorMessage;


    public LoginResultUtil(boolean success, String username) {
        this.success = success;
        this.username = username;
        this.errorMessage = null;
    }

    public LoginResultUtil(boolean success, String username, String errorMessage) {
        this.success = success;
        this.username = username;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
