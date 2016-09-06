package com.dmplayer.models;

import android.os.AsyncTask;
import android.util.Log;

import com.dmplayer.internetservices.VkAPIService;
import com.dmplayer.models.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkProfileUserDataResponse.VkUserDataCollection;
import com.dmplayer.uicomponent.SwappingLayout.SwappingLayoutController;
import com.dmplayer.utility.VkSettings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkAccount implements ExternalMusicAccount {


    private final String userId;
    private final String token;
    private final SwappingLayoutController swappingLayoutController;

    private VkAPIService service;

    private ExternalProfileObject.Builder profileObjectBuilder;

    private static final String TAG = "VkAccount";

    private VkAccount(String userId, String token, SwappingLayoutController swappingLayoutController) {
        this.userId = userId;
        this.token = token;
        this.swappingLayoutController = swappingLayoutController;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public SwappingLayoutController getSwappingLayoutController() {
        return swappingLayoutController;
    }

    @Override
    public ExternalProfileObject loadProfile() {
        VkUserDataTask userDataTask = new VkUserDataTask();
        VkMusicDataTask musicDataTask = new VkMusicDataTask();

        profileObjectBuilder = new ExternalProfileObject.Builder();

        createApiService();

        swappingLayoutController.onLogInStarted();
        try {
            musicDataTask.execute().get();
            userDataTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        swappingLayoutController.onLogInFinished(profileObjectBuilder.build());

        return profileObjectBuilder.build();
    }

    @Override
    public List<Playlist> loadMusicLists() {
        return Collections.emptyList();
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

    private class VkMusicDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
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

            return null;
        }
    }

    private class VkUserDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
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
            return null;
        }
    }

    public static class Builder {
        private String userId;
        private String token;
        private SwappingLayoutController swappingLayoutController;

        public Builder setUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        public Builder setSwappingLayoutController(SwappingLayoutController swappingLayoutController) {
            this.swappingLayoutController = swappingLayoutController;

            return this;
        }

        public VkAccount build() {
            return new VkAccount(userId, token, swappingLayoutController);
        }
    }
}
