package com.pretext.musicplayerhmi.util;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.pretext.musicplayerhmi.BR;


public class UserUtil extends BaseObservable {
    private String account;
    private String password;

    public UserUtil(String account, String password) {
        this.account = account;
        this.password = password;
    }

    @Bindable
    public String getAccount() {
        return account;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
        notifyPropertyChanged(BR.account);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
}
