package com.dmplayer.models.playlisitems;

public enum DefaultPlaylistCategorySingle {
    ALL_SONGS,
    MOST_PLAYED,

    GENRE,
    ARTIST,
    ALBUM;

    public static DefaultPlaylistCategorySingle valueOf(int value) {
        for (DefaultPlaylistCategorySingle category : values()) {
            if (category.ordinal() == value) {
                return category;
            }
        }

        throw new IllegalArgumentException("Value not found!");
    }
}