package com.dmplayer.models.VkObjects;

import android.os.Bundle;

import com.dmplayer.models.Playlist;
import com.dmplayer.models.SongDetail;
import com.dmplayer.phonemedia.PhoneMediaControl;

import java.util.List;

public class VkPlaylist extends Playlist {
    private int type;
    private Long id;

    public final static int ALL = 0;
    public final static int ALBUM = 1;
    public final static int POPULAR = 2;
    public final static int RECOMMENDED = 3;

    private VkPlaylist(long id, String name, List<SongDetail> songs) {
        super(id, name, songs);
    }

//    public VkPlaylist(String name, int type) {
//        super(name, true);
//        this.type = type;
//    }
//
//    public VkPlaylist(String name, int type, String id) {
//        super(name, true);
//        this.type = type;
//        this.id = id;
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
