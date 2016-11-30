package com.dmplayer.models;

public class PlaylistItem {
    public static final int NO_ID = -1;
    public static final String NO_NAME = "<NO_NAME>";
    public static final String NO_DETAILS = "<NO_DETAILS>";
    public static final int NO_IMAGE_RESOURCE = -1;

    private final int id;
    private final String name;
    private final String details;
    private final int imageResourceId;

    public PlaylistItem(int id, String name, String details, int imageResourceId) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.imageResourceId = imageResourceId;
    }

    public int getId() {
        return id;
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