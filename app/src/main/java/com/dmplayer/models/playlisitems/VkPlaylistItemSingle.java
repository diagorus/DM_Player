package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class VkPlaylistItemSingle extends PlaylistItem {
    private final VkPlaylistCategorySingle category;

    public VkPlaylistItemSingle(String name, String details, int imageResourceId,
                                VkPlaylistCategorySingle category) {
        super(name, details, imageResourceId);

        this.category = category;
    }

    public VkPlaylistCategorySingle getCategory() {
        return category;
    }
}
