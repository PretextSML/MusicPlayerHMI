package com.pretext.musicplayerhmi.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryUtil {
    private final List<String> historyList = new ArrayList<>();

    public String addMusic(String song) {
        if (historyList.isEmpty()) {
            historyList.add(song);
            return historyList.size() + " : " + song;
        } else if (!historyList.get(historyList.size() - 1).equals(song)) {
            historyList.add(song);
            return historyList.size() + " : " + song;
        }

        return null;
    }

    public List<String> getHistoryList() {
        List<String> numberedList = new ArrayList<>();
        int cnt = 0;
        for (String name : historyList) {
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
