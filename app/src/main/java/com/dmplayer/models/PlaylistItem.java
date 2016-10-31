package com.dmplayer.models;

public class PlaylistItem {
    private final String name;
    private final String details;
    private final int imageResourceId;

    public PlaylistItem(String name, String details, int imageResourceId) {
        this.name = name;
        this.details = details;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}