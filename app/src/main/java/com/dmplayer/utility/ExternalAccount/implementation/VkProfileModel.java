package com.dmplayer.utility.ExternalAccount.implementation;

import android.graphics.Bitmap;

public class VkProfileModel {
    private final Bitmap photo;
    private final String nickname;
    private final String songsCount;
    private final String albumsCount;

    public VkProfileModel(Bitmap photo, String nickname, String songsCount, String albumsCount) {
        this.photo = photo;
        this.nickname = nickname;
        this.songsCount = songsCount;
        this.albumsCount = albumsCount;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSongsCount() {
        return songsCount;
    }

    public String getAlbumsCount() {
        return albumsCount;
    }

    public static class Builder {
        private Bitmap photoResource;
        private String nickname;
        private String songsCount;
        private String albumsCount;

        public Builder setPhotoResource(Bitmap photoResource) {
            this.photoResource = photoResource;

            return this;
        }

        public Builder setNickname(String nickname) {
            this.nickname = nickname;

            return this;
        }

        public void addNicknamePart(String part) {
            nickname = ((nickname == null) ? "" : nickname) + part;
        }

        public Builder setSongsCount(String songsCount) {
            this.songsCount = songsCount;

            return this;
        }

        public Builder setAlbumsCount(String albumsCount) {
            this.albumsCount = albumsCount;

            return this;
        }

        public VkProfileModel build() {
            return new VkProfileModel(photoResource, nickname, songsCount, albumsCount);
        }
    }
}
