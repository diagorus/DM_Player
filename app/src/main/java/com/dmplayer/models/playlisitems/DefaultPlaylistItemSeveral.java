package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class DefaultPlaylistItemSeveral extends PlaylistItem {
    private final DefaultPlaylistCategorySeveral category;

    public DefaultPlaylistItemSeveral(String name, String details, int imageResourceId,
                                      DefaultPlaylistCategorySeveral category) {
        super(name, details, imageResourceId);

        this.category = category;
    }

    public DefaultPlaylistCategorySeveral getCategory() {
        return category;
    }
}
