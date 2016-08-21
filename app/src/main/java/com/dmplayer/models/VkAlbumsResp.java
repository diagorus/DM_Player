package com.dmplayer.models;

public class VkAlbumsResp
{
    private VkAlbumsRespCont response;

    public VkAlbumsRespCont getResponse ()
    {
        return response;
    }

    public void setResponse (VkAlbumsRespCont response)
    {
        this.response = response;
    }

    @Override
    public String toString()
    {
        return "VkAlbumsRespCont [response = "+response+"]";
    }
}