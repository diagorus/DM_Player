package com.dmplayer.helperservises;

import android.util.Log;

import com.dmplayer.converters.VkToSongDetailConverter;
import com.dmplayer.externalaccount.ExternalMusicLoader;
import com.dmplayer.internetservices.VkApiService;
import com.dmplayer.models.Playlist;
import com.dmplayer.models.VkObjects.VkAlbumObject;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkAudioGetResponce.VkAudioWrapper;
import com.dmplayer.models.VkObjects.VkAudioObject;
import com.dmplayer.models.VkObjects.VkPlaylist;
import com.dmplayer.models.VkObjects.VkPopularAudioResponce.VkPopularCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkMusicHelper implements ExternalMusicLoader {
    private boolean logged;
    private String userId;
    private String token;

    private final static String TAG = "VkMusicHelper";

    private VkMusicHelper(boolean logged,String userId, String token) {
        this.logged = logged;
        this.userId = userId;
        this.token = token;
    }

    @Override
    public List<Playlist> loadMusicListsToShow() {
        List<Playlist> playlistsToShow = new ArrayList<>();

        VkApiService service = createApiService();

        loadVkDefaultAlbums(playlistsToShow);
        try {
            loadVkUserAlbums(service, playlistsToShow);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return playlistsToShow;
    }

    @Override
    public Playlist loadMusicList(int type, String id, String name) {
        VkApiService service = createApiService();

        return loadVkPlaylist(service, type, id, name);
    }

    //TODO: use strategy pattern to resolve this code more gracefully
    private Playlist loadVkPlaylist(VkApiService service, int type, String id, String name){
        switch (type) {
            case VkPlaylist.ALL:
                return loadAlbum(service, 0, 100, "", "My audios");

            case VkPlaylist.POPULAR:
                return loadPopular(service, 0, 100);

            case VkPlaylist.RECOMMENDED:
                return loadRecommended(service, 0, 100);

            case VkPlaylist.ALBUM:
                return loadAlbum(service, 0, 100, id, name);

            default:
                return null;
        }
    }

    private void loadVkUserAlbums(VkApiService service, List<Playlist> playlistsToShow) throws IOException {
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

    private Playlist loadAlbum(VkApiService service, int offset, int count, String id, String name) {
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

    private Playlist loadPopular(VkApiService service, int offset, int count) {
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

    private Playlist loadRecommended(VkApiService service, int offset, int count) {
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

    private VkApiService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VkProfileHelper.VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VkApiService.class);
    }

    public boolean isLogged() {
        return logged;
    }

    public static class Builder {
        private boolean logged;
        private String userId;
        private String token;

        public Builder setLogged(boolean logged) {
            this.logged = logged;

            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        public VkMusicHelper build() {
            return new VkMusicHelper(logged, userId, token);
        }
    }
}
