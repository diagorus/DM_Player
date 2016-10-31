package com.dmplayer.models.playlisitems;

public enum VkPlaylistCategorySeveral {
    MY_ALBUMS,
    POPULAR;

    public static VkPlaylistCategorySeveral valueOf(int value) {
        for (VkPlaylistCategorySeveral category : values()) {
            if (category.ordinal() == value) {
                return category;
            }
        }

        throw new IllegalArgumentException("Value not found!");
    }
}
