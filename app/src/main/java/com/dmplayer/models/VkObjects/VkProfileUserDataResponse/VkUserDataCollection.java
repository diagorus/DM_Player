package com.dmplayer.models.VkObjects.VkProfileUserDataResponse;

public class VkUserDataCollection {

    private VkUserData[] response;

    public VkUserData[] getResponse() {
        return response;
    }

    public void setResponse(VkUserData[] response) {
        this.response = response;
    }

    public String toString() {
        return "VkUserDataCollection{" +
                "response=" + response +
                '}';
    }

    public String[] getStringValues() {
        return new String[] {
            response[0].getId(),
            response[0].getFirst_name(),
            response[0].getLast_name(),
            response[0].getPhoto_100()
        };
    }
}