package com.dmplayer.models;

public class ExternalProfileObject {
    private final String photoUrl;
    private final String nickname;
    private final String songsCount;
    private final String albumsCount;

    public ExternalProfileObject(String photoUrl, String nickname, String songsCount, String albumsCount) {
        this.photoUrl = photoUrl;
        this.nickname = nickname;
        this.songsCount = songsCount;
        this.albumsCount = albumsCount;
    }

    public String getPhotoUrl() {
        return photoUrl;
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
        private String photoUrl;
        private String nickname;
        private String songsCount;
        private String albumsCount;

        public Builder setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;

            return this;
        }

        public Builder setNickname(String nickname) {
            this.nickname = nickname;

            return this;
        }

        public void addNicknamePart(String part) {
            StringBuilder stringBuilder = new StringBuilder((nickname==null) ? "": nickname);
            stringBuilder.append(part);
            nickname = stringBuilder.toString();
        }

        public Builder setSongsCount(String songsCount) {
            this.songsCount = songsCount;

            return this;
        }

        public Builder setAlbumsCount(String albumsCount) {
            this.albumsCount = albumsCount;

            return this;
        }

        public ExternalProfileObject build() {
            return new ExternalProfileObject(photoUrl, nickname, songsCount, albumsCount);
        }
    }
}
