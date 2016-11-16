package com.dmplayer.utility;

import com.dmplayer.R;
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

    public static List<PlaylistItem> getLocalPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        return temp;
    }

    public static List<PlaylistItem> getDefaultPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new DefaultPlaylistItemSingle("All songs", "songs", R.drawable.ic_play,
                DefaultPlaylistCategorySingle.ALL_SONGS));

        temp.add(new DefaultPlaylistItemSingle("Most played", "", R.drawable.ic_play,
                DefaultPlaylistCategorySingle.MOST_PLAYED));

        temp.add(new DefaultPlaylistItemSeveral("Albums", "", R.drawable.ic_play,
                DefaultPlaylistCategorySeveral.ALBUMS));

        temp.add(new DefaultPlaylistItemSeveral("Artists", "", R.drawable.ic_play,
                DefaultPlaylistCategorySeveral.ARTISTS));

        temp.add(new DefaultPlaylistItemSeveral("Genres", "", R.drawable.ic_play,
                DefaultPlaylistCategorySeveral.GENRES));

        return temp;
    }

    public static List<PlaylistItem> getVkPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new VkPlaylistItemSingle("My music", "", R.drawable.ic_play,
                VkPlaylistCategorySingle.MY_MUSIC));

        temp.add(new VkPlaylistItemSingle("Recommendations", "", R.drawable.ic_play,
                VkPlaylistCategorySingle.RECOMMENDATIONS));

        temp.add(new VkPlaylistItemSeveral("Popular", "", R.drawable.ic_play,
                VkPlaylistCategorySeveral.POPULAR));

        temp.add(new VkPlaylistItemSeveral("My albums", "", R.drawable.ic_play,
                VkPlaylistCategorySeveral.MY_ALBUMS));

        return temp;
    }

    private static List<PlaylistItem> getPlaylistsFromCursor() {
        return null;
    }
}
