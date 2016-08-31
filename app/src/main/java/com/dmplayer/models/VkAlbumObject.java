package com.dmplayer.models;

public class VkAlbumObject {
    private String id;

    private String owner_id ;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "VkAlbumObject{" +
                "id='" + id + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
