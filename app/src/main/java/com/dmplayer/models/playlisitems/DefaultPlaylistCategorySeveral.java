package com.dmplayer.models.playlisitems;

public enum DefaultPlaylistCategorySeveral {
    ARTISTS,
    GENRES,
    ALBUMS;

    public static DefaultPlaylistCategorySeveral valueOf(int value) {
        for (DefaultPlaylistCategorySeveral category : values()) {
            if (category.ordinal() == value) {
                return category;
            }
        }

        throw new IllegalArgumentException("Value not found!");
    }
}
