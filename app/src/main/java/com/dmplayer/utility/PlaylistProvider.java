package com.dmplayer.utility;

import android.content.Context;
import android.database.Cursor;

import com.dmplayer.R;
import com.dmplayer.dbhandler.PlaylistTableHelper;
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

    public static List<PlaylistItem> getLocalPlaylists(Context context) {
        List<PlaylistItem> temp = new ArrayList<>();

        Cursor playlistCursor = PlaylistTableHelper.getInstance(context).getPlaylists();


        return temp;
    }

    public static List<PlaylistItem> getDefaultPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new DefaultPlaylistItemSingle(PlaylistItem.NO_ID, "ALL songs", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, DefaultPlaylistCategorySingle.ALL_SONGS));

        temp.add(new DefaultPlaylistItemSingle(PlaylistItem.NO_ID, "Most played", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, DefaultPlaylistCategorySingle.MOST_PLAYED));

        temp.add(new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Albums", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, DefaultPlaylistCategorySeveral.ALBUMS));

        temp.add(new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Artists", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, DefaultPlaylistCategorySeveral.ARTISTS));

        temp.add(new DefaultPlaylistItemSeveral(PlaylistItem.NO_ID, "Genres", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, DefaultPlaylistCategorySeveral.GENRES));

        return temp;
    }

    public static List<PlaylistItem> getVkPlaylists() {
        List<PlaylistItem> temp = new ArrayList<>();

        temp.add(new VkPlaylistItemSingle(PlaylistItem.NO_ID, "My music", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, VkPlaylistCategorySingle.MY_MUSIC));

        temp.add(new VkPlaylistItemSingle(PlaylistItem.NO_ID, "Recommendations", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, VkPlaylistCategorySingle.RECOMMENDATIONS));

        temp.add(new VkPlaylistItemSeveral(PlaylistItem.NO_ID, "Popular", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, VkPlaylistCategorySeveral.POPULAR));

        temp.add(new VkPlaylistItemSeveral(PlaylistItem.NO_ID, "My albums", PlaylistItem.NO_DETAILS,
                R.drawable.ic_play, VkPlaylistCategorySeveral.MY_ALBUMS));

        return temp;
    }
}
