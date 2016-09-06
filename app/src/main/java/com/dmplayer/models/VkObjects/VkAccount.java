package com.dmplayer.models.VkObjects;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.ExternalMusicAccount;
import com.dmplayer.models.ExternalProfileObject;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserDataCollection;
import com.dmplayer.uicomponent.PlaylistsLoadingController;
import com.dmplayer.uicomponent.SwappingLayout.SwappingLayoutController;
import com.dmplayer.utility.VkSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkAccount implements ExternalMusicAccount {
    private final String userId;
    private final String token;

    private VkAPIService service;

    private static final String TAG = "VkAccount";

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
                .baseUrl(VkSettings.VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(VkAPIService.class);
    }

    private void loadVkUserAlbums(List<Playlist> playlistsToShow) throws IOException {
        Map<String, String> optionsForAlbums = new HashMap<>();
        optionsForAlbums.put("offset", "0");
        optionsForAlbums.put("count", "100");
        optionsForAlbums.put("owner_id", userId);
        optionsForAlbums.put("access_token", token);
        optionsForAlbums.put("v", "5.53");

        Call<VkAlbumsWrapper> callForAlbums = service.loadAlbums(optionsForAlbums);

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
        Map<String, String> optionsForSongsCount = new HashMap<>();
        optionsForSongsCount.put("owner_id", userId);
        optionsForSongsCount.put("access_token", token);
        optionsForSongsCount.put("v", "5.53");

        Call<ResponseBody> callForSongsCount = service.loadSongsCount(optionsForSongsCount);

        Map<String, String> optionsForAlbumsCount = new HashMap<>();
        optionsForAlbumsCount.put("offset", "0");
        optionsForAlbumsCount.put("count", "100");
        optionsForAlbumsCount.put("owner_id", userId);
        optionsForAlbumsCount.put("access_token", token);
        optionsForAlbumsCount.put("v", "5.53");

        Call<VkAlbumsWrapper> callForAlbumsCount = service.loadAlbums(optionsForAlbumsCount);

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
        Map<String, String> optionsForUserData = new HashMap<>();
        optionsForUserData.put("fields", "photo_100");
        optionsForUserData.put("access_token", token);
        optionsForUserData.put("v", "5.53");

        Call<VkUserDataCollection> callForUserData = service.loadUserData(optionsForUserData);
        try {
            Response<VkUserDataCollection> responseUserData = callForUserData.execute();

            String[] userData = responseUserData.body().getStringValues();
            profileObjectBuilder.addNicknamePart(userData[1]);
            profileObjectBuilder.addNicknamePart(" " + userData[2]);
            profileObjectBuilder.setPhotoUrl(userData[3]);
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
