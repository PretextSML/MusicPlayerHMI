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
    private final MutableLiveData<UserUtil> mUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final MutableLiveData<LoginResultUtil> mLoginResult = new MutableLiveData<>();
    private final MutableLiveData<RegisterResultUtil> mRegisterResult = new MutableLiveData<>();

    public LoginViewModel() {
        mUser.setValue(new UserUtil("", ""));
    }

    public LiveData<UserUtil> getUser() {
        return mUser;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public MutableLiveData<LoginResultUtil> getLoginResult() {
        return mLoginResult;
    }

    public MutableLiveData<RegisterResultUtil> getRegisterResult() {
        return mRegisterResult;
    }

    public void authenticateUser(String account, String password) {
        mIsLoading.setValue(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (!account.isBlank() && !password.isBlank()) {
                    boolean isCorrect = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().authenticatedUser(account, password);
                    if (isCorrect) {
                        mLoginResult.postValue(new LoginResultUtil(true, account));
                    } else {
                        mLoginResult.postValue(new LoginResultUtil(false, null, "Username or password error!"));
                    }
                } else {
                    mLoginResult.postValue(new LoginResultUtil(false, null, "Username or password is blank!"));
                }
            } catch (RemoteException e) {
                mLoginResult.postValue(new LoginResultUtil(false, null, "Unable to connect service!"));
            } finally {
                mIsLoading.postValue(false);
            }
        });
    }

    public void registerUser(String account, String password) {
        mIsLoading.setValue(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (!account.isBlank() && !password.isBlank()) {
                    boolean userExists = MusicPlayerServiceConnection.getInstance().getMusicPlayerInterface().addNewUser(account, password);
                    if (!userExists) {
                        mRegisterResult.postValue(new RegisterResultUtil(true, account));
                    } else {
                        mRegisterResult.postValue(new RegisterResultUtil(false, null, "User already exists!"));
                    }
                } else {
                    mRegisterResult.postValue(new RegisterResultUtil(false, null, "Username or password is blank!"));
                }
            } catch (RemoteException e) {
                mRegisterResult.postValue(new RegisterResultUtil(false, null, "Unable to connect service!"));
            } finally {
                mIsLoading.postValue(false);
            }
        });
    }
}
