// IMusicPlayerInterface.aidl
package com.example.musicplayerservice;

// Declare any non-default types here with import statements

interface IMusicPlayerInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void serviceTest();
     boolean checkcheckAuthentication(String username, String password);
}