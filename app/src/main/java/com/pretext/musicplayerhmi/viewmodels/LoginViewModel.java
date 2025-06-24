package com.pretext.musicplayerhmi.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pretext.musicplayerhmi.util.UserUtil;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<UserUtil> user;

    public MutableLiveData<UserUtil> getUser() {
        if (user == null)
            user = new MutableLiveData<>();

        return user;
    }
}
