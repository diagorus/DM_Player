package com.dmplayer.externalaccount;

import com.dmplayer.models.Playlist;

import java.util.List;

public interface ExternalMusicLoader {
    List<Playlist> loadMusicListsToShow();
    Playlist loadMusicList(int type, String id, String name);
}
