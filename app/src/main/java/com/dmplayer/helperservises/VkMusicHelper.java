package com.dmplayer.helperservises;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmplayer.converters.VkToSongDetailConverter;
import com.dmplayer.internetservices.VkApiService;
import com.dmplayer.models.SongDetail;
import com.dmplayer.models.VkGenres;
import com.dmplayer.models.VkObjects.VkAlbumObject;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkAudioGetResponce.VkAudioWrapper;
import com.dmplayer.models.VkObjects.VkAudioObject;
import com.dmplayer.models.VkObjects.VkPopularAudioResponce.VkPopularCollection;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySingle;
import com.dmplayer.models.playlisitems.VkPlaylistItemInSeveral;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dmplayer.helperservises.VkProfileHelper.SP_ACCESS_TOKEN;
import static com.dmplayer.helperservises.VkProfileHelper.SP_LOGGED;
import static com.dmplayer.helperservises.VkProfileHelper.SP_USER_ID;

public class VkMusicHelper {
    private Context context;
    private boolean logged;
    private String userId;
    private String token;

    private SharedPreferences prefs;

    private final static String TAG = "VkMusicHelper";

    private VkMusicHelper(Context context, boolean logged, String userId, String token) {
        prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        this.context = context;
        this.logged = logged;
        this.userId = userId;
        this.token = token;
    }

    public boolean isLogged() {
        return logged;
    }

    public List<SongDetail> loadAlbum(int offset, int count, int id) throws IOException, NullPointerException {
        Call<VkAudioWrapper> callForAlbum = createApiService()
                .loadAudio((id == -1) ? "" : String.valueOf(id), String.valueOf(offset),
                        String.valueOf(count), userId, token);


        Response<VkAudioWrapper> responseAlbum = callForAlbum.execute();

        ArrayList<VkAudioObject> vkAlbum =
                new ArrayList<>(Arrays.asList(responseAlbum.body().getResponse().getItems()));

        List<SongDetail> playlist = new ArrayList<>();

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkAlbum) {
                playlist.add(converter.convert(vkSong));
        }

        return playlist;
    }

    public List<SongDetail> loadPopular(int offset, int count, int id) throws IOException, NullPointerException {
        Call<VkPopularCollection> callForPopular = createApiService()
                .loadPopularAudio(String.valueOf(offset),
                        String.valueOf(count), String.valueOf(id), token);

        Response<VkPopularCollection> responsePopular = callForPopular.execute();

        ArrayList<VkAudioObject> vkPopular =
                new ArrayList<>(Arrays.asList(responsePopular.body().getResponse()));

        List<SongDetail> playlist = new ArrayList<>();

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkPopular) {
                playlist.add(converter.convert(vkSong));
        }

        return playlist;
    }

    public List<SongDetail> loadRecommended(int offset, int count) throws IOException, NullPointerException{
        Call<VkAudioWrapper> callForRecommended = createApiService()
                .loadRecommendedAudio(String.valueOf(offset), String.valueOf(count), userId, token);

        Response<VkAudioWrapper> responseRecommended = callForRecommended.execute();

        ArrayList<VkAudioObject> vkRecommended =
                new ArrayList<>(Arrays.asList(responseRecommended.body().getResponse().getItems()));

        List<SongDetail> playlist = new ArrayList<>();

        VkToSongDetailConverter converter = new VkToSongDetailConverter();
        for (VkAudioObject vkSong : vkRecommended) {
            playlist.add(converter.convert(vkSong));
        }

        return playlist;
    }

    public List<VkPlaylistItemInSeveral> loadUserAlbums() throws IOException, NullPointerException {
        Call<VkAlbumsWrapper> callForAlbums = createApiService()
                .loadAlbums("0", "100", userId, token);
        Response<VkAlbumsWrapper> responseAlbums = callForAlbums.execute();

        ArrayList<VkAlbumObject> vkAllAlbums = new ArrayList<>(
                Arrays.asList(responseAlbums.body().getResponse().getItems()));

        List<VkPlaylistItemInSeveral> userPlaylists = new ArrayList<>();
        for (VkAlbumObject vkAlbum : vkAllAlbums) {
            userPlaylists.add(new VkPlaylistItemInSeveral(Integer.valueOf(vkAlbum.getId()),
                    vkAlbum.getTitle(), VkPlaylistCategorySingle.ALBUM));
        }

        return userPlaylists;
    }

    public List<VkPlaylistItemInSeveral> loadPopularGenres() {
        List<VkPlaylistItemInSeveral> popularGenres = new ArrayList<>();
        for (VkGenres genre : VkGenres.values()) {
            popularGenres.add(new VkPlaylistItemInSeveral(genre.getId(), genre.getName(),
                    VkPlaylistCategorySingle.GENRE));
        }

        return popularGenres;
    }

    private VkApiService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VkProfileHelper.VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VkApiService.class);
    }

    public static class Builder {
        private Context context;
        private boolean logged;
        private String userId;
        private String token;

        public Builder(Context context) {
            this.context = context;
        }

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
            if (!logged && token == null && userId == null) {
                SharedPreferences prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

                boolean logged = prefs.getBoolean(SP_LOGGED, false);;
                String token = prefs.getString(SP_ACCESS_TOKEN, "");
                String userId = prefs.getString(SP_USER_ID, "");

                return new VkMusicHelper(context, logged, userId, token);
            } else {
                return new VkMusicHelper(context, logged, userId, token);
            }
        }
    }
}
