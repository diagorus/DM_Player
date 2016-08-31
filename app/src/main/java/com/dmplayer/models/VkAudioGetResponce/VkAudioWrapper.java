package com.dmplayer.models.VkAudioGetResponce;

public class VkAudioWrapper
{
    private VkAudioCollection response;

    public VkAudioCollection getResponse ()
    {
        return response;
    }

    public void setResponse (VkAudioCollection response)
    {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return "VkAudioWrapper [response = " + response + "]";
    }
}
