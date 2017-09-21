package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItemInSeveral;

public class VkPlaylistItemInSeveral extends PlaylistItemInSeveral {
    private VkPlaylistCategorySingle category;

    public VkPlaylistItemInSeveral(int id, String name, VkPlaylistCategorySingle category) {
        super(id, name);
        this.category = category;
    }

    public VkPlaylistCategorySingle getCategory() {
        return category;
    }
}
