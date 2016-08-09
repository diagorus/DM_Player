package com.dmplayer.playlist;

import com.dmplayer.models.SongDetail;

import java.util.ArrayList;

/**
 * Created by asus on 03.08.2016.
 */
public class Playlist {

    private String name;
    private ArrayList<SongDetail> songs;

    public ArrayList<SongDetail> getSongs(){
        return songs;
    }

    public String getName(){
        return name;
    }

}
