package com.dmplayer.models.playlisitems;

public enum VkPlaylistCategorySingle {
    MY_MUSIC,
    RECOMMENDATIONS,

    GENRE,
    ALBUM;

    public static VkPlaylistCategorySingle valueOf(int value) {
        for (VkPlaylistCategorySingle category : values()) {
            if (category.ordinal() == value) {
                return category;
            }
        }

        throw new IllegalArgumentException("Value not found!");
    }
}
