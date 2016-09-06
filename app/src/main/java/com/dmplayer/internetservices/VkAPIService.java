package com.dmplayer.internetservices;

import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkAudioGetResponce.VkAudioWrapper;
import com.dmplayer.models.VkObjects.VkPopularAudioResponce.VkPopularCollection;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserDataCollection;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface VkAPIService {
        @GET("users.get")
        Call<VkUserDataCollection> loadUserData(@QueryMap Map<String, String> options);

        @GET("audio.getCount")
        Call<ResponseBody> loadSongsCount(@QueryMap Map<String, String> options);

        @GET("audio.getAlbums")
        Call<VkAlbumsWrapper> loadAlbums(@QueryMap Map<String, String> options);

        @GET("audio.get")
        Call<VkAudioWrapper> loadAudio(@QueryMap Map<String, String> options);

        @GET("audio.getPopular")
        Call<VkPopularCollection> loadPopularAudio(@QueryMap Map<String, String> options);

        @GET("audio.getRecommendations")
        Call<VkAudioWrapper> loadRecommendedAudio(@QueryMap Map<String, String> options);
}