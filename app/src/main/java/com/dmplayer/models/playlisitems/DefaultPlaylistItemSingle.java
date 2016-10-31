package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItem;

public class DefaultPlaylistItemSingle extends PlaylistItem {
    private final DefaultPlaylistCategorySingle category;

    public DefaultPlaylistItemSingle(String name, String details, int imageResourceId,
                                     DefaultPlaylistCategorySingle category) {
        super(name, details, imageResourceId);

        this.category = category;
    }

    public DefaultPlaylistCategorySingle getCategory() {
        return category;
    }
}
