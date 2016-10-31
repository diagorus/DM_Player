package com.dmplayer.models.playlisitems;

import com.dmplayer.models.PlaylistItemInSeveral;

public class DefaultPlaylistItemInSeveral extends PlaylistItemInSeveral {
    private DefaultPlaylistCategorySingle category;

    public DefaultPlaylistItemInSeveral(int id, String name, DefaultPlaylistCategorySingle category) {
        super(id, name);
        this.category = category;
    }

    public DefaultPlaylistCategorySingle getCategory() {
        return category;
    }
}
