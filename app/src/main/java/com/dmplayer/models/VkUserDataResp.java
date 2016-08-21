package com.dmplayer.models;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class VkUserDataResp {

    @SerializedName("response")
    private VkUserDataRespItem[] vkUserDataRespItems;

    public VkUserDataRespItem[] getVkUserDataRespItems()
    {
        return vkUserDataRespItems;
    }

    public void setVkUserDataRespItems(VkUserDataRespItem[] vkUserDataRespItems) {
        this.vkUserDataRespItems = vkUserDataRespItems;
    }

    public String toString() {
        return "VkUserDataResp{" +
                "vkUserDataRespItems=" + Arrays.toString(vkUserDataRespItems) +
                '}';
    }

    public String[] getStringValues() {
        return new String[] {
            vkUserDataRespItems[0].getId(),
            vkUserDataRespItems[0].getFirst_name(),
            vkUserDataRespItems[0].getLast_name(),
            vkUserDataRespItems[0].getPhoto_100()
        };
    }
}