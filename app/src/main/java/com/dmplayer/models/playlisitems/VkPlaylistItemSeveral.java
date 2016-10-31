package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class VkPlaylistItemSeveral extends PlaylistItem {
    private final VkPlaylistCategorySeveral category;

    public VkPlaylistItemSeveral(String name, String details, int imageResourceId,
                                     VkPlaylistCategorySeveral category) {
        super(name, details, imageResourceId);

        this.category = category;
    }

    public VkPlaylistCategorySeveral getCategory() {
        return category;
    }
}
