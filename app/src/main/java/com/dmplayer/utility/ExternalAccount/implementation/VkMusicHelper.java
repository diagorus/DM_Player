package com.dmplayer.utility.ExternalAccount.implementation;

import android.util.Log;

import com.dmplayer.converters.VkToSongDetailConverter;
import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.VkObjects.VkAlbumObject;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkAudioGetResponce.VkAudioWrapper;
import com.dmplayer.models.VkObjects.VkAudioObject;
import com.dmplayer.models.VkObjects.VkPlaylist;
import com.dmplayer.models.VkObjects.VkPopularAudioResponce.VkPopularCollection;
import com.dmplayer.utility.ExternalAccount.core.ExternalMusicLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkMusicHelper implements ExternalMusicLoader {
    private String userId;
    private String token;

    private VkAPIService service;

    private final static String TAG = "VkMusicHelper";

    private VkMusicHelper(String userId, String token) {
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
    public Playlist loadMusicList(int type, String id, String name) {
        createApiService();

        return loadVkPlaylist(type, id, name);
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

    //TODO: use strategy pattern to resolve this code more gracefully
    private Playlist loadVkPlaylist(int type, String id, String name){
        switch (type) {
            case VkPlaylist.ALL:
                return loadAlbum(0, 16, "", "My audios");

            case VkPlaylist.POPULAR:
                return loadPopular(0, 16);

            case VkPlaylist.RECOMMENDED:
                return loadRecommended(0, 16);

            case VkPlaylist.ALBUM:
                return loadAlbum(0, 16, id, name);

            default:
                return null;
        }
    }

    private Playlist loadAlbum(int offset, int count, String id, String name) {

        Call<VkAudioWrapper> callForAlbum =
                service.loadAudio(id, String.valueOf(offset), String.valueOf(count), userId, token);

        Response<VkAudioWrapper> responseAlbum = null;
        try {
            responseAlbum = callForAlbum.execute();
        } catch (IOException e) {
            Log.e(TAG, "Handled: " + Log.getStackTraceString(e));
        }

        ArrayList<VkAudioObject> vkAlbum =
                new ArrayList<>(Arrays.asList(responseAlbum.body().getResponse().getItems()));

        Playlist playlistAlbum = new Playlist();
        playlistAlbum.setName(name);

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkAlbum) {
            try {
                playlistAlbum.addSong(converter.convert(vkSong));
            } catch (IOException e) {
                Log.e(TAG, "Handled: " + Log.getStackTraceString(e));
            }
        }

        return playlistAlbum;
    }

    private Playlist loadPopular(int offset, int count) {
        Call<VkPopularCollection> callForPopular =
                service.loadPopularAudio(String.valueOf(offset), String.valueOf(count), token);

        Response<VkPopularCollection> responsePopular = null;
        try {
            responsePopular = callForPopular.execute();
        } catch (IOException e) {
            Log.e(TAG, "Handled: " + e.toString());
            e.printStackTrace();
        }

        ArrayList<VkAudioObject> vkPopular =
                new ArrayList<>(Arrays.asList(responsePopular.body().getResponse()));

        Playlist playlistPopular = new Playlist();
        playlistPopular.setName("Popular");

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkPopular) {
            try {
                playlistPopular.addSong(converter.convert(vkSong));
            } catch (IOException e) {
                Log.e(TAG, "Handled: " + e.toString());
                e.printStackTrace();
            }
        }

        return playlistPopular;
    }

    private Playlist loadRecommended(int offset, int count) {
        Call<VkAudioWrapper> callForRecommended =
                service.loadRecommendedAudio(String.valueOf(offset), String.valueOf(count), userId, token);

        Response<VkAudioWrapper> responseRecommended = null;
        try {
            responseRecommended = callForRecommended.execute();
        } catch (IOException e) {
            Log.e(TAG, "Handled: " + e.toString());
            e.printStackTrace();
        }

        ArrayList<VkAudioObject> vkRecommended =
                new ArrayList<>(Arrays.asList(responseRecommended.body().getResponse().getItems()));

        Playlist playlistRecommended = new Playlist();
        playlistRecommended.setName("Recommended");

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkRecommended) {
            try {
                playlistRecommended.addSong(converter.convert(vkSong));
            } catch (IOException e) {
                Log.e(TAG, "Handled: " + e.toString());
                e.printStackTrace();
            }
        }

        return playlistRecommended;
    }

    private void createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VkProfileHelper.VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(VkAPIService.class);
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

        public VkMusicHelper build() {
            return new VkMusicHelper(userId, token);
        }
    }
}
