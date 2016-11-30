package com.dmplayer.models;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmplayer.phonemedia.PhoneMediaControl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    private static long count = 0;

    private long id;
    private String name;
    private transient String path;
    private List<SongDetail> songs;

    private static final String TAG = "LOCAL_PLAYLIST";

    public Playlist() {
        this.id = count++;
        this.name = "";
        this.path = "";
        this.songs = new ArrayList<>();
    }

    public Playlist(String name, List<SongDetail> songs) {
        this();

        this.name = name;
        this.songs = songs;
    }

    public long getId() {
        return id;
    }

    public void addSong(SongDetail newSong){
        songs.add(newSong);
    }

    public List<SongDetail> getSongs(){
        return songs;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getSongCount(){
        return songs.size();
    }

    public void setPath(String path){
        this.path = path;
    }

    public static long getCount() {
        return count;
    }

    public String getPath() {
        return path;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong("tagfor", PhoneMediaControl.SongsLoadFor.LOCAL_PLAYLIST.ordinal());
        bundle.putString("playlistname", name);
        bundle.putString("playlistpath", path);
        bundle.putString("title_one", "ALL my songs");

        return bundle;
    }

    public byte[] getBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();
        }
        catch(Exception ex) {return null;}
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (Exception ex) {
                // ignore close exception
            }
        }
    }

    @Nullable
    public static Playlist decipher(byte[] playlist) {
        ByteArrayInputStream bis = new ByteArrayInputStream(playlist);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (Playlist) in.readObject();
        } catch (Exception ex) {
            return null;
        }
        finally {
            try {
                bis.close();
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }
}
