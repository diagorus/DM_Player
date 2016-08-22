package com.dmplayer.internetservices;

import com.dmplayer.models.VkAlbumsResp;
import com.dmplayer.models.VkUserDataResp;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface APIService {
        @GET("users.get?")
        Call<VkUserDataResp> loadUserData(@QueryMap Map<String, String> options);

        @GET("audio.getCount?")
        Call<ResponseBody> loadSongsCount(@QueryMap Map<String, String> options);

        @GET("audio.getAlbums?")
        Call<VkAlbumsResp> loadAlbums(@QueryMap Map<String, String> options);
}