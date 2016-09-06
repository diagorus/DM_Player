package com.dmplayer.models;

import java.util.List;
import java.util.Map;

public interface ExternalMusicAccount {
    ExternalProfileObject loadProfile();
    List<Playlist> loadMusicLists();
    void logOut();
}