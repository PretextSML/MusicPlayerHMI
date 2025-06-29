package com.pretext.musicplayerhmi.viewmodel;

import android.os.RemoteException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pretext.musicplayerhmi.connection.MusicPlayerServiceConnection;
import com.pretext.musicplayerhmi.util.LoginResultUtil;
import com.pretext.musicplayerhmi.util.RegisterResultUtil;
import com.pretext.musicplayerhmi.util.UserUtil;

import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<UserUtil> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<LoginResultUtil> loginResult = new MutableLiveData<>();
    private final MutableLiveData<RegisterResultUtil> registerResult = new MutableLiveData<>();

    public LoginViewModel() {
        user.setValue(new UserUtil("", ""));
    }

    public LiveData<UserUtil> getUser() {
        return user;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<LoginResultUtil> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<RegisterResultUtil> getRegisterResult() {
        return registerResult;
    }

    public void authenticateUser(String account, String password) {
        isLoading.setValue(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                boolean isCorrect = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().authenticatedUser(account, password);
                loginResult.postValue(new LoginResultUtil(isCorrect, isCorrect ? account : null));
            } catch (RemoteException e) {
                loginResult.postValue(new LoginResultUtil(false, null, "Unable to connect service!"));
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void registerUser(String account, String password) {
        isLoading.setValue(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                boolean userExists = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().addNewUser(account, password);
                if (!userExists) {
                    registerResult.postValue(new RegisterResultUtil(true, account));
                } else {
                    registerResult.postValue(new RegisterResultUtil(false, null, "User already exists!"));
                }
            } catch (RemoteException e) {
                registerResult.postValue(new RegisterResultUtil(false, null, "Unable to connect service!"));
            } finally {
                isLoading.postValue(false);
            }
        });
    }
}
