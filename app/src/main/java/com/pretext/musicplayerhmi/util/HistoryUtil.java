package com.pretext.musicplayerhmi.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryUtil {
    private final List<String> mHistoryList = new ArrayList<>();

    public void addMusic(String song) {
        if (mHistoryList.isEmpty()) {
            mHistoryList.add(song);
        } else if (!mHistoryList.get(mHistoryList.size() - 1).equals(song)) {
            mHistoryList.add(song);
        }
    }

    public List<String> getHistoryList() {
        List<String> numberedList = new ArrayList<>();
        int cnt = 0;
        for (String name : mHistoryList) {
            cnt++;
            numberedList.add(cnt + " : " + name);
        }
        return numberedList;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public HistoryUtil fromGson(String json) {
        return new Gson().fromJson(json, HistoryUtil.class);
    }
}
