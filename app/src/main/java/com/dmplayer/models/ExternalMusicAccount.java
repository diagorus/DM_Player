package com.dmplayer.models;

import java.util.List;

public interface ExternalMusicAccount {
    ExternalProfileObject loadProfile();
    List<Playlist> loadMusicListsToShow();
    Playlist loadMusicList(String... args);
    void logOut();
}