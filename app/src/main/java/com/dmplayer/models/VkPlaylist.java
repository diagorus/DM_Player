package com.dmplayer.models;

import android.os.Bundle;

import com.dmplayer.phonemidea.PhoneMediaControl;

public class VkPlaylist extends Playlist {
    private int type;
    private String id;

    public final static int ALL = 0;
    public final static int ALBUM = 1;
    public final static int POPULAR = 2;
    public final static int RECOMMENDED = 3;

    public VkPlaylist(String name, int type) {
        super(name, true);
        this.type = type;
    }

    public VkPlaylist(String name, int type, String id) {
        super(name, true);
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return (type == ALBUM)? id : "";
    }

    @Override
    public Bundle getBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong("tagfor", PhoneMediaControl.SongsLoadFor.VkPlaylist.ordinal());
        bundle.putString("playlistname", getName());
        bundle.putInt("playlisttype", type);
        bundle.putString("playlistid", (type == ALBUM)? id : "");
        bundle.putString("title_one", "All my songs");

        return bundle;
    }
}
