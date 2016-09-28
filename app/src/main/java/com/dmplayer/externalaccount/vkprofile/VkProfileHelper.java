package com.dmplayer.externalaccount.vkprofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dmplayer.bitmaploader.UriLoader;
import com.dmplayer.bitmaploader.UrlLoader;
import com.dmplayer.externalaccount.ExternalProfileHelper;
import com.dmplayer.externalaccount.ExternalProfileModel;
import com.dmplayer.internetservices.VkApiService;
import com.dmplayer.models.VkObjects.VkAlbumsResponse.VkAlbumsWrapper;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserData;
import com.dmplayer.models.VkObjects.VkProfileUserDataResponse.VkUserDataCollection;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.utility.dialogs.ProfileDialog;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VkProfileHelper implements ExternalProfileHelper {
    private boolean logged;
    private String userId;
    private String token;


    private Context context;
    private SharedPreferences prefs;

    public final static String VK_API_URL = "https://api.vk.com/method/";
    private final static String TAG = "VkProfileHelper";

    private VkProfileHelper(Context context, boolean logged , String userId, String token) {
        prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        this.context = context;
        this.logged = logged;
        this.userId = userId;
        this.token = token;
    }

    @Override
    public void logOut() {
        userId = null;
        token = null;

        eraseProfile();
        eraseAccessValues();
    }

    @Override
    public ExternalProfileModel loadProfileOnline() {
        VkProfileModel.Builder profileObjectBuilder = new VkProfileModel.Builder();
        VkApiService service = createApiService();

        loadVkUserData(service, profileObjectBuilder);
        loadVkMusicData(service, profileObjectBuilder);

        VkProfileModel model = profileObjectBuilder.build();

        saveProfile(model);
        saveAccessValues(token, userId);

        return model;
    }

    @Override
    public ExternalProfileModel loadProfileOffline() {
        return new VkProfileModel.Builder()
                .setNickname(prefs.getString("VK_USER_NICKNAME", ""))
                .setAlbumsCount(prefs.getString("VK_USER_ALBUMS_COUNT", ""))
                .setSongsCount(prefs.getString("VK_USER_SONGS_COUNT", ""))
                .setPhoto(new UriLoader().loadImage(context,
                        prefs.getString("VK_USER_PHOTO_URI", "")))
                .build();
    }

    private VkApiService createApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VK_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VkApiService.class);
    }

    private void loadVkMusicData(VkApiService service, VkProfileModel.Builder profileObjectBuilder) {
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

    private void loadVkUserData(VkApiService service, VkProfileModel.Builder profileObjectBuilder) {
        Call<VkUserDataCollection> callForUserData = service.loadUserData("photo_100", userId, token);

        try {
            Response<VkUserDataCollection> responseUserData = callForUserData.execute();

            VkUserData userData = responseUserData.body().getResponse()[0];

            profileObjectBuilder.addNicknamePart(userData.getFirst_name());
            profileObjectBuilder.addNicknamePart(" " + userData.getLast_name());
            profileObjectBuilder.setPhoto(new UrlLoader().loadImage(context,
                    userData.getPhoto_100()));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private void saveProfile(VkProfileModel profile) {
        DMPlayerUtility.saveBitmap(profile.getPhoto(), ProfileDialog.PHOTO_DIR_PATH, "vk_photo");

        prefs.edit()
                .putString("VK_USER_NICKNAME", profile.getNickname())
                .putString("VK_USER_SONGS_COUNT", profile.getSongsCount())
                .putString("VK_USER_ALBUMS_COUNT", profile.getAlbumsCount())
                .putString("VK_USER_PHOTO_URI", DMPlayerUtility.getUriFromPath(ProfileDialog.AVATAR_VK_FILE_PATH).toString())
                .apply();
    }

    private void saveAccessValues(String token, String userId) {
        prefs.edit()
                .putBoolean("VK_LOGGED", true)
                .putString("VK_ACCESS_TOKEN", token)
                .putString("VK_USER_ID", userId)
                .apply();
    }

    private void eraseProfile() {
        DMPlayerUtility.deleteFile(ProfileDialog.PHOTO_DIR_PATH + "vk_photo.jpg");

        prefs.edit()
                .remove("VK_USER_NICKNAME")
                .remove("VK_USER_SONGS_COUNT")
                .remove("VK_USER_ALBUMS_COUNT")
                .remove("VK_USER_PHOTO_URI")
                .apply();
    }

    private void eraseAccessValues() {
        prefs.edit()
                .remove("VK_LOGGED")
                .remove("VK_ACCESS_TOKEN")
                .remove("VK_USER_ID")
                .apply();
    }

    public boolean isLogged() {
        return logged;
    }

    public static class Builder {
        private Context context;
        private boolean logged;
        private String token;
        private String userId;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setLogged(boolean logged) {
            this.logged = logged;

            return this;
        }

        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;

            return this;
        }

        public VkProfileHelper build() {
            if (!logged && token == null && userId == null) {
                SharedPreferences prefs = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

                boolean logged = prefs.getBoolean("VK_LOGGED", false);;
                String token = prefs.getString("VK_ACCESS_TOKEN", "");
                String userId = prefs.getString("VK_USER_ID", "");

                return new VkProfileHelper(context, logged, userId, token);
            } else {
                return new VkProfileHelper(context, logged, userId, token);
            }

        }
    }
}