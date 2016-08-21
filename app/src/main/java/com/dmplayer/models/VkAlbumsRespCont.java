package com.dmplayer.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class VkAlbumsRespCont {
    @SerializedName("count")
    private String albumCount;

    @SerializedName("items")
    private VkAlbum[] vkAlb;

    public String getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(String albumCount) {
        this.albumCount = albumCount;
    }

    public VkAlbum[] getVkAlb() {
        return vkAlb;
    }

    public void setVkAlb(VkAlbum[] vkAlb) {
        this.vkAlb = vkAlb;
    }

    @Override
    public String toString() {
        return "VkAlbumsRespCont{" +
                "albumCount='" + albumCount + '\'' +
                ", vkAlb=" + Arrays.toString(vkAlb) +
                '}';
    }
}
