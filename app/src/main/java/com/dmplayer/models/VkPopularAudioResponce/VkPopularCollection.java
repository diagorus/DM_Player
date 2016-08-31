package com.dmplayer.models.VkPopularAudioResponce;


import com.dmplayer.models.VkAudioObject;

public class VkPopularCollection {

    private VkAudioObject[] response;

    public VkAudioObject[] getResponse()
    {
        return response;
    }

    public void setResponse(VkAudioObject[] response) {
        this.response = response;
    }

    public String toString() {
        return "VkPopularCollection{" +
                "response=" + response +
                '}';
    }
}
