package com.pretext.musicplayerhmi.util;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.pretext.musicplayerhmi.BR;


public class UserUtil extends BaseObservable {
    private String mAccount;
    private String mPassword;

    public UserUtil(String mAccount, String mPassword) {
        this.mAccount = mAccount;
        this.mPassword = mPassword;
    }

    @Bindable
    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String mAccount) {
        this.mAccount = mAccount;
        notifyPropertyChanged(BR.account);
    }

    @Bindable
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
        notifyPropertyChanged(BR.password);
    }
}
