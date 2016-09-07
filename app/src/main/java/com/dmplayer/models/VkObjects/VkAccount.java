package com.dmplayer.models.VkObjects;

import android.util.Log;

import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.ExternalMusicAccount;
import com.dmplayer.models.ExternalProfileObject;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserData;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserDataCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkAccount implements ExternalMusicAccount {
    private final String userId;
    private final String token;

    private VkAPIService service;

    public final static String VK_API_URL = "https://api.vk.com/method/";

    private final static String TAG = "VkAccount";

    private VkAccount(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    @Override
    public ExternalProfileObject loadProfile() {
        createApiService();

        ExternalProfileObject.Builder profileObjectBuilder = new ExternalProfileObject.Builder();

        loadVkUserData(profileObjectBuilder);
        loadVkMusicData(profileObjectBuilder);

        return profileObjectBuilder.build();
    }

    @Override
    public List<Playlist> loadMusicListsToShow() {
        createApiService();
        List<Playlist> playlistsToShow = new ArrayList<>();

        loadVkDefaultAlbums(playlistsToShow);
        try {
            loadVkUserAlbums(playlistsToShow);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return playlistsToShow;
    }

    @Override
    public Playlist loadMusicList(String... args) {
        return null;
    }

    @Override
    public void logOut() {}

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(VkAPIService.class);
    }

    private void loadVkUserAlbums(List<Playlist> playlistsToShow) throws IOException {
        Call<VkAlbumsWrapper> callForAlbums = service.loadAlbums("0", "100", userId, token);

        Response<VkAlbumsWrapper> responseAlbums = callForAlbums.execute();

        ArrayList<VkAlbumObject> vkAllAlbums = new ArrayList<>(
                Arrays.asList(responseAlbums.body().getResponse().getItems()));

        for (VkAlbumObject vkAlbum : vkAllAlbums) {
            playlistsToShow.add(new VkPlaylist(vkAlbum.getTitle(), VkPlaylist.ALBUM, vkAlbum.getId()));
        }
    }

    private void loadVkDefaultAlbums(List<Playlist> playlistsToShow) {
        playlistsToShow.add(new VkPlaylist("My Audios", VkPlaylist.ALL));
        playlistsToShow.add(new VkPlaylist("Popular", VkPlaylist.POPULAR));
        playlistsToShow.add(new VkPlaylist("Recommended", VkPlaylist.RECOMMENDED));
    }

    private void loadVkMusicData(ExternalProfileObject.Builder profileObjectBuilder) {
        Call<ResponseBody> callForSongsCount = service.loadSongsCount(userId, token);

        Call<VkAlbumsWrapper> callForAlbumsCount = service.loadAlbums("0", "100", userId, token);

        try {
            Response<ResponseBody> responseSongsCount = callForSongsCount.execute();
            Response<VkAlbumsWrapper> responseAlbumsCount = callForAlbumsCount.execute();

            String json = responseSongsCount.body().string();
            String songCount = json.substring(json.lastIndexOf(":") + 1, json.length() - 1);
            profileObjectBuilder.setSongsCount(songCount);

            String albumsCount = responseAlbumsCount.body().getResponse().getCount();
            profileObjectBuilder.setAlbumsCount(albumsCount);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void loadVkUserData(ExternalProfileObject.Builder profileObjectBuilder) {
        Call<VkUserDataCollection> callForUserData = service.loadUserData("photo_100", token);

        try {
            Response<VkUserDataCollection> responseUserData = callForUserData.execute();

            VkUserData userData = responseUserData.body().getResponse()[0];

            profileObjectBuilder.addNicknamePart(userData.getFirst_name());
            profileObjectBuilder.addNicknamePart(" " + userData.getLast_name());
            profileObjectBuilder.setPhotoUrl(userData.getPhoto_100());
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static class Builder {
        private String userId;
        private String token;

        public Builder setUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        public VkAccount build() {
            return new VkAccount(userId, token);
        }
    }
}
