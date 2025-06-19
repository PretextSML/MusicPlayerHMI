package com.pretext.musicplayerhmi;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HistoryUtil {
    private final List<String> historyList = new ArrayList<>();

    public void addMusic(String song) {
        historyList.add(song);
        if (historyList.size() > 50) {
            historyList.remove(0);
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public HistoryUtil fromGson(String json) {
        return new Gson().fromJson(json, HistoryUtil.class);
    }
}
