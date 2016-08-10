package com.dmplayer.playlist;

import android.support.annotation.Nullable;

import com.dmplayer.models.SongDetail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by asus on 03.08.2016.
 */
public class Playlist implements Serializable {

    private String name="";
    private transient String path="";
    private ArrayList<SongDetail> songs = new ArrayList<>();

    public void addSong(SongDetail newSong){
        songs.add(newSong);
    }

    public ArrayList<SongDetail> getSongs(){
        return songs;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public int getSongCount(){
        return songs.size();
    }

    public void setPath(String path){
        this.path=path;
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
            } catch (Exception ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                // ignore close exception
            }
        }
    }
}
