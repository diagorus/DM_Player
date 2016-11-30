package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class DefaultPlaylistItemSeveral extends PlaylistItem {
    private final DefaultPlaylistCategorySeveral category;

    public DefaultPlaylistItemSeveral(int id, String name, String details, int imageResourceId,
                                      DefaultPlaylistCategorySeveral category) {
        super(id, name, details, imageResourceId);

        this.category = category;
    }

    public DefaultPlaylistCategorySeveral getCategory() {
        return category;
    }
}
