package com.dmplayer.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class VkSession {
    private SharedPreferences sharedPreferences;
    private final static String PREFS_NAME = "VALUES";
    private Context appContext;
    private SharedPreferences.Editor editor;

    public VkSession(){}

    public VkSession(Context context){
        appContext = context;
        sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAccessToken(String accessToken, String userId){
        editor.putString("VkAccessToken", accessToken);
        editor.putString("VkUserId", userId);
        editor.commit();
    }

    public String[] getAccessToken(){
        String[] params = new String[2];
        params[0] = sharedPreferences.getString("VkAccessToken", "");
        params[1] = sharedPreferences.getString("VkUserId", "");
        return params;
    }

    public void resetAccessToken(){
        editor.putString("VkAccessToken", "");
        editor.putString("VkUserId", "");
        editor.commit();
    }
}
