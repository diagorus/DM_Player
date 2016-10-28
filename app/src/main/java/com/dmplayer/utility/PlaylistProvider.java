package com.dmplayer.utility;

import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.DefaultPlaylistItemSingle;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemSeveral;
import com.dmplayer.models.playlisitems.VkPlaylistItemSingle;

import java.util.ArrayList;
import java.util.List;

public final class PlaylistProvider {
    private PlaylistProvider() {
        throw new AssertionError();
    }

    public static List<PlaylistItem> getDefaultPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new DefaultPlaylistItemSingle("ALL SONGS", "", android.R.color.transparent,
                DefaultPlaylistCategorySingle.ALL_SONGS));

        temp.add(new DefaultPlaylistItemSingle("MOST PLAYED", "", android.R.color.transparent,
                DefaultPlaylistCategorySingle.MOST_PLAYED));

        temp.add(new DefaultPlaylistItemSeveral("ALBUMS", "", android.R.color.transparent,
                DefaultPlaylistCategorySeveral.ALBUMS));

        temp.add(new DefaultPlaylistItemSeveral("ARTISTS", "", android.R.color.transparent,
                DefaultPlaylistCategorySeveral.ARTISTS));

        temp.add(new DefaultPlaylistItemSeveral("GENRES", "", android.R.color.transparent,
                DefaultPlaylistCategorySeveral.GENRES));

        return temp;
    }

    public static List<PlaylistItem> getVkPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new VkPlaylistItemSingle("MY MUSIC", "", android.R.color.transparent,
                VkPlaylistCategorySingle.MY_MUSIC));

        temp.add(new VkPlaylistItemSingle("RECOMMENDATIONS", "", android.R.color.transparent,
                VkPlaylistCategorySingle.RECOMMENDATIONS));

        temp.add(new VkPlaylistItemSeveral("POPULAR", "", android.R.color.transparent,
                VkPlaylistCategorySeveral.POPULAR));

        temp.add(new VkPlaylistItemSeveral("MY ALBUMS", "", android.R.color.transparent,
                VkPlaylistCategorySeveral.MY_ALBUMS));
        return temp;
    }
}
